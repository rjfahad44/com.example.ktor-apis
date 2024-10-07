package com.example.routing.response

data class ApiResponse<T>(
    val statusCode: Int = 404,
    val message: String = "",
    val data: T? = null
)