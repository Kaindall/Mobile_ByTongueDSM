package com.example.bytongue.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs : SharedPreferences = context.getSharedPreferences("ByTonguePrefs", Context.MODE_PRIVATE)

    companion object {
        private const val USER_NAME = "USER_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val USER_IMAGE = "USER_IMAGE"
        private const val USER_LEVEL = "USER_LEVEL"
        private const val USER_LANGUAGE = "USER_LANGUAGE"
        private const val TOKEN = "TOKEN"
    }

    fun saveUserName(userName: String) {
        prefs.edit() { putString(USER_NAME, userName).apply() }
    }

    fun getUserName() : String? {
        return prefs.getString(USER_NAME, null)
    }

    fun saveUserEmail(userEmail: String) {
        prefs.edit() { putString(USER_EMAIL, userEmail).apply() }
    }

    fun getUserEmail() : String? {
        return prefs.getString(USER_EMAIL, null)
    }

    fun saveUserId(token: String) {
        prefs.edit() { putString(TOKEN, token).apply() }
    }

    fun getUserId() : String? {
        return prefs.getString(TOKEN, null)
    }

    fun saveUserImage(userImage : String) {
        prefs.edit() { putString(USER_IMAGE, userImage) }
    }

    fun getUserImage() : String? {
        return prefs.getString(USER_IMAGE, null)
    }

    fun saveLevel(userLevel : Int) {
        prefs.edit() { putInt(USER_LEVEL, userLevel) }
    }

    fun getLevel() : Int {
        return prefs.getInt(USER_LEVEL, 1)
    }

    fun saveLanguage(language: String) {
        prefs.edit { putString(USER_LANGUAGE, language) }
    }

    fun getLanguage(): String? {
        return prefs.getString(USER_LANGUAGE, "en-US")
    }

    fun clearSession() {
        prefs.edit() { clear().apply() }
    }
}