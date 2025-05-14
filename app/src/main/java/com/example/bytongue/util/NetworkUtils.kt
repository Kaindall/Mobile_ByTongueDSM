package com.example.bytongue.util

import android.content.Context
import android.util.Log
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {
    companion object {
        fun getRetrofitInstance(path: String, context: Context): Retrofit {

            val client = OkHttpClient.Builder()
                .cookieJar(PersistentCookieHandler(context))
                .build()

            return Retrofit.Builder()
                .baseUrl(path)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
