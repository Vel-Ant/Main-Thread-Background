package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAll(): List<Post>
    fun getAllAsync(callback: RepositoryCallback<List<Post>>)
    fun likeById(id: Long, callback: RepositoryCallback<Post>)
    fun unlikeById(id: Long, callback: RepositoryCallback<Post>)
    fun save(post: Post, callback: RepositoryCallback<Post>)
    fun removeById(id: Long, callback: RepositoryCallback<Unit>)

    interface RepositoryCallback<T> {
        fun onSuccess(value: T) {}
        fun onError(exception: Exception) {}
    }
}
