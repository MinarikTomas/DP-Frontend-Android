package sk.stuba.fei.uim.dp.attendance.data.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import sk.stuba.fei.uim.dp.attendance.config.AppConfig
import sk.stuba.fei.uim.dp.attendance.data.api.helper.AuthInterceptor
import sk.stuba.fei.uim.dp.attendance.data.api.model.AddActivityRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.LoginRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.AuthResponse
import sk.stuba.fei.uim.dp.attendance.data.api.model.ActivityResponse
import sk.stuba.fei.uim.dp.attendance.data.api.model.ActivityWithParticipantsResponse
import sk.stuba.fei.uim.dp.attendance.data.api.model.AddParticipantRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.CardRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.CardResponse
import sk.stuba.fei.uim.dp.attendance.data.api.model.SignupRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.UserResponse

interface ApiService {

    @POST("auth/login")
    suspend fun loginUser(@Body userInfo: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signupUser(@Body userInfo: SignupRequest): Response<Void>

    @POST("activity")
    suspend fun addActivity(@Body activityInfo: AddActivityRequest): Response<Void>

    @GET("user/{id}/activities?type=created")
    suspend fun getCreatedActivities(@Path("id") uid: Int): Response<List<ActivityResponse>>

    @GET("activity/{id}")
    suspend fun getActivity(@Path("id") id: Int): Response<ActivityWithParticipantsResponse>

    @PUT("activity/{id}/start")
    suspend fun startActivity(@Path("id") id: Int): Response<Void>

    @PUT("activity/{id}/end")
    suspend fun endActivity(@Path("id") id: Int): Response<Void>

    @POST("activity/{id}")
    suspend fun addParticipant(@Path("id") id: Int, @Body participantInfo: AddParticipantRequest): Response<UserResponse>

    @GET("user/{id}/cards?type=active")
    suspend fun getCards(@Path("id")id: Int): Response<List<CardResponse>>

    @DELETE("card/{id}")
    suspend fun deactivateCard(@Path("id")id: Int): Response<Void>

    @POST("user/{id}/cards")
    suspend fun addCard(@Path("id")id: Int, @Body cardInfo: CardRequest): Response<Void>

    companion object {
        fun create(context: Context): ApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}