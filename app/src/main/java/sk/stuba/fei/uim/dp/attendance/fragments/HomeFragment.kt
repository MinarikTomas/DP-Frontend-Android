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
import sk.stuba.fei.uim.dp.attendance.adapters.MyItem
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.databinding.FragmentHomeBinding
import sk.stuba.fei.uim.dp.attendance.utils.SpacesItemDecoration
import sk.stuba.fei.uim.dp.attendance.viewmodels.AddActivityViewModel
import sk.stuba.fei.uim.dp.attendance.viewmodels.HomeViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding ?= null
    private lateinit var viewModel: HomeViewModel
    private lateinit var addActivityViewModel: AddActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[HomeViewModel::class.java]

        addActivityViewModel = ViewModelProvider(requireActivity(), object: ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AddActivityViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AddActivityViewModel::class.java]
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
            addActivityModel = addActivityViewModel
        }.also { bnd ->
            bnd.title.apply {
                setOnClickListener {
                    PreferenceData.getInstance().clearData(requireContext())
                    requireView().findNavController().navigate(R.id.action_home_login)
                }
            }

            val recyclerView = bnd.recyclerviewActivities
            recyclerView.layoutManager = LinearLayoutManager(context)
            val activitiesAdapter = ActivitiesAdapter()
            recyclerView.adapter = activitiesAdapter
            recyclerView.addItemDecoration(
                SpacesItemDecoration(20)
            )

            viewModel.getCreatedActivities(PreferenceData.getInstance().getUser(requireContext())?.id ?: -1)

            viewModel.getActivitiesResult.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        view.findViewById(R.id.bottom_bar),
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.activities.observe(viewLifecycleOwner){
                it?.let {
                    if(it.isNotEmpty()){
                       val items = ArrayList<MyItem>()
                       it.forEachIndexed { index, activity ->
                           if(index == 0 || activity.date != it[index-1].date){
                               items.add(MyItem(
                                   ActivitiesAdapter.THE_DATE_VIEW,
                                   -1,
                                   "",
                                   "",
                                   "",
                                   activity.date))
                           }
                           items.add(MyItem(
                               ActivitiesAdapter.THE_ACTIVITY_VIEW,
                               activity.id,
                               activity.name,
                               activity.location,
                               activity.time,
                               activity.date
                           ))
                       }
                       activitiesAdapter.updateItems(items)
                    }
                }
            }

            addActivityViewModel.addActivityResult.observe(viewLifecycleOwner){
                if(it.isEmpty()){
                    Log.d("HomeFragment", "activty created")
                }
            }
        }

//        val dataList = ArrayList<MyItem>()
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "D104", name = "Functional Programming", time = "10:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "AB300", name = "Team Project 1", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "31.10.2024", location = "AB300", name = "Network encryption", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "1.11.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", name = "Functional Programming", time = "10:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", name = "Team Project 1", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "D104", name = "Functional Programming", time = "10:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "AB300", name = "Team Project 1", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "31.10.2024", location = "AB300", name = "Network encryption", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "1.11.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", name = "Functional Programming", time = "10:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", name = "Team Project 1", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "D104", name = "Functional Programming", time = "10:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "AB300", name = "Team Project 1", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "31.10.2024", location = "AB300", name = "Network encryption", time = "12:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "1.11.2024", location = "", time = "", name = ""))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", name = "Functional Programming", time = "10:00"))
//        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", name = "Team Project 1", time = "12:00"))



    }


}