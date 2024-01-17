package sk.stuba.fei.uim.dp.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.utils.ParticipantItemDiffCallback

data class ParticipantItem(val name: String)

class ParticipantsAdapter : RecyclerView.Adapter<ParticipantsAdapter.ParticipantsViewHolder>(){
    private var items: MutableList<ParticipantItem> = mutableListOf()
    class ParticipantsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.participant_item, parent, false)
        return ParticipantsViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.name).text = items[position].name
    }

    fun updateItems(newItems: MutableList<ParticipantItem>) {
        val diffCallback = ParticipantItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun addItem(newItem: ParticipantItem){
        items.add(newItem)
        notifyItemInserted(items.size-1)
    }
}