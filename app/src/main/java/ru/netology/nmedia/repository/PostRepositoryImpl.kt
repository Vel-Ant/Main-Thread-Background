package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.util.copy
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.errors.NumberResponseError
import ru.netology.nmedia.viewmodel.PostViewModel

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAll() {
        val response = PostApi.service.getAll()

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }

        val posts = response.body() ?: throw RuntimeException("body is null")

        dao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun save(post: Post) {
        val response = PostApi.service.save(post)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        val body = response.body() ?: throw RuntimeException("body is null")

        dao.insert(PostEntity.fromDto(body))
    }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
        val response = PostApi.service.likeById(id)

        if (!response.isSuccessful) {
            dao.likeById(id)
            throw NumberResponseError(response.code())
        }
        val body = response.body() ?: throw RuntimeException("body is null")

        dao.insert(PostEntity.fromDto(body))
    }

    override suspend fun unlikeById(id: Long) {
        dao.likeById(id)
        val response = PostApi.service.unlikeById(id)

        if (!response.isSuccessful) {
            dao.likeById(id)
            throw NumberResponseError(response.code())
        }
        val body = response.body() ?: throw RuntimeException("body is null")

        dao.insert(PostEntity.fromDto(body))
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        val response = PostApi.service.removeById(id)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
    }
}
