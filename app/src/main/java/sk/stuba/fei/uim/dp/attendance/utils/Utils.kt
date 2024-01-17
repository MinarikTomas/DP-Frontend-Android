package sk.stuba.fei.uim.dp.attendance.utils

import androidx.recyclerview.widget.DiffUtil
import sk.stuba.fei.uim.dp.attendance.adapters.ActivitiesAdapter
import sk.stuba.fei.uim.dp.attendance.adapters.ActivityItem
import sk.stuba.fei.uim.dp.attendance.adapters.ParticipantItem

class ActivityItemDiffCallback(
    private val oldList: List<ActivityItem>,
    private val newList: List<ActivityItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if(oldList[oldItemPosition].theView == ActivitiesAdapter.THE_DATE_VIEW &&
            newList[newItemPosition].theView == ActivitiesAdapter.THE_DATE_VIEW){
            return oldList[oldItemPosition].activity.date == newList[newItemPosition].activity.date
        }
        return oldList[oldItemPosition].activity.id == newList[newItemPosition].activity.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

class ParticipantItemDiffCallback(
    private val oldList: List<ParticipantItem>,
    private val newList: List<ParticipantItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}