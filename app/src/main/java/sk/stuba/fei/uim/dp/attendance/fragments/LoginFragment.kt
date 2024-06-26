package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.config.AppConfig.SERVER_CLIENT_ID
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentLoginBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.LoginViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var binding: FragmentLoginBinding?= null
    private lateinit var viewModel: LoginViewModel
    private val TAG = "LoginFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[LoginViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val credentialManager = CredentialManager.create(requireContext())

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
                        viewModel.loginUser(requireContext())
                    }
                }
            }

            bnd.forgotPassword.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_login_forgotten_password)
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
                    if (user.hasCard == true){
                        PreferenceData.getInstance().putUser(requireContext(), user)
                        viewModel.clear()
                        requireView().findNavController().navigate(R.id.action_login_home)
                    }else{
                        requireView().findNavController().navigate(LoginFragmentDirections.actionLoginAddCard(user))
                    }
                }
            }

            bnd.textInputLayoutEmail.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutEmail)
            )

            bnd.textInputLayoutPassword.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutPassword))

            bnd.btnGoogle.setOnClickListener {
                val googleIdOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(SERVER_CLIENT_ID)
                    .build()

                val request: GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .setPreferImmediatelyAvailableCredentials(false)
                    .build()

                lifecycleScope.launch {
                    try {
                        val result = credentialManager.getCredential(
                            request = request,
                            context = requireContext(),
                        )
                        handleSignIn(result)
                    } catch (e: GetCredentialException) {
                        Log.e(TAG, e.message.toString())
                    }
                }
            }
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        Log.d(TAG, googleIdTokenCredential.idToken)
                        viewModel.googleLogin(googleIdTokenCredential.idToken, requireContext())
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
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