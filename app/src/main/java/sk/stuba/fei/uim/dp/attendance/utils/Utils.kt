package sk.stuba.fei.uim.dp.attendance.utils

import androidx.recyclerview.widget.DiffUtil
import sk.stuba.fei.uim.dp.attendance.adapters.ActivitiesAdapter
import sk.stuba.fei.uim.dp.attendance.adapters.MyItem

class ItemDiffCallback(
    private val oldList: List<MyItem>,
    private val newList: List<MyItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if(oldList[oldItemPosition].theView == ActivitiesAdapter.THE_DATE_VIEW &&
            newList[newItemPosition].theView == ActivitiesAdapter.THE_DATE_VIEW){
            return oldList[oldItemPosition].date == newList[newItemPosition].date
        }
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}