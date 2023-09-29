package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.errors.NumberResponseError
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.AttachmentType
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    attachment = Attachment("", AttachmentType.IMAGE),
    ownedByMe = false
)

@HiltViewModel
@ExperimentalCoroutinesApi
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    val data: LiveData<FeedModel> = appAuth.authState.flatMapLatest { token ->
        repository.data.map { posts ->
            FeedModel(posts.map {
                it.copy(ownedByMe = it.authorId == token?.id)
            }, posts.isEmpty())
        }
    }.asLiveData(Dispatchers.Default)

    private val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val newerCount: LiveData<Int> = data.switchMap {
        val id = it.posts.firstOrNull()?.id ?: 0L
        repository.getNewerCount(id).asLiveData(Dispatchers.Default)

    }

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

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

    fun loadAllNewPosts() {
        _state.value = FeedModelState(loading = true)
        viewModelScope.launch {
            try {
                repository.getAllNewPosts()
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
                    _photo.value?.let { photoModel ->
                        repository.saveWithAttachment(it, photoModel.file)
                    } ?: run { repository.save(it) }
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
            content = text,
//            published = java.text.SimpleDateFormat("dd.MM.yyyy' в 'HH:mm:ss' '")
//                .format(System.currentTimeMillis())
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

    fun setPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto() {
        _photo.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}