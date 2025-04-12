package com.example.mygallery

import android.app.Application
import com.example.mygallery.data.AppContainer

class GalleryApplication : Application() {
    lateinit var appContainer : AppContainer
    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this).apply {
            mediaRepositoryImpl
        }
    }
}