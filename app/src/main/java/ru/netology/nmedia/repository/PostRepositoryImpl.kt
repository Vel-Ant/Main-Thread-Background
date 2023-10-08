package ru.netology.nmedia.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.errors.NumberResponseError
import ru.netology.nmedia.util.AttachmentType
import java.io.File
import java.util.concurrent.CancellationException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: PostApiService
) : PostRepository {

    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        pagingSourceFactory = { PostPagingSource(apiService) },
    ).flow

    override val dataCount: Flow<List<Post>> = dao.getAllVisible()
        .map { it.map(PostEntity::toDto) }
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000)
                val response = apiService.getNewer(id)

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
        val response = apiService.getAll()

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }

        val posts = response.body() ?: throw RuntimeException("body is null")

        dao.insert(posts.toEntity(false))
    }

    override suspend fun getAllNewPosts() {
        dao.makeAllNewPostsVisible()
    }

    override suspend fun save(post: Post) {
        val response = apiService.save(post)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        val body = response.body() ?: throw RuntimeException("body is null")

        dao.insert(PostEntity.fromDto(body))
    }

    override suspend fun saveWithAttachment(post: Post, file: File) {
        val media = uploadMedia(file)

        val response = apiService.save(
            post.copy(
                attachment = Attachment(
                    url = media.id,
                    AttachmentType.IMAGE
                )
            )
        )

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
        val body = response.body() ?: throw RuntimeException("body is null")

        dao.insert(PostEntity.fromDto(body))
    }

    private suspend fun uploadMedia(file: File): Media {
        val formData = MultipartBody.Part.createFormData(
            "file", file.name, file.asRequestBody()
        )

        val response = apiService.uploadMedia(formData)
        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }

        return response.body() ?: throw RuntimeException("body is null")
    }

    override suspend fun likeById(id: Long) {
        dao.likeById(id)
        val response = apiService.likeById(id)

        if (!response.isSuccessful) {
            dao.likeById(id)
            throw NumberResponseError(response.code())
        }
        val body = response.body() ?: throw RuntimeException("body is null")

        dao.insert(PostEntity.fromDto(body))
    }

    override suspend fun unlikeById(id: Long) {
        dao.likeById(id)
        val response = apiService.unlikeById(id)

        if (!response.isSuccessful) {
            dao.likeById(id)
            throw NumberResponseError(response.code())
        }
        val body = response.body() ?: throw RuntimeException("body is null")

        dao.insert(PostEntity.fromDto(body))
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        val response = apiService.removeById(id)

        if (!response.isSuccessful) {
            throw NumberResponseError(response.code())
        }
    }
}
