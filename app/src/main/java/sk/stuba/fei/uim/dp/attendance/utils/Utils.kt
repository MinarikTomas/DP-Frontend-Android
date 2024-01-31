package sk.stuba.fei.uim.dp.attendance.utils

import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.textfield.TextInputLayout
import sk.stuba.fei.uim.dp.attendance.adapters.ActivitiesAdapter
import sk.stuba.fei.uim.dp.attendance.adapters.ActivityItem
import sk.stuba.fei.uim.dp.attendance.adapters.AttendedActivityItem
import sk.stuba.fei.uim.dp.attendance.adapters.CardItem
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

class CardItemDiffCallback(
    private val oldList: List<CardItem>,
    private val newList: List<CardItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

class AttendedActivityItemDiffCallback(
    private val oldList: List<AttendedActivityItem>,
    private val newList: List<AttendedActivityItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

class DisableErrorTextWatcher(
    private val layout: TextInputLayout,
): TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        layout.isErrorEnabled = false
    }

    override fun afterTextChanged(s: Editable?) {
    }
}