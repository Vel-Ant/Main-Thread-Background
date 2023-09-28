package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.UserRepository
import java.io.File
import javax.inject.Inject

class SignInAndUpViewModel @Inject constructor(
) : ViewModel() {

    private val repository = UserRepository()

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _avatar = MutableLiveData<PhotoModel?>(null)
    val avatar: LiveData<PhotoModel?>
        get() = _avatar


    fun login(login: String, pass: String) {
        viewModelScope.launch {
            try {
                val user = repository.updateUser(login, pass)
                _user.value = user
            } catch (e: Exception) {
                _state.value = FeedModelState(loginError = true)
            }
        }
    }

    fun regNewUser(login: String, pass: String, name: String) {
        viewModelScope.launch {
            try {
                val user = repository.registerUser(login, pass, name)
                _user.value = user
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun regNewUserWithPhoto(login: String, pass: String, name: String, media: MediaUpload) {
        viewModelScope.launch {
            try {
                _avatar.value?.file?.let { file ->
                    val user = repository.registerUserWithPhoto(
                        login = login,
                        pass = pass,
                        name = name,
                        media = MediaUpload(file)
                    )
                    _user.value = user
                }
            } catch (e: Exception) {
                _state.value = FeedModelState(error = true)
            }
        }
    }

    fun setAvatar(photoModel: PhotoModel) {
        _avatar.value = photoModel
    }

    fun changeAvatar(uri: Uri, file: File) {
        _avatar.value = PhotoModel(uri, file)
    }

    fun clearAvatar() {
        _avatar.value = null
    }
}