package com.mahad.easyshopping.data

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "easy_shopping_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_PHONE = "user_phone"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) {
            // Use commit() for critical auth token to ensure immediate synchronous write
            prefs.edit().putString(KEY_TOKEN, value).commit()
        }

    var userName: String?
        get() = prefs.getString(KEY_USER_NAME, null)
        set(value) {
            prefs.edit().putString(KEY_USER_NAME, value).apply()
        }

    var userEmail: String?
        get() = prefs.getString(KEY_USER_EMAIL, null)
        set(value) {
            prefs.edit().putString(KEY_USER_EMAIL, value).apply()
        }

    var userPhone: String?
        get() = prefs.getString(KEY_USER_PHONE, null)
        set(value) {
            prefs.edit().putString(KEY_USER_PHONE, value).apply()
        }
    
    val isLoggedIn: Boolean
        get() = token != null

    fun getBearerToken(): String? {
        return token?.let { "Bearer $it" }
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
