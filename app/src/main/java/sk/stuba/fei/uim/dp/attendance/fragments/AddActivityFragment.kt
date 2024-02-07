package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentAddActivityBinding
import sk.stuba.fei.uim.dp.attendance.utils.DisableErrorTextWatcher
import sk.stuba.fei.uim.dp.attendance.viewmodels.ActivityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddActivityFragment : Fragment(R.layout.fragment_add_activity) {

    private var binding: FragmentAddActivityBinding ?= null
    private lateinit var viewModel: ActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActivityViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ActivityViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PreferenceData.getInstance().getUser(requireContext()) == null){
            requireView().findNavController().navigate(R.id.action_to_login)
        }
        binding = FragmentAddActivityBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            viewModel.clearBinds()
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -1)
            val constraintsBuilder = CalendarConstraints.Builder()
            val dateValidatorMin = DateValidatorPointForward.from(calendar.timeInMillis)
            constraintsBuilder.setValidator(dateValidatorMin)
            val datePicker = MaterialDatePicker
                .Builder
                .datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
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
                viewModel.time.value =
                    when(timePicker.hour.toString().length < 2){
                        true -> "0" + timePicker.hour
                        false -> timePicker.hour.toString()
                    } + ":" +
                    when(timePicker.minute.toString().length < 2){
                        true -> "0" + timePicker.minute
                        false -> timePicker.minute
                    }
            }

            bnd.btnSave.apply {
                setOnClickListener {
                    if(areAllFieldFilled()){
                        viewModel.addActivity(
                            PreferenceData.getInstance().getUser(requireContext())?.id ?: -1,
                            bnd.repeatCheckbox.isChecked
                        )
                    }
                }
            }

            bnd.repeatCheckbox.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked){
                        bnd.weeks.visibility = View.VISIBLE
                    }else{
                        bnd.weeks.visibility = View.GONE
                    }
                }
            }

            viewModel.addActivityResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.btn_save),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }else{
                        viewModel.clearBinds()
                        requireView().findNavController().navigate(R.id.action_add_activity_home)
                    }
                }
            }

            bnd.textInputLayoutName.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutName)
            )
            bnd.textInputLayoutLocation.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.textInputLayoutLocation)
            )
            bnd.weeks.editText?.addTextChangedListener(
                DisableErrorTextWatcher(bnd.weeks)
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
        if(binding!!.repeatCheckbox.isChecked && viewModel.weeks.value.isNullOrEmpty()){
            binding!!.weeks.isErrorEnabled = true
            binding!!.weeks.error = "Cannot be empty"
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