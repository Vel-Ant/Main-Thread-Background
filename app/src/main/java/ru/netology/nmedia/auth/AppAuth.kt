package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.dto.Token

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow<Token?>(null)
    val authState = _authState.asStateFlow()

    init {
        val id = prefs.getLong(ID_KEY, 0)
        val token = prefs.getString(TOKEN_KEY, null)

        if (!prefs.contains(ID_KEY) || token == null) {
            prefs.edit { clear() }
        } else {
            _authState.value = Token(id, token)
        }
    }

    // Сохранение учетной записи
    @Synchronized
    fun setAuth(id: Long, token: String) {
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        _authState.value = Token(id, token)
    }

    // все очистить
    @Synchronized
    fun removeAuth() {
        prefs.edit { clear() }
        _authState.value = null
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"

        @Volatile
        private var INSTANCE: AppAuth? = null
        fun getInstance(): AppAuth = requireNotNull(INSTANCE)

        fun initApp(context: Context) {
            INSTANCE = AppAuth(context)
        }
    }
}