package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.errors.NumberResponseError
import ru.netology.nmedia.model.FeedModel

class PostRepositoryImpl : PostRepository {

    override fun getAll(): List<Post> {
        return PostApi.service.getPosts()
            .execute()
            .let {
                if (!it.isSuccessful) {
                    error("Response code is ${it.code()}")
                }
                it.body() ?: throw RuntimeException("body is null")
            }
    }

    override fun getAllAsync(callback: PostRepository.RepositoryCallback<List<Post>>) {

        PostApi.service.getPosts()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
//                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    val posts = response.body()

                    if (posts == null) {
                        callback.onError(RuntimeException("Body is empty"))
                        return
                    }

                    callback.onSuccess(posts)

                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })
    }

    override fun likeById(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
        PostApi.service.likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
//                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }
                    val post = response.body()

                    if (post == null) {
                        callback.onError(RuntimeException("Body is empty"))
                        return
                    }

                    return callback.onSuccess(post)

                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun unlikeById(id: Long, callback: PostRepository.RepositoryCallback<Post>) {
        PostApi.service.unlikeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
//                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }
                    val post = response.body()

                    if (post == null) {
                        callback.onError(RuntimeException("Body is empty"))
                        return
                    }

                    callback.onSuccess(post)

                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.RepositoryCallback<Post>) {
        PostApi.service.savePost(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
//                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    val post = response.body()

                    if (post == null) {
                        callback.onError(RuntimeException("Body is empty"))
                        return
                    }

                    callback.onSuccess(post)
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })
    }

    override fun removeById(id: Long, callback: PostRepository.RepositoryCallback<Unit>) {
        PostApi.service.deletePost(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(NumberResponseError(response.code()))
//                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    callback.onSuccess(Unit)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }
}
