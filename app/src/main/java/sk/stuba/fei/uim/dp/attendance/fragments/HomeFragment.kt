package sk.stuba.fei.uim.dp.attendance.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.adapters.ActivitiesAdapter
import sk.stuba.fei.uim.dp.attendance.adapters.MyItem
import sk.stuba.fei.uim.dp.attendance.utils.SpacesItemDecoration

class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataList = ArrayList<MyItem>()
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "D104", title = "Functional Programming", time = "10:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "AB300", title = "Team Project 1", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "31.10.2024", location = "AB300", title = "Network encryption", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "1.11.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", title = "Functional Programming", time = "10:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", title = "Team Project 1", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "D104", title = "Functional Programming", time = "10:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "AB300", title = "Team Project 1", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "31.10.2024", location = "AB300", title = "Network encryption", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "1.11.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", title = "Functional Programming", time = "10:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", title = "Team Project 1", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "D104", title = "Functional Programming", time = "10:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "30.10.2024", location = "AB300", title = "Team Project 1", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "30.10.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "31.10.2024", location = "AB300", title = "Network encryption", time = "12:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_DATE_VIEW, date = "1.11.2024", location = "", time = "", title = ""))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", title = "Functional Programming", time = "10:00"))
        dataList.add(MyItem(ActivitiesAdapter.THE_ACTIVITY_VIEW, date = "1.11.2024", location = "AB300", title = "Team Project 1", time = "12:00"))

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_activities)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val activitiesAdapter = ActivitiesAdapter()
        recyclerView.adapter = activitiesAdapter
        recyclerView.addItemDecoration(
            SpacesItemDecoration(20)
        )
        activitiesAdapter.updateItems(dataList)

    }


}