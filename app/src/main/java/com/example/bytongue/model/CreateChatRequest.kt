package com.example.bytongue.model

data class CreateChatRequest(
    val level : String,
    val from : String,
    val to : String,
    val content : String,

)