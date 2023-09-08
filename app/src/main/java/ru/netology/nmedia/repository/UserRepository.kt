package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.errors.NumberResponseError

class UserRepository {
    suspend fun updateUser(login: String, pass: String): User {
        val response = PostApi.service.updateUser(login, pass)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        return response.body() ?: throw RuntimeException("body is null")
    }

    suspend fun registerUser (login: String, pass: String, name: String): User {
        val response = PostApi.service.registerUser(login, pass, name)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        return response.body() ?: throw RuntimeException("body is null")
    }

}