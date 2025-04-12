package com.example.mygallery.data

import android.app.Application
import android.provider.MediaStore.Images.Media
import androidx.room.Room

//import com.example.mygallery.model.GalleryViewModelFactory




class AppContainer (
    private val application: Application
) {
    // Room Database instance
    private val mediaDatabase by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            MediaDatabase::class.java,
            "media_database"
        ).build()
    }

    // MediaDao instance
    private val mediaDao by lazy{
        mediaDatabase.mediaDao()
    }

    val mediaRepositoryImpl: MediaRepositoryImpl by lazy {
        MediaRepositoryImpl(
            application.applicationContext,
            mediaDao = mediaDao)
    }
        //.apply {
        //mediaRepositoryImpl.schedulePeriodicCleanup() }

  /*  fun provideGalleryViewModelFactory(): GalleryViewModelFactory {
        return GalleryViewModelFactory(mediaRepository)
    }
*/
}