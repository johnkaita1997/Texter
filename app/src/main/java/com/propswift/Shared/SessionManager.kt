package com.propswift.Shared

import android.content.Context
import android.content.SharedPreferences
import com.propswift.R


class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun savejwtToken(access: String, refresh: String) {
        val editor = prefs.edit()
        editor.putString("access", access)
        editor.apply()
        editor.putString("refresh", refresh)
        editor.apply()
    }

    fun fetchAccessToken(): String {
        return "Bearer ${prefs.getString("access", null)}"
    }

    fun fetchRefreshToken(): String {
        return "Bearer ${prefs.getString("refresh", null)}"
    }

    fun logout(): Boolean {
        try {
            val editor = prefs.edit()
            editor.remove("access")
            editor.apply()
            editor.remove("refresh")
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