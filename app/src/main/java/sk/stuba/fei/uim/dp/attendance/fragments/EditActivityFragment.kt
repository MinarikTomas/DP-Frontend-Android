package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
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
                viewModel.updateActivity(args.activity.id)
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
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}