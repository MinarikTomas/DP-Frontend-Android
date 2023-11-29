package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentSignupBinding
import sk.stuba.fei.uim.dp.attendance.viewmodels.AuthViewModel

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private var binding: FragmentSignupBinding ?= null
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]
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
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}