package sk.stuba.fei.uim.dp.attendance.data.api.helper

import android.content.Context
import androidx.navigation.Navigation
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData
import sk.stuba.fei.uim.dp.attendance.data.api.ApiService
import sk.stuba.fei.uim.dp.attendance.data.model.User

class TokenAuthenticator(val context: Context): Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {

        if (response.request().url().toString().contains("/auth/signup", true)
            || response.request().url().toString().contains("/auth/login", true)
            || response.request().url().toString().contains("/auth/refresh", true)
            || response.request().url().toString().contains("/auth/google", true)
        ) {
        } else {
            //if the authorization token was required, but it was rejected from REST API, it is probably outdated
            if (response.code() == 401) {
                val userItem = PreferenceData.getInstance().getUser(context)
                userItem?.let { user ->
                    val tokenResponse = ApiService.create(context).refreshTokenBlocking().execute()
                    if (tokenResponse.isSuccessful) {
                        tokenResponse.body()?.let {
                            val new_user = User(
                                user.name,
                                user.email,
                                user.id,
                            )
                            PreferenceData.getInstance().putUser(context, new_user)
                            PreferenceData.getInstance().putAccess(context, it.accessToken)
                            PreferenceData.getInstance().putRefresh(context, it.refreshToken)
                            return response.request().newBuilder()
                                .header("Authorization", "Bearer ${it.accessToken}")
                                .build()
                        }
                    }
                }
                //if there was no success of refresh token we logout user and clean any data
                PreferenceData.getInstance().clearData(context)
                return null
            }
        }
        return null
    }
}