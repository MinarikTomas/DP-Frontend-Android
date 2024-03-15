package sk.stuba.fei.uim.dp.attendance.data.model

import com.google.gson.Gson
import java.io.IOException

data class Activity (
    val id: Int,
    val name: String,
    val location: String,
    val date: String,
    val time: String,
    val createdBy: Number,
    val participants: MutableList<User?>?,
    val startTime: String,
    val endTime: String
){
    fun toJson(): String? {
        return try {
            Gson().toJson(this)
        }catch (ex: IOException){
            ex.printStackTrace()
            null
        }
    }
}
