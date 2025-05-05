package com.example.bytongue.model

data class AuthResponse(
    val user_id: Int,
    val name: String,
    val email: String,
    val birthday: String? // Pode ser null
)
