package sk.stuba.fei.uim.dp.attendance.data

import android.content.Context
import android.util.Log
import com.auth0.android.jwt.JWT
import sk.stuba.fei.uim.dp.attendance.data.api.ApiService
import sk.stuba.fei.uim.dp.attendance.data.api.model.AddActivityRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.AddParticipantRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.CardRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.ChangePasswordRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.GoogleLoginRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.LoginRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.NameRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.SignupRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.UpdateActivityRequest
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.data.model.Card
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
        password: String,
        context: Context
    ): Pair<String, User?>{
        try{
            val response = service.loginUser(LoginRequest(email, password))
            if(response.isSuccessful){
                response.body()?.let {
                    val jwt = JWT(it.accessToken)
                    PreferenceData.getInstance().putAccess(context, it.accessToken)
                    PreferenceData.getInstance().putRefresh(context, it.refreshToken)
                    return Pair("", User(
                        jwt.getClaim("fullName").asString() ?: "",
                        jwt.subject ?: "",
                        jwt.getClaim("id").asInt() ?: -1,
                        )
                    )
                }
            }
            return Pair("Wrong username or password", null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return Pair("Check internet connection. Failed to login user.", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to login user.", null)
    }

    suspend fun apiSignupUser(
        name: String,
        email: String,
        password: String,
        cardName: String,
        cardSerialNumber: String
    ): String{
        try {
            val response = service.signupUser(SignupRequest(name, email, password, CardRequest(cardName, cardSerialNumber)))
            if (response.isSuccessful){
                return ""
            }
            return "Failed to signup user"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to signup user."
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to signup user."
    }

    suspend fun apiAddActivity(
        uid: Int,
        name: String,
        location: String,
        date: String,
        time: String,
        weeks: Int
    ): String{
        try{
            val response = service.addActivity(AddActivityRequest(
                uid,
                name,
                location,
                date + " " + time,
                weeks
            ))
            if(response.isSuccessful){
                return ""
            }
            return "Failed to create activity"
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return "Check internet connection. Failed to create activity"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to create activity"
    }

    suspend fun apiGetCreatedActivities(uid: Int): Pair<String, List<Activity>>
    {
        try{
            val response = service.getCreatedActivities(uid)
            if (response.isSuccessful){
                response.body()?.let {
                    val activities = it.map{
                        Activity(
                            it.id,
                            it.name,
                            it.location,
                            it.time.split(" ")[0],
                            it.time.split(" ")[1],
                            it.createdBy,
                            null,
                            it.startTime,
                            it.endTime
                        )
                    }
                    return Pair("", activities)
                }
            }
            return Pair("Failed to load activities", emptyList())
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return Pair("Check internet connection. Failed to load activities", emptyList())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load activities", emptyList())
    }

    suspend fun apiGetActivity(id: Int): Pair<String, Activity?>{
        try{
            val response = service.getActivity(id)
            if(response.isSuccessful){
                response.body()?.let {
                    return Pair("", Activity(
                        it.id,
                        it.name,
                        it.location,
                        it.time.split(" ")[0],
                        it.time.split(" ")[1],
                        it.createdBy,
                        it.participants.map {user-> User(user?.fullName ?: "", "", -1,) },
                        it.startTime,
                        it.endTime
                        )
                    )
                }
            }
            return Pair("Failed to load activity", null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return Pair("Check internet connection. Failed to load activity", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load activity", null)
    }

    suspend fun apiStartActivity(id: Int): String {
        try {
            val response = service.startActivity(id)
            if (response.isSuccessful){
                return ""
            }
            return "Failed to start activity"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to start activity"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to start activity"
    }

    suspend fun apiEndActivity(id: Int): String {
        try {
            val response = service.endActivity(id)
            if (response.isSuccessful){
                return ""
            }
            return "Failed to end activity"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to end activity"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to end activity"
    }

    suspend fun apiAddParticipant(id: Int, serialNumber: String): Pair<String, User?>{
        try{
            val response = service.addParticipant(id, AddParticipantRequest(serialNumber))
            if(response.isSuccessful){
                response.body()?.let {
                    return Pair("", User(
                        it.fullName,
                        it.email,
                        it.id
                    ))
                }
            }
            return Pair("Failed to add participant", null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return Pair("Check internet connection. Failed to add participant", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to add participant", null)
    }

    suspend fun apiGetCards(id: Int): Pair<String, List<Card>>{
        try{
            val response = service.getCards(id)
            if (response.isSuccessful){
                response.body()?.let {
                    return Pair("",
                        it.map {
                            Card(it.id, it.name)
                        })
                }
            }
            return Pair("Failed to load cards", emptyList())
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return Pair("Check internet connection. Failed to load cards", emptyList())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load cards", emptyList())
    }

    suspend fun apiDeactivateCard(id: Int): String{
        try {
            val response = service.deactivateCard(id)
            if (response.isSuccessful){
                return ""
            }
            return "Failed to delete card"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to delete card"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to delete card"
    }

    suspend fun apiAddCard(id: Int, name: String, serialNumber: String): String{
        try {
            val response = service.addCard(id, CardRequest(name, serialNumber))
            if(response.isSuccessful){
                return ""
            }
            return "Failed to add card"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to add card"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to add card"
    }

    suspend fun apiGetAttendedActivities(uid: Int): Pair<String, List<Activity>>
    {
        try{
            val response = service.getAttendedActivities(uid)
            if (response.isSuccessful){
                response.body()?.let {
                    val activities = it.map{
                        Activity(
                            it.id,
                            it.name,
                            it.location,
                            it.time.split(" ")[0],
                            it.time.split(" ")[1],
                            it.createdBy,
                            null,
                            it.startTime,
                            it.endTime
                        )
                    }
                    return Pair("", activities)
                }
            }
            return Pair("Failed to load activities", emptyList())
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return Pair("Check internet connection. Failed to load activities", emptyList())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to load activities", emptyList())
    }

    suspend fun apiUpdateCard(id: Int, name: String): String {
        try {
            val response = service.updateCard(id, NameRequest(name))
            if(response.isSuccessful){
                return ""
            }
            return "Failed to update card"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to update card"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to update card"
    }

    suspend fun apiDeleteCard(id: Int): String{
        try {
            val response = service.deleteActivity(id)
            if(response.isSuccessful){
                return ""
            }
            return "Failed to delete activity"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to delete"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to delete activity"
    }

    suspend fun apiUpdateActivity(
        id: Int,
        name: String,
        location: String,
        date: String,
        time: String
    ): Pair<String, Activity?>{
        try {
            val response = service.updateActivity(
                id,
                UpdateActivityRequest(
                    name,
                    location,
                    date + " " + time,
                    false
            ))
            if(response.isSuccessful){
                response.body()?.let {
                    return Pair("", Activity(
                        it.id,
                        it.name,
                        it.location,
                        it.time.split(" ")[0],
                        it.time.split(" ")[1],
                        it.createdBy,
                        null,
                        it.startTime,
                        it.endTime
                    ))
                }
            }
            return Pair("Failed to update activity", null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("API", ex.message.toString())
            return Pair("Check internet connection. Failed to update activity", null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return Pair("Fatal error. Failed to update activity", null)
    }

    suspend fun apiChangePassword(id: Int, password: String): String{
        try {
            val response = service.changePassword(id, ChangePasswordRequest(password))
            if(response.isSuccessful){
                return ""
            }
            return "Failed to change password"
        }catch (ex: IOException) {
            ex.printStackTrace()
            return "Check internet connection. Failed to change password"
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return "Fatal error. Failed to change password"
    }

    suspend fun apiGoogleLogin(token: String, context: Context): Pair<String, User?>{
        try {
            val response = service.googleLogin(GoogleLoginRequest(token))
            if (response.isSuccessful){
                response.body()?.let {
                    val jwt = JWT(it.accessToken)
                    PreferenceData.getInstance().putAccess(context, it.accessToken)
                    PreferenceData.getInstance().putRefresh(context, it.refreshToken)
                    val user = User(
                        jwt.getClaim("fullName").asString() ?: "",
                        jwt.subject ?: "",
                        jwt.getClaim("id").asInt() ?: -1,
                        jwt.getClaim("hasCard").asBoolean()
                    )
                    return Pair("", user)
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