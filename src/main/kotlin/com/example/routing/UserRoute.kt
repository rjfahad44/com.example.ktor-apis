package com.example.routing

import com.example.models.REGISTRATION_ENUM
import com.example.models.User
import com.example.routing.request.RegistrationRequest
import com.example.routing.response.ApiResponse
import com.example.routing.response.UserResponse
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.userRoute(userService: UserService){
    post("/registration") {
        val registrationRequest = call.receive<RegistrationRequest>()

        /*call.response.header(
            name = "id",
            value = createUser.id.toString()
        )*/
        val createUser = userService.save(registrationRequest.toModel())
        val pairData = when(createUser.first){
            REGISTRATION_ENUM.SUCCESS -> {
                Pair(HttpStatusCode.Created, ApiResponse(
                    statusCode = HttpStatusCode.Created.value,
                    message = "Register Successfully",
                    data = createUser.second.toResponse()
                ))
            }
            REGISTRATION_ENUM.EXISTS -> {
                Pair(HttpStatusCode.Found, ApiResponse(
                    statusCode = HttpStatusCode.Found.value,
                    message = "Already Registered",
                    data = createUser.second.toResponse()
                ))
            }
            REGISTRATION_ENUM.FAILED -> {
                Pair(HttpStatusCode.BadRequest, ApiResponse(
                    statusCode = HttpStatusCode.BadRequest.value,
                    message = "Something want wrong!, please try again later.",
                    data = createUser.second.toResponse()
                ))
            }
        }

        call.respond(pairData.first, pairData.second)
    }

//    get {
//        val  users = userService.findAllUser()
//        call.respond(HttpStatusCode.OK, ApiResponse(
//            statusCode = HttpStatusCode.OK.value,
//            message = "Success.",
//            data = users.map (User::toResponse)
//        ))
//    }

    get("") { //http://localhost:8080/api/user?id=d8e9f76d-14db-4031-9a8d-e3d8395dea26
        //val id : String = call.parameters["id"]
        val id : String = call.request.queryParameters["id"]
            ?: return@get call.respond(
            HttpStatusCode.BadRequest,
            ApiResponse(
            statusCode = HttpStatusCode.BadRequest.value,
            message = "Please provide valid id.",
            data = null
        ))
        val foundUser = userService.findById(id)?: return@get call.respond(
            HttpStatusCode.NotFound,
            ApiResponse(
                statusCode = HttpStatusCode.NotFound.value,
                message = "NotFound!!",
                data = null
            ))
        call.respond(HttpStatusCode.OK, ApiResponse(
            statusCode = HttpStatusCode.OK.value,
            message = "Success.",
            data = foundUser
        ))
    }
}

private fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        phoneNumber = this.phoneNumber
    )
}


private fun RegistrationRequest.toModel(): User {
    return User(
        id = UUID.randomUUID(),
        phoneNumber = this.phoneNumber,
        password = this.password
    )
}
