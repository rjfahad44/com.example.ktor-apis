package com.example.repositorty

import com.example.models.User
import java.util.UUID

class UserRepository {

    private val users = mutableListOf<User>()

    fun findAllUser(): List<User> = users

    fun findById(id: UUID): User? = users.firstOrNull { it.id == id }

    fun findByPhoneNumber(phoneNumber: String): User? = users.firstOrNull { it.phoneNumber == phoneNumber }

    fun save(user: User): Boolean = users.add(user)

}