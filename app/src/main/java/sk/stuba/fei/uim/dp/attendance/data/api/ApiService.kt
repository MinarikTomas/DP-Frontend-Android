package sk.stuba.fei.uim.dp.attendance.data.api

import android.content.Context
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import sk.stuba.fei.uim.dp.attendance.config.AppConfig
import sk.stuba.fei.uim.dp.attendance.data.api.model.LoginRequest
import sk.stuba.fei.uim.dp.attendance.data.api.model.AuthResponse
import sk.stuba.fei.uim.dp.attendance.data.api.model.SignupRequest

interface ApiService {

    @POST("auth/login")
    suspend fun loginUser(@Body userInfo: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signupUser(@Body userInfo: SignupRequest): Response<Void>

    companion object {
        fun create(context: Context): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}