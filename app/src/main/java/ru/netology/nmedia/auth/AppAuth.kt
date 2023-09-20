package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.*

import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.workers.SendPushWorker

class AppAuth private constructor(private val context: Context) {
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

//        sendPushToken()
    }

    // Сохранение учетной записи
    @Synchronized
    fun setAuth(id: Long, token: String) {
        prefs.edit {
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)
        }
        _authState.value = Token(id, token)

        sendPushToken()
    }

    // все очистить
    @Synchronized
    fun removeAuth() {
        prefs.edit { clear() }
        _authState.value = null

        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            SendPushWorker.NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<SendPushWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(SendPushWorker.TOKEN_KEY, token)
                        .build()
                )
                .build()
            )

//        CoroutineScope(Dispatchers.Default).launch {
//            val tokenDto = PushToken(token ?: Firebase.messaging.token.await())
//
//            runCatching {
//                PostApi.service.sendPushToken(tokenDto)
//            }
//        }
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