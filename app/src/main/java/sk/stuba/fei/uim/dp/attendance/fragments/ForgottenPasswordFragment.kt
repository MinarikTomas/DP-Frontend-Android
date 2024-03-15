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
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentForgottenPasswordBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.LoginViewModel

class ForgottenPasswordFragment : Fragment(R.layout.fragment_forgotten_password) {
    private var binding: FragmentForgottenPasswordBinding? = null
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

        binding = FragmentForgottenPasswordBinding.bind(view).apply {
            model = viewModel
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.btnSend.apply {
                setOnClickListener {
                    if(isInputValid()){
                        viewModel.resetPassword()
                    }
                }
            }

            viewModel.resetPasswordResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_send),
                            "Email sent",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        requireView().findNavController().navigate(R.id.action_forgotten_password_login)
                    }else{
                        Snackbar.make(
                            view.findViewById(R.id.btn_send),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            bnd.textInputLayoutEmail.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutEmail)
            )
        }
    }

    private fun isInputValid(): Boolean{
        if(!Patterns.EMAIL_ADDRESS.matcher(viewModel.email.value.toString()).matches()) {
            binding!!.textInputLayoutEmail.isErrorEnabled = true
            binding!!.textInputLayoutEmail.error = "Wrong format"
            return false
        }
        return true
    }
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}