package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth
) : ViewModel() {
    val data = appAuth.authState.asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = data.value?.token != null
}