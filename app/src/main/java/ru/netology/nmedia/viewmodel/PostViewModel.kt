package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.errors.NumberResponseError
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
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
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(posts = it, empty = it.isEmpty())
    }
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun refresh() {
        _state.value = FeedModelState(refreshing = true)
        viewModelScope.launch {
            try {
                repository.getAll()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = false)
                if (e is NumberResponseError) {
                    _state.value = FeedModelState(
                        codeResponse = e.code, error = true
                    )
                } else {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
    }
    fun loadPosts() {
        _state.value = FeedModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getAll()
                _state.value = FeedModelState()
            } catch (e: Exception) {
                _state.value = FeedModelState(error = false)
                if (e is NumberResponseError) {
                    _state.value = FeedModelState(
                        codeResponse = e.code, error = true
                    )
                } else {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            edited.value?.let {
                try {
                    repository.save(it)
                    _state.value = FeedModelState()
                } catch (e: Exception) {
                    _state.value = FeedModelState(error = false)
                    if (e is NumberResponseError) {
                        _state.value = FeedModelState(
                            codeResponse = e.code, error = true
                        )
                    } else {
                        _state.value = FeedModelState(error = true)
                    }
                }
            }
            edited.value = empty
        }
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
        viewModelScope.launch {
            try {
                repository.likeById(id)
                _state.value = FeedModelState(
                    error = false, codeResponse = null
                )
            } catch (e: Exception) {
                _state.value = FeedModelState(error = false)
                if (e is NumberResponseError) {
                    _state.value = FeedModelState(
                        codeResponse = e.code, error = true
                    )
                } else {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
    }

    fun unlikeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.unlikeById(id)
                _state.value = FeedModelState(
                    error = false, codeResponse = null
                )
            } catch (e: Exception) {
                _state.value = FeedModelState(error = false)
                if (e is NumberResponseError) {
                    _state.value = FeedModelState(
                        codeResponse = e.code, error = true
                    )
                } else {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
    }

    fun removeById(id: Long) {
        val old = data.value?.posts.orEmpty()
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _state.value = FeedModelState(
                    error = false, codeResponse = null
                )
            } catch (e: Exception) {
                data.value?.copy(posts = old)
                _state.value = FeedModelState(error = false)
                if (e is NumberResponseError) {
                    _state.value = FeedModelState(
                        codeResponse = e.code, error = true
                    )
                } else {
                    _state.value = FeedModelState(error = true)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}