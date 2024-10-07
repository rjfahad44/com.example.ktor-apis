package com.example.service

import com.example.models.User
import com.example.repositorty.UserRepository
import java.util.*

class UserService(
    private val userRepository: UserRepository
) {
    fun findAllUser() = userRepository.findAllUser()
    fun findById(id: String) = userRepository.findById(UUID.fromString(id))
    fun findByUsername(username: String) = userRepository.findByUsername(username)
    fun save(user: User): User?{
        val foundUser = findByUsername(user.username)
        return if (foundUser == null){
            userRepository.save(user)
            user
        }else null
    }
}