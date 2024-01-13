package sk.stuba.fei.uim.dp.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.dp.attendance.R

data class MyItem(val theView: Int, val id: Number, val name: String, val location: String, val time: String, val date: String)
class ActivitiesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val THE_DATE_VIEW = 1
        const val THE_ACTIVITY_VIEW = 2
    }

    private var items: List<MyItem> = listOf()

    private inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){
            itemView.findViewById<TextView>(R.id.item_date).text = items[position].date
        }
    }

    private inner class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){
            itemView.findViewById<TextView>(R.id.item_title).text = items[position].name
            itemView.findViewById<TextView>(R.id.item_location).text = items[position].location
            itemView.findViewById<TextView>(R.id.item_time).text = items[position].time
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
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].theView
    }

    fun updateItems(newItems: List<MyItem>){
        items = newItems
        notifyDataSetChanged()
    }

}