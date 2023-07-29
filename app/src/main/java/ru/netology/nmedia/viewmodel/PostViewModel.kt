package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.AttachmentType
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    attachment = Attachment("", " ", AttachmentType.IMAGE)
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.RepositoryCallback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.value = FeedModel(posts = value, empty = value.isEmpty())
            }

            override fun onError(exception: Exception) {
                _data.value = FeedModel(error = true)
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.RepositoryCallback<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(exception: Exception) {
                    _data.value = FeedModel(error = true)
                }
            })
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(
            content = text
        )
    }

    fun likeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.likeById(id, object : PostRepository.RepositoryCallback<Post> {
            override fun onSuccess(value: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) value else it
                        }
                    )
                )
            }

            override fun onError(exception: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
                _data.value = FeedModel(error = true)
            }
        })
    }

    fun unlikeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        repository.unlikeById(id, object : PostRepository.RepositoryCallback<Post> {
            override fun onSuccess(value: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) value else it
                        }
                    )
                )
            }

            override fun onError(exception: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
                _data.value = FeedModel(error = true)
            }
        })
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()

        repository.removeById(id, object : PostRepository.RepositoryCallback<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(exception: Exception) {
//                _data.postValue(_data.value?.copy(posts = old))
                _data.value = FeedModel(error = true)
            }
        })
    }
}