package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentSignupBinding
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
                    Log.d("SignupFragment", viewModel.email.value.toString())
//                    if(viewModel.email.value.isNullOrEmpty()){
//                        bnd.textInputLayoutEmail.error = "Email cannot be empty"
//                    }
                    it.findNavController().navigate(R.id.action_signup_to_add_card)

                }
            }

            viewModel.signupResult.observe(viewLifecycleOwner){
                if(it.isNotEmpty()){
                    Snackbar.make(
                        view.findViewById(R.id.btn_continue),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            bnd.email.addTextChangedListener {
                bnd.textInputLayoutEmail.isErrorEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}