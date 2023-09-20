package ru.netology.nmedia.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.PushToken

class SendPushWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(
    context,
    params) {

     companion object {
         const val NAME = "SendPushWorker"
         const val TOKEN_KEY = "TOKEN_KEY"
     }
    override suspend fun doWork(): Result {
        val token = inputData.getString(TOKEN_KEY)

        val tokenDto = PushToken(token ?: Firebase.messaging.token.await())

        return runCatching {
            PostApi.service.sendPushToken(tokenDto)
        }.map {
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}