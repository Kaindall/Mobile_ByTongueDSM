package com.example.bytongue.util

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {
    companion object {
        fun getRetrofitInstance(path: String): Retrofit {
            val client = OkHttpClient.Builder()
                .cookieJar(CookieHandler()) // Agora está referenciando a classe correta
                .build()

            return Retrofit.Builder()
                .baseUrl(path)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
