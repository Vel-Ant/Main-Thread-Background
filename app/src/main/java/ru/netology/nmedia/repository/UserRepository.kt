package ru.netology.nmedia.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.MediaUpload
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

    suspend fun registerUser(login: String, pass: String, name: String): User {
        val response = PostApi.service.registerUser(login, pass, name)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        return response.body() ?: throw RuntimeException("body is null")
    }

    suspend fun registerUserWithPhoto(
        login: String,
        pass: String,
        name: String,
        media: MediaUpload
    ): User {

        val media = MultipartBody.Part.createFormData(
            name = "file",
            filename = media.file.name,
            body = media.file.asRequestBody()
        )

        val response = PostApi.service.registerWithPhoto(
            login = login.toRequestBody("text/plain".toMediaType()),
            pass = pass.toRequestBody("text/plain".toMediaType()),
            name = name.toRequestBody("text/plain".toMediaType()),
            media
        )

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        return response.body() ?: throw RuntimeException("body is null")
    }

}