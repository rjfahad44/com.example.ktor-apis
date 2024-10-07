package com.example.routing

import com.example.models.User
import com.example.routing.request.UserRequest
import com.example.routing.response.UserResponse
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoute(userService: UserService){
    post {
        val userRequest = call.receive<UserRequest>()

        val createUser = userService.save(
            userRequest.toModel()
        )?: return@post call.respond(HttpStatusCode.BadRequest)

        call.response.header(
            name = "id",
            value = createUser.id.toString()
        )

        call.respond(HttpStatusCode.Created, createUser.toResponse())
    }

    get {
        val  users = userService.findAllUser()
        call.respond(message = users.map (User::toResponse))
    }

    get("/{id}") {
        val id : String = call.parameters["id"]?: return@get call.respond(HttpStatusCode.BadRequest)
        val foundUser = userService.findById(id)?: return@get call.respond(HttpStatusCode.NotFound)
        call.respond(message = foundUser.toResponse())
    }
}

private fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username
    )
}

private fun UserRequest.toModel(): User {
    return User(
        id = UUID.randomUUID(),
        username = this.username,
        password = this.password
    )
}
