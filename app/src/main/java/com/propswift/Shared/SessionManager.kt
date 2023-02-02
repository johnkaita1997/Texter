package com.propswift.Shared

import android.content.Context
import android.content.SharedPreferences
import com.propswift.R


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
        return "Bearer ${prefs.getString("authtoken", null)}"
//        return "Bearer mpKtbwDVR0vLnGN9s18kBc9EG6mQ8B"
    }

    fun fetchJwtToken(): String? {
        return "Bearer ${prefs.getString("jwttoken", null)}"
//        return "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VyIjoiZjg3OTJiMGEtMDg2YS00ZGNmLWI5ZWQtYTg4MzgwMTA2MzM4Iiwicm9sZXMiOltdLCJleHAiOjE2NzUzODkwMDQsImlhdCI6MTY3NTMwMjYwNCwiYXVkIjoidXJuOmpzdCJ9._aUjZJvAst1nzhWpkXDgwuB7FzjVa50XpQ5XLjmw1LcX8V3UByIMfpJM9QPFmHOh34KiyHSO_i5iLAKkOi3J7Q"

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