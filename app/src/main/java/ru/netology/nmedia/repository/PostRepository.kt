package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: RepositoryCallback<List<Post>>)
    fun likeByIdAsync(id: Long, callback: RepositoryCallback<Post>)
    fun unlikeByIdAsync(id: Long, callback: RepositoryCallback<Post>)
    fun saveAsync(post: Post, callback: RepositoryCallback<Unit>)
    fun removeByIdAsync(id: Long, callback: RepositoryCallback<Unit>)

    interface RepositoryCallback<T> {
        fun onSuccess(value: T) {}
        fun onError() {}
    }
}
