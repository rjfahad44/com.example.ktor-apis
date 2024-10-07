package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.routing.request.LoginRequest
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.Date

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
        val foundUser = userService.findByUsername(loginRequest.username)

        return if (foundUser != null && foundUser.password == loginRequest.password){
            JWT
                .create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", foundUser.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
                .sign(Algorithm.HMAC512(secret))
        }else null
    }

    fun customValidator(credential: JWTCredential): JWTPrincipal? {
        val username = extractUsername(credential)
        val foundUser = username?.let (userService::findByUsername)

        return foundUser?.let {
            if (audienceMatches(credential)){
                JWTPrincipal(credential.payload)
            }else null
        }
    }

    private fun audienceMatches(credential: JWTCredential): Boolean {
        return credential.payload.audience.contains(audience)
    }

    private fun extractUsername(credential: JWTCredential): String? {
        return credential.payload.getClaim("username").asString()
    }

    private fun getConfigProperty(path: String): String{
        val value = application.environment.config.propertyOrNull(path)?.getString()
        println("Config value for $path: $value") // Logging for debugging
        return value ?: throw IllegalArgumentException("Missing configuration for $path")
    }
}