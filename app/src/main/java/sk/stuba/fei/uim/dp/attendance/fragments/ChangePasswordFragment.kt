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
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentChangePasswordBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.ProfileViewModel

class ChangePasswordFragment: Fragment(R.layout.fragment_change_password) {
    private var binding: FragmentChangePasswordBinding? = null
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PreferenceData.getInstance().getUser(requireContext()) == null){
            requireView().findNavController().navigate(R.id.action_to_login)
        }
        binding = FragmentChangePasswordBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.btnSave.setOnClickListener {
                if (areAllFieldsFilled() && isInputValid()){
                    viewModel.changePassword(
                        PreferenceData.getInstance().getUser(requireContext())?.id ?: -1
                    )
                }
            }

            viewModel.changePasswordResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if (it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_save),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        Snackbar.make(
                            view.findViewById(R.id.btn_save),
                            "Successfully changed password",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        requireView().findNavController().navigate(R.id.action_change_password_profile)
                    }
                }
            }

            bnd.newPassword.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.newPassword)
            )

            bnd.repeatPassword.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.repeatPassword)
            )
        }
    }

    private fun areAllFieldsFilled(): Boolean{
        var isFilled = true
        if (viewModel.newPassword.value.isNullOrEmpty()){
            binding!!.newPassword.error = "Cannot be empty"
            isFilled = false
        }
        if(viewModel.repeatPassword.value.isNullOrEmpty()){
            binding!!.repeatPassword.error = "Cannot be empty"
            isFilled = false
        }
        return isFilled
    }

    private fun isInputValid(): Boolean{
        var isValid = true
        if(viewModel.newPassword.value!!.length < 6){
            binding!!.newPassword.error = "Must be 6 or more characters"
            isValid = false
        }
        if(viewModel.newPassword.value != viewModel.repeatPassword.value){
            binding!!.repeatPassword.error = "Passwords do not match"
            isValid = false
        }
        return isValid
    }
}