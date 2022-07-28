package paita.stream_app_final.Tafa.Shared

import android.content.Context
import android.content.SharedPreferences
import paita.stream_app_final.R

class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun saveAuthToken(authtoken: String, refreshtoken: String, jwttoken: String) {
        val editor = prefs.edit()
        editor.putString("authtoken", authtoken)
        editor.apply()
        editor.putString("refreshtoken", refreshtoken)
        editor.apply()
        editor.putString("jwttoken", jwttoken)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString("authtoken", null)
    }

    fun fetchJwtToken(): String? {
        return prefs.getString("jwttoken", null)
    }

    fun logout(): Boolean {
        try {
            val editor = prefs.edit()
            editor.remove("authtoken")
            editor.apply()
            editor.remove("refreshtoken")
            editor.apply()
            editor.remove("refreshtoken")
            editor.apply()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun saveUp(u: String, p: String) {
        val editor = prefs.edit()
        editor.putString("u", u)
        editor.apply()
        editor.putString("p", p)
        editor.apply()
    }

    fun fetchu(): String? {
        return prefs.getString("u", null)
    }

    fun fetchp(): String? {
        return prefs.getString("p", null)
    }

}