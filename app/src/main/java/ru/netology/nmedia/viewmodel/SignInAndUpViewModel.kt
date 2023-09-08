package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.UserRepository

class SignInAndUpViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

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

}