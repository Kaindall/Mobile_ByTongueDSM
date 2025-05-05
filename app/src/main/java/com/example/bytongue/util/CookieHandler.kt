package com.example.bytongue.util

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CookieHandler : CookieJar {

    private val cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url.host()] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host()] ?: emptyList()
    }
}
