package com.example.bytongue.util

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class PersistentCookieHandler(context: Context) : CookieJar {
    private val prefs = context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieString = cookies.joinToString("; ") { it.toString() }
        prefs.edit().putString(url.host(), cookieString).apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieString = prefs.getString(url.host(), null) ?: return emptyList()
        return cookieString.split(";").mapNotNull {
            Cookie.parse(url, it)
        }
    }
}

