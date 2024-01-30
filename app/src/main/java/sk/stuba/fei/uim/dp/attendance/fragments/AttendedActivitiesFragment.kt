package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.adapters.AttendedActivitiesAdapter
import sk.stuba.fei.uim.dp.attendance.adapters.AttendedActivityItem
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentAttendedActivitiesBinding
import sk.stuba.fei.uim.dp.attendance.viewmodels.AttendedActivitiesViewModel

class AttendedActivitiesFragment : Fragment(R.layout.fragment_attended_activities) {

    private var binding: FragmentAttendedActivitiesBinding ?= null
    private lateinit var viewModel: AttendedActivitiesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AttendedActivitiesViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AttendedActivitiesViewModel::class.java]

        viewModel.getAttendedActivities(
            PreferenceData.getInstance().getUser(requireContext())?.id ?: -1
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAttendedActivitiesBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            val recyclerView = bnd.recyclerviewAttendedActivities
            recyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = AttendedActivitiesAdapter()
            recyclerView.adapter = adapter
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )

            viewModel.getAttendedActivitiesResult.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        Snackbar.make(
                            view.findViewById(R.id.recyclerview_attended_activities),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.attendedActivities.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    if(it.isNotEmpty()){
                        adapter.updateItems(it.map { AttendedActivityItem(
                            it.id,
                            it.name,
                            it.date + " " + it.time
                        ) })
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