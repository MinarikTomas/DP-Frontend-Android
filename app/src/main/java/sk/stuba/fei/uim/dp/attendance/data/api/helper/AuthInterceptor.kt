package sk.stuba.fei.uim.dp.attendance.data.api.helper

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
        Log.d("Interceptor", chain.request().url().toString())

        if (chain.request().url().toString().contains("/auth/signup", true)
            || chain.request().url().toString().contains("/auth/login", true)
        ) {
            //here we do not need a authorization token
        } else if (chain.request().url().toString().contains("/auth/refresh", true)) {
            //when refreshing token we need to add refresh token
            val refreshToken = PreferenceData.getInstance().getRefresh(context)
            request.header(
                "Authorization",
                "Bearer $refreshToken"
                )
        } else {
            //we add auth token
            val token = PreferenceData.getInstance().getAccess(context)
            request.header(
                "Authorization",
                "Bearer $token"
            )
        }

        return chain.proceed(request.build())
    }
}