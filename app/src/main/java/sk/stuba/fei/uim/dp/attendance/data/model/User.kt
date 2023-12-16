package sk.stuba.fei.uim.dp.attendance.data.model

import com.google.gson.Gson
import java.io.IOException

data class User(
    val name: String?,
    val email: String?,
    val id: Number?,
    val access: String
){

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