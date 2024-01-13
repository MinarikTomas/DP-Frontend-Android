package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentAddActivityBinding
import sk.stuba.fei.uim.dp.attendance.viewmodels.AddActivityViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AddActivityFragment : Fragment(R.layout.fragment_add_activity) {

    private var binding: FragmentAddActivityBinding ?= null
    private lateinit var viewModel: AddActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddActivityViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AddActivityViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddActivityBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val datePicker = MaterialDatePicker
                .Builder
                .datePicker()
                .setTitleText("Select a date")
                .build()
            val timePicker = MaterialTimePicker
                .Builder()
                .setTitleText("Select a time")
                .setTimeFormat(CLOCK_24H)
                .build()

            bnd.btnDate.apply {
                setOnClickListener {
                    datePicker.show(parentFragmentManager, "DATE_PICKER")
                }
            }
            datePicker.addOnPositiveButtonClickListener {
                timePicker.show(parentFragmentManager, "TIME_PICKER")
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                viewModel.date.value = sdf.format(it)
            }
            timePicker.addOnPositiveButtonClickListener {
                viewModel.time.value = timePicker.hour.toString() + ":" +
                        when(timePicker.minute.toString().length < 2){
                            true -> "0" + timePicker.minute
                            false -> timePicker.minute
                        }
            }

            bnd.btnSave.apply {
                setOnClickListener {
                    viewModel.addActivity(
                        PreferenceData.getInstance().getUser(requireContext())?.id ?: -1
                    )
                }
            }

            viewModel.addActivityResult.observe(viewLifecycleOwner){
                if(it.isNotEmpty()){
                    Snackbar.make(
                        view.findViewById(R.id.btn_save),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }else{
                    requireView().findNavController().navigate(R.id.action_add_activity_home)
                }
            }
        }
    }
}