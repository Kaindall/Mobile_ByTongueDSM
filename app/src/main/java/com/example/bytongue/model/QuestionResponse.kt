package com.example.bytongue.model

data class QuestionResponse (

    val question: String? = null,
    val options: List<String>? = null,
    val correct: Int = 0

)