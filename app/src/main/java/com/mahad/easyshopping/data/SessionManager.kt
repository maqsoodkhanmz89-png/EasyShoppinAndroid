package com.mahad.easyshopping.data

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "easy_shopping_prefs"
    private const val KEY_TOKEN = "auth_token"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            prefs.edit().putString(KEY_TOKEN, value).apply()
        }
    
    val isLoggedIn: Boolean
        get() = token != null

    fun getBearerToken(): String? {
        return token?.let { "Bearer $it" }
    }

    fun logout() {
        token = null
    }
}
