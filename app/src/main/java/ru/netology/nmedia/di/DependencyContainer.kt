package ru.netology.nmedia.di

import androidx.room.Room
import ru.netology.nmedia.db.AppDb
import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.util.concurrent.TimeUnit

//
//class DependencyContainer(
//    private val context: Context
//) {
//    companion object {
//        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
//
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        fun initApp(context: Context) {
//            instance = DependencyContainer(context)
//        }
//
//        fun getInstance(): DependencyContainer {
//            return instance!!
//        }
//    }
//
//    val appAuth = AppAuth(context)
//
//    private val authInterceptor = Interceptor { chain ->
//        val request = appAuth.authState.value?.token?.let {
//            chain.request()
//                .newBuilder()
//                .addHeader("Authorization", it)
//                .build()
//        } ?: chain.request()
//
//        chain.proceed(request)
//    }
//
//    private val loggingInterceptor = HttpLoggingInterceptor().apply {
//        if (BuildConfig.DEBUG) {
//            level = HttpLoggingInterceptor.Level.BODY
//        } else {
//            level = HttpLoggingInterceptor.Level.NONE
//        }
//    }
//
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .addInterceptor(loggingInterceptor)
//        .addInterceptor(authInterceptor)
//        .build()
//
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(client)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
//        .fallbackToDestructiveMigration()
//        .allowMainThreadQueries()
//        .build()
//
//    private val postDao = appDb.postDao()
//
//    val apiService = retrofit.create<PostApiService>()
//
//    val repository: PostRepository = PostRepositoryImpl(
//        postDao,
//        apiService
//    )
//}