package com.example.routing

import com.example.service.JwtService
import com.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    jwtService: JwtService
){
    routing {

        route("/api/auth") {
            authRoute(jwtService)
        }

        route("/api/user"){
            userRoute(userService, jwtService)
        }
    }
}