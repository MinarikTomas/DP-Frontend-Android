package sk.stuba.fei.uim.dp.attendance.data

import android.content.Context
import android.content.SharedPreferences
import sk.stuba.fei.uim.dp.attendance.config.AppConfig
import sk.stuba.fei.uim.dp.attendance.data.model.User

class PreferenceData private constructor() {
    private fun getSharedPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(
            shpKey, Context.MODE_PRIVATE
        )
    }

    fun clearData(context: Context?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putUser(context: Context?, user: User?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        user?.toJson()?.let {
            editor.putString(userKey, it)
        } ?: editor.remove(userKey)

        editor.apply()
    }

    fun getUser(context: Context?): User? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val json = sharedPref.getString(userKey, null) ?: return null

        return User.fromJson(json)
    }
    fun putAccess(context: Context?, access: String) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putString(accessKey, access)
        editor.apply()
    }

    fun getAccess(context: Context?): String? {
        val sharedPref = getSharedPreferences(context) ?: return ""
        return sharedPref.getString(accessKey, "")
    }

    fun putRefresh(context: Context?, refresh: String) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putString(refreshKey, refresh)
        editor.apply()
    }

    fun getRefresh(context: Context?): String? {
        val sharedPref = getSharedPreferences(context) ?: return ""
        return sharedPref.getString(refreshKey, "")
    }

    fun putIsActivityRunning(context: Context?, isRunning: Boolean) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean(runningKey, isRunning)

        editor.apply()
    }

    fun getIsActivityRunning(context: Context?): Boolean {
        val sharedPref = getSharedPreferences(context) ?: return false
        val isRunning = sharedPref.getBoolean(runningKey, false)
        return isRunning
    }

    fun putIsUpcomingSelected(context: Context?, isUpcomingSelected: Boolean) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean(upcomingKey, isUpcomingSelected)

        editor.apply()
    }

    fun getIsUpcomingSelected(context: Context?): Boolean {
        val sharedPref = getSharedPreferences(context) ?: return false
        val isSelected = sharedPref.getBoolean(upcomingKey, true)
        return isSelected
    }

    companion object {
        @Volatile
        private var INSTANCE: PreferenceData? = null

        private val lock = Any()

        fun getInstance(): PreferenceData =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: PreferenceData().also { INSTANCE = it }
            }

        private const val shpKey = AppConfig.SHARED_PREFERENCES_KEY
        private const val userKey = "userKey"
        private const val accessKey = "accessKey"
        private const val refreshKey = "refreshKey"
        private const val runningKey = "runningKey"
        private const val upcomingKey = "upcomingKey"
    }
}