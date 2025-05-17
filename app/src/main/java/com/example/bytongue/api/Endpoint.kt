package com.example.bytongue.api

import com.example.bytongue.model.AuthResponse
import com.example.bytongue.model.ChatRequest
import com.example.bytongue.model.ChatResponse
import com.example.bytongue.model.CreateChatRequest
import com.example.bytongue.model.CreateChatResponse
import com.example.bytongue.model.HistoryResponse
import com.example.bytongue.model.LoginRequest
import com.example.bytongue.model.SignupRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Endpoint {

    @POST("users")
    fun registerUser(
        @Body request: SignupRequest
    ): Call<ResponseBody>

    @POST("auth")
    fun authenticate(
        @Body loginRequest: LoginRequest
    ): Call<Void>

    @GET("auth")
    fun getUserToken(): Call<AuthResponse>

    @DELETE("auth")
    fun logout(): Call<Void>

    @POST("ias/chat/{chatId}")
    fun sendMessage(
        @Path("chatId") chatId: String,
        @Body request: ChatRequest
    ): Call<ChatResponse>

    @POST("ias/chat")
    fun createSendMessage(
        @Body request: CreateChatRequest
    ): Call<CreateChatResponse>

    @GET("users/{userId}/chats")
    suspend fun historyUser(
        @Path("userId") userId: String
    ): Response<List<HistoryResponse>>
}