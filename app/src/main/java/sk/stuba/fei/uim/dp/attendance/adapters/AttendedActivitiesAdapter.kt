package sk.stuba.fei.uim.dp.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.utils.AttendedActivityItemDiffCallback

data class AttendedActivityItem(val id: Int, val name: String, val datetime: String)

class AttendedActivitiesAdapter : RecyclerView.Adapter<AttendedActivitiesAdapter.AttendedActivitiesViewHolder>(){
    var items: List<AttendedActivityItem> = emptyList()

    class AttendedActivitiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendedActivitiesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attended_activity_item, parent, false)
        return AttendedActivitiesViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: AttendedActivitiesViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.name).text = items[position].name
        holder.itemView.findViewById<TextView>(R.id.date).text = items[position].datetime
    }

    fun updateItems(newItems: List<AttendedActivityItem>){
        val diffCallback = AttendedActivityItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }
}