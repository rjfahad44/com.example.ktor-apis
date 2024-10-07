package com.example.routing.response

import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse<T>(
    val statusCode: Int = 404,
    val message: String = "",
    val data: T? = null
)