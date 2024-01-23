package sk.stuba.fei.uim.dp.attendance.data.model

import com.google.gson.Gson
import java.io.IOException

data class Card(
    val id: Int,
    val name: String
) {
    fun toJson(): String? {
        return try {
            Gson().toJson(this)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    companion object {
        fun fromJson(string: String): Card? {
            return try {
                Gson().fromJson(string, Card::class.java)
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
        }
    }
}


