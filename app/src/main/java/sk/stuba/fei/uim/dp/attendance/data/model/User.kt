package sk.stuba.fei.uim.dp.attendance.data.model

import android.os.Parcelable
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize
import java.io.IOException

@Parcelize
data class User(
    val name: String,
    val email: String,
    val id: Int,
    val hasCard: Boolean? = true
) : Parcelable{

    fun toJson(): String? {
        return try {
            Gson().toJson(this)
        }catch (ex: IOException){
            ex.printStackTrace()
            null
        }
    }

    companion object {
        fun fromJson(string: String): User? {
            return try {
                Gson().fromJson(string, User::class.java)
            }catch (ex: IOException){
                ex.printStackTrace()
                null
            }
        }
    }
}