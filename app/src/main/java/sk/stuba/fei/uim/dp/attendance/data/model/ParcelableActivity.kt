package sk.stuba.fei.uim.dp.attendance.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableActivity(
    val id: Int,
    val name: String,
    val location: String,
    val date: String,
    val time: String
) : Parcelable
