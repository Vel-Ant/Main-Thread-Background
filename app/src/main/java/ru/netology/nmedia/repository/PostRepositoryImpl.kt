package ru.netology.nmedia.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.errors.NumberResponseError
import java.util.concurrent.CancellationException

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {

    override val data: Flow<List<Post>> = dao.getAll().map {
        it.map(PostEntity::toDto)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = PostApi.service.getNewer(id)

                val posts = response.body().orEmpty()

                dao.insert(posts.toEntity(true))

                emit(posts.size)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // ignore
                e.printStackTrace()
            }
        }
    }

    override suspend fun getAll() {
        val response = PostApi.service.getAll()

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }

        val posts = response.body() ?: throw RuntimeException("body is null")

        dao.insert(posts.toEntity(false))
    }

    override suspend fun getAllNewPosts() {
        dao.getAllNewPosts()
        dao.makeAllNewPostsVisible()
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
