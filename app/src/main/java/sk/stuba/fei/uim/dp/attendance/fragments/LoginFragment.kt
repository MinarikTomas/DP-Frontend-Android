package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentLoginBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.LoginViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var binding: FragmentLoginBinding?= null
    private lateinit var viewModel: LoginViewModel

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