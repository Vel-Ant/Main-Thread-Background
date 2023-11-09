package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import java.io.File

interface PostRepository {

    val data: Flow<PagingData<FeedItem>>
    val dataCount: Flow<List<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun likeById(id: Long)
    suspend fun unlikeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, file: File)
    suspend fun removeById(id: Long)

}
