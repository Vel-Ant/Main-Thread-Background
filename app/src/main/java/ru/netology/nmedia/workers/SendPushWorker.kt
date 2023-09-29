package ru.netology.nmedia.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dto.PushToken

class SendPushWorker(
    @ApplicationContext
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(
    context,
    params) {

     companion object {
         const val NAME = "SendPushWorker"
         const val TOKEN_KEY = "TOKEN_KEY"
     }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getPostApiService(): PostApiService
    }

    override suspend fun doWork(): Result {
        val token = inputData.getString(TOKEN_KEY)

        val tokenDto = PushToken(token ?: Firebase.messaging.token.await())

        return runCatching {
            val entryPoint =
                EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            entryPoint.getPostApiService().sendPushToken(tokenDto)
        }.map {
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}