package sk.stuba.fei.uim.dp.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.fragments.HomeFragmentDirections
import sk.stuba.fei.uim.dp.attendance.utils.ActivityItemDiffCallback
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ActivityItem(val theView: Int, val activity: Activity)
class ActivitiesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val THE_DATE_VIEW = 1
        const val THE_ACTIVITY_VIEW = 2
    }

    private var items: List<ActivityItem> = listOf()

    private inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){
            val date = LocalDate.parse(items[position].activity.date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            if (date.isEqual(LocalDate.now())){
                itemView.findViewById<TextView>(R.id.item_date).text = "Today"
            }else if(date.plusDays(1).isEqual(LocalDate.now())){
                itemView.findViewById<TextView>(R.id.item_date).text = "Tomorrow"
            }else{
                itemView.findViewById<TextView>(R.id.item_date).text = items[position].activity.date
            }
        }
    }

    private inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){
            itemView.findViewById<TextView>(R.id.item_title).text = items[position].activity.name
            itemView.findViewById<TextView>(R.id.item_location).text = items[position].activity.location
            itemView.findViewById<TextView>(R.id.item_time).text = items[position].activity.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == THE_DATE_VIEW){
            return DateViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.date_item, parent, false)
            )
        }
        return ActivityViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(items[position].theView == THE_DATE_VIEW){
            (holder as DateViewHolder).bind(position)
        }else {
            (holder as ActivityViewHolder).bind(position)
            holder.itemView.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeActivity(items[position].activity.id.toInt())
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].theView
    }

    fun updateItems(activities: List<Activity>) {
        val newItems = ArrayList<ActivityItem>()
        activities.forEachIndexed { index, activity ->
            if(index == 0 || activity.date != activities[index-1].date){
                newItems.add(ActivityItem(
                    ActivitiesAdapter.THE_DATE_VIEW,
                    activity))
            }
            newItems.add(ActivityItem(
                ActivitiesAdapter.THE_ACTIVITY_VIEW,
                activity
            ))
        }
        val diffCallback = ActivityItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

}