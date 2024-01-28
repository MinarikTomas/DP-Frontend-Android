package sk.stuba.fei.uim.dp.attendance.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.fei.uim.dp.attendance.databinding.CardItemBinding
import sk.stuba.fei.uim.dp.attendance.utils.CardItemDiffCallback

data class CardItem(val name: String, val id: Int)
class CardsAdapter : RecyclerView.Adapter<CardsAdapter.CardsViewHolder>(){
    private var items: MutableList<CardItem> = mutableListOf()
    private var onClickDeleteListener: OnClickListener? = null
    private var onClickEditSaveListener: OnClickListener? = null
    inner class CardsViewHolder(val binding: CardItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {
        val binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardsViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.binding.name.text = items[position].name
        holder.binding.nameEdit.editText?.setText(items[position].name)
        holder.binding.delete.setOnClickListener {
            if(onClickDeleteListener != null){
                onClickDeleteListener!!.onClick(position, items[position])
            }
        }
        holder.binding.edit.setOnClickListener {
            holder.binding.edit.visibility = View.INVISIBLE
            holder.binding.name.visibility = View.INVISIBLE
            holder.binding.save.visibility = View.VISIBLE
            holder.binding.nameEdit.visibility = View.VISIBLE
        }
        holder.binding.save.setOnClickListener {
            Log.d("CardsAdapter", holder.binding.nameEdit.editText?.text.toString())
            if(onClickEditSaveListener != null){
                onClickEditSaveListener!!.onClick(position,
                    CardItem(holder.binding.nameEdit.editText?.text.toString(), items[position].id))
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

    fun updateItem(position: Int, item: CardItem){
        items[position] = item
        notifyItemChanged(position)
    }

    fun setOnClickDeleteListener(onClickListener: OnClickListener){
        this.onClickDeleteListener = onClickListener
    }

    fun setOnClickEditSaveListener(onClickListener: OnClickListener){
        this.onClickEditSaveListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: CardItem)
    }
}