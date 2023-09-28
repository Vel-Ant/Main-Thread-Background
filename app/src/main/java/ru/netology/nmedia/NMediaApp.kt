package ru.netology.nmedia

import android.app.Application
import ru.netology.nmedia.di.DependencyContainer

class NMediaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyContainer.initApp(this)
    }
}