package sk.stuba.fei.uim.dp.attendance.fragments

import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.config.AppConfig
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentLoginBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.LoginViewModel
import java.lang.Exception

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var binding: FragmentLoginBinding?= null
    private lateinit var viewModel: LoginViewModel
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[LoginViewModel::class.java]

        oneTapClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(AppConfig.SERVER_CLIENT_ID)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .build()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val user = PreferenceData.getInstance().getUser(requireContext())
            if(user != null){
                requireView().findNavController().navigate(R.id.action_login_home)
            }
            bnd.signup.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_login_signup)
                }
            }
            
            bnd.btnLogin.apply { 
                setOnClickListener {
                    if(areAllFieldsFilled()){
                        viewModel.loginUser()
                    }
                }
            }

            viewModel.loginResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_login),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.userResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {user ->
                    PreferenceData.getInstance().putUser(requireContext(), user)
                    requireView().findNavController().navigate(R.id.action_login_home)
                }
            }

            bnd.textInputLayoutEmail.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutEmail)
            )

            bnd.textInputLayoutPassword.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutPassword))

            val activityResultLauncher = registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    try {
                        val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                        val idToken = credential.googleIdToken
                        when {
                            idToken != null -> {
                                // Got an ID token from Google. Use it to authenticate
                                // with your backend.
                                Log.d("LoginFragment", "Got ID token." + idToken)
                            }

                            else -> {
                                // Shouldn't happen.
                                Log.d("LoginFragment", "No ID token or password!")
                            }
                        }
                    }catch(e: Exception){
                        Log.e("LoginFragment", e.message.toString())
                    }
                }
            }
            bnd.google.setOnClickListener {
                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(requireActivity()) { result ->
                        try {
                            val intentSender = IntentSenderRequest.Builder(
                                result.pendingIntent.intentSender
                            ).build()
                            activityResultLauncher.launch(intentSender)
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e("LoginFragment", "Couldn't start One Tap UI: ${e.localizedMessage}")
                        }
                    }
                    .addOnFailureListener(requireActivity()) { e ->
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d("LoginFragment", e.localizedMessage)
                    }
            }
        }
    }

    private fun areAllFieldsFilled(): Boolean{
        var areFilled = true
        if(viewModel.email.value.isNullOrEmpty()){
            binding!!.textInputLayoutEmail.isErrorEnabled = true
            binding!!.textInputLayoutEmail.error = "Cannot be empty"
            areFilled = false
        }
        if(viewModel.password.value.isNullOrEmpty()){
            binding!!.textInputLayoutPassword.isErrorEnabled = true
            binding!!.textInputLayoutPassword.error = "Cannot be empty"
        }
        return areFilled
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}