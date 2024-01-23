package sk.stuba.fei.uim.dp.attendance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.dp.attendance.R
import sk.stuba.fei.uim.dp.attendance.utils.CardItemDiffCallback

data class CardItem(val name: String, val id: Int)
class CardsAdapter : RecyclerView.Adapter<CardsAdapter.CardsViewHolder>(){
    private var items: MutableList<CardItem> = mutableListOf()
    private var onClickListener: OnClickListener? = null
    class CardsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)
        return CardsViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.name).text = items[position].name
        holder.itemView.findViewById<ImageView>(R.id.delete).setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position, items[position])
            }
        }
    }

    fun updateItems(newItems: MutableList<CardItem>) {
        val diffCallback = CardItemDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteItem(position: Int){
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size-position)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: CardItem)
    }
}