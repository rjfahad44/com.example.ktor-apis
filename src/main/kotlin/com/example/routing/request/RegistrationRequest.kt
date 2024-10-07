package com.example.routing.request

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val phoneNumber: String,
    val password: String
)
