package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentSignupBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.SignupViewModel

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private var binding: FragmentSignupBinding ?= null
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignupViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[SignupViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.login.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_signup_login)
                }
            }

            bnd.btnContinue.apply {
                setOnClickListener {
                    if(areAllFieldsFilled() && isInputValid()){
                        it.findNavController().navigate(R.id.action_signup_to_add_card)
                    }
                }
            }

            viewModel.signupResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_continue),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            bnd.textInputLayoutEmail.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutEmail))

            bnd.textInputLayoutName.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutName))

            bnd.textInputLayoutPassword.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutPassword))

            bnd.textInputLayoutConfirmPassword.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutConfirmPassword))
        }
    }

    private fun areAllFieldsFilled(): Boolean{
        var areFilled = true
        if(viewModel.email.value.isNullOrEmpty()){
            binding!!.textInputLayoutEmail.isErrorEnabled = true
            binding!!.textInputLayoutEmail.error = "Cannot be empty"
            areFilled = false
        }
        if(viewModel.fullName.value.isNullOrEmpty()){
            binding!!.textInputLayoutName.isErrorEnabled = true
            binding!!.textInputLayoutName.error = "Cannot be empty"
            areFilled = false
        }
        if(viewModel.password.value.isNullOrEmpty()){
            binding!!.textInputLayoutPassword.isErrorEnabled = true
            binding!!.textInputLayoutPassword.error = "Cannot be empty"
            areFilled = false
        }
        if(viewModel.repeatPassword.value.isNullOrEmpty()){
            binding!!.textInputLayoutConfirmPassword.isErrorEnabled = true
            binding!!.textInputLayoutConfirmPassword.error = "Cannot be empty"
            areFilled = false
        }
        return areFilled
    }

    private fun isInputValid(): Boolean{
        var isValid = true
        if(!Patterns.EMAIL_ADDRESS.matcher(viewModel.email.value.toString()).matches()){
            binding!!.textInputLayoutEmail.isErrorEnabled = true
            binding!!.textInputLayoutEmail.error = "Wrong format"
            isValid = false
        }
        if(viewModel.password.value!!.length < 6){
            binding!!.textInputLayoutPassword.isErrorEnabled = true
            binding!!.textInputLayoutPassword.error = "Must be 6 or more characters"
            isValid = false
        }
        if (viewModel.password.value != viewModel.repeatPassword.value){
            binding!!.textInputLayoutConfirmPassword.isErrorEnabled = true
            binding!!.textInputLayoutConfirmPassword.error = "Passwords do not match"
            isValid = false
        }
        return isValid
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}