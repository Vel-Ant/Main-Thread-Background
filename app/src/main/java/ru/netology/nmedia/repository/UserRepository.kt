package ru.netology.nmedia.repository

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.errors.NumberResponseError
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: PostApiService
) {

    suspend fun updateUser(login: String, pass: String): User {
        val response = apiService.updateUser(login, pass)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        return response.body() ?: throw RuntimeException("body is null")
    }

    suspend fun registerUser(login: String, pass: String, name: String): User {
        val response = apiService.registerUser(login, pass, name)

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

        val response = apiService.registerWithPhoto(
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