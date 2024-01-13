package sk.stuba.fei.uim.dp.attendance.data.model

import com.google.gson.Gson
import java.io.IOException

class Activity (
    val id: Number,
    val name: String,
    val location: String,
    val date: String,
    val time: String,
    val createdBy: Number,
    val startTime: String,
    val endTime: String
)
{

    fun toJson(): String? {
        return try {
            Gson().toJson(this)
        }catch (ex: IOException){
            ex.printStackTrace()
            null
        }
    }

    companion object {
        fun fromJson(string: String): Activity? {
            return try {
                Gson().fromJson(string, Activity::class.java)
            }catch (ex: IOException){
                ex.printStackTrace()
                null
            }
        }
    }
}