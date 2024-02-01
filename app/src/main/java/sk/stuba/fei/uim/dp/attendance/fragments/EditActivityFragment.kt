package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentEditActivityBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.ActivityViewModel
import sk.stuba.fei.uim.dp.attendance.viewmodels.EditActivityViewModel

class EditActivityFragment : Fragment(R.layout.fragment_edit_activity) {
    private var binding: FragmentEditActivityBinding ?= null
    private lateinit var viewModel: EditActivityViewModel

    private val args: EditActivityFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditActivityViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[EditActivityViewModel::class.java]

        viewModel.setData(args.activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditActivityBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd->
            bnd.btnSave.setOnClickListener {
                if(areAllFieldFilled()){
                    viewModel.updateActivity(args.activity.id)
                }
            }

            viewModel.editActivityResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_save),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        requireView().findNavController().navigate(EditActivityFragmentDirections.actionEditToActivity(args.activity.id))
                    }
                }
            }

            bnd.textInputLayoutName.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutName)
            )
            bnd.textInputLayoutLocation.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutLocation)
            )
        }
    }

    private fun areAllFieldFilled(): Boolean{
        var areFilled = true
        if (viewModel.name.value.isNullOrEmpty()){
            binding!!.textInputLayoutName.isErrorEnabled = true
            binding!!.textInputLayoutName.error = "Cannot be empty"
            areFilled = false
        }
        if(viewModel.location.value.isNullOrEmpty()){
            binding!!.textInputLayoutLocation.isErrorEnabled = true
            binding!!.textInputLayoutLocation.error = "Cannot be empty"
            areFilled = false
        }
        if(viewModel.date.value.isNullOrEmpty()){
            Snackbar.make(
                requireView(),
                "Date not selected",
                Snackbar.LENGTH_SHORT
            ).show()
            areFilled = false
        }
        if(viewModel.time.value.isNullOrEmpty()){
            Snackbar.make(
                requireView(),
                "Time not selected",
                Snackbar.LENGTH_SHORT
            ).show()
            areFilled = false
        }
        return areFilled
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}