package com.example.bytongue.model

import com.google.gson.annotations.SerializedName

data class HistoryResponse (
    @SerializedName("chat_id") val chat_id: String,
    @SerializedName("created_dt") val created_dt: String,
    @SerializedName("updt_dt") val updt_dt: String
)