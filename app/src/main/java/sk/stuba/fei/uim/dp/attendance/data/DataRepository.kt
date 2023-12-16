package sk.stuba.fei.uim.dp.attendance.data

import android.content.Context
import android.util.Log
import com.auth0.android.jwt.JWT
import sk.stuba.fei.uim.dp.attendance.data.api.ApiService
import sk.stuba.fei.uim.dp.attendance.data.api.model.LoginRequest
import sk.stuba.fei.uim.dp.attendance.data.model.User
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(
                        ApiService.create(context)
                    ).also { INSTANCE = it }
            }
    }

    suspend fun apiLoginUser(
        email: String,
        password: String
    ): Pair<String, User?>{
        if (email.isEmpty()) {
            return Pair("Email can not be empty", null)
        }
        if (password.isEmpty()) {
            return Pair("Password can not be empty", null)
        }
        try{
            val response = service.loginUser(LoginRequest(email, password))
            if(response.isSuccessful){
                response.body()?.let {
                    val jwt = JWT(it.accessToken)
                    Log.d("API", "success")
                    return Pair("", User(
                        jwt.getClaim("fullName").asString(),
                        jwt.subject,
                        jwt.getClaim("id").asInt(),
                        it.accessToken
                        )
                    )
                }
            }
            return Pair("Failed to login user", null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }
}