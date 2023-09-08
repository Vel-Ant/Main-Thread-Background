package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel : ViewModel() {
    val data = AppAuth.getInstance().authState.asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = data.value?.token != null
}