package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.adapters.ActivitiesAdapter
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentHomeBinding
import sk.stuba.fei.uim.dp.attendance.utils.SpacesItemDecoration
import sk.stuba.fei.uim.dp.attendance.viewmodels.ActivityViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding ?= null
    private lateinit var viewModel: ActivityViewModel
    private var activities = emptyList<Activity>()

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
        binding = FragmentHomeBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            viewModel.getCreatedActivities(PreferenceData.getInstance().getUser(requireContext())?.id ?: -1)

            val recyclerView = bnd.recyclerviewActivities
            recyclerView.layoutManager = LinearLayoutManager(context)
            val activitiesAdapter = ActivitiesAdapter()
            recyclerView.adapter = activitiesAdapter
            recyclerView.addItemDecoration(
                SpacesItemDecoration(20)
            )

            bnd.upcoming.isChecked = PreferenceData.getInstance().getIsUpcomingSelected(requireContext())
            bnd.past.isChecked = !PreferenceData.getInstance().getIsUpcomingSelected(requireContext())

            viewModel.getActivitiesResult.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let {
                    if (it.isNotEmpty()) {
                        Snackbar.make(
                            view.findViewById(R.id.bottom_bar),
                            it,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            viewModel.activities.observe(viewLifecycleOwner){
                it.getContentIfNotHandled()?.let {
                    Log.d("HomeFragment", "activities observer")
                    if(it.isNotEmpty()){
                        activities = it
                        if(it.filter { it.startTime.isNotEmpty() && it.endTime.isEmpty() }.isNotEmpty()){
                            PreferenceData.getInstance().putIsActivityRunning(requireContext(), true)
                        }
                        val filteredActivities = if(bnd.upcoming.isChecked){
                            it.filter { it.endTime.isEmpty() }
                        }else{
                            it.filter { it.endTime.isNotEmpty() }
                        }
                        activitiesAdapter.updateItems(filteredActivities)
                    }
                }
            }

            bnd.upcoming.setOnCheckedChangeListener { _, isChecked ->
                PreferenceData.getInstance().putIsUpcomingSelected(requireContext(),isChecked)
                val filteredActivities = if(isChecked){
                    activities.filter { it.endTime.isEmpty() }
                }else{
                    activities.filter { it.endTime.isNotEmpty() }
                }
                activitiesAdapter.updateItems(filteredActivities)
            }
        }
    }
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}