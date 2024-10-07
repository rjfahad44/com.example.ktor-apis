package com.example.service

import com.example.models.REGISTRATION_ENUM
import com.example.models.User
import com.example.repositorty.UserRepository
import java.util.*

class UserService(
    private val userRepository: UserRepository
) {
    fun findAllUser() = userRepository.findAllUser()
    fun findById(id: String) = userRepository.findById(UUID.fromString(id))
    fun findByPhoneNumber(username: String) = userRepository.findByPhoneNumber(username)
    fun save(user: User): Pair<REGISTRATION_ENUM, User>  {
        val foundUser = findByPhoneNumber(user.phoneNumber)
        return if (foundUser == null){
            if (userRepository.save(user)) Pair(REGISTRATION_ENUM.SUCCESS, user) else Pair(REGISTRATION_ENUM.FAILED, user)
        }else Pair(REGISTRATION_ENUM.EXISTS, user)
    }
}