package ru.netology.nmedia

import android.app.Application
import ru.netology.nmedia.auth.AppAuth

class NMediaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        AppAuth.initApp(this)
    }
}