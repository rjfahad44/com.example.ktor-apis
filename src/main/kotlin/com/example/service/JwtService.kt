package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import com.example.routing.request.LoginRequest
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtService(
    private val application: Application,
    private val userService: UserService
) {

    private val secret = System.getenv("SECRET_KEY")
    private val issuer = System.getenv("ISSUER")
    private val audience = System.getenv("AUDIENCE")

    val realm = System.getenv("REALM")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC512(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    fun createJwtToken(loginRequest: LoginRequest): String?{
        val foundUser = userService.findByPhoneNumber(loginRequest.phoneNumber)

        return if (foundUser != null && foundUser.password == loginRequest.password){
            JWT
                .create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("phoneNumber", foundUser.phoneNumber)
                .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
                .sign(Algorithm.HMAC512(secret))
        }else null
    }

    fun customValidator(credential: JWTCredential): JWTPrincipal? {
        val user = extractData(credential)
        val foundUser = user.phoneNumber?.let (userService::findByPhoneNumber)

        return foundUser?.let {
            if (audienceMatches(credential)){
                JWTPrincipal(credential.payload)
            }else null
        }
    }

    private fun audienceMatches(credential: JWTCredential): Boolean {
        return credential.payload.audience.contains(audience)
    }

    private fun extractData(credential: JWTCredential): User {
        return User(id = UUID.randomUUID(),
            phoneNumber = credential.payload.getClaim("phoneNumber").asString(),
            password = credential.payload.getClaim("password").asString(),
            )
    }

    private fun getConfigProperty(path: String): String{
        val value = application.environment.config.propertyOrNull(path)?.getString()
        println("Config value for $path: $value") // Logging for debugging
        return value ?: throw IllegalArgumentException("Missing configuration for $path")
    }
}