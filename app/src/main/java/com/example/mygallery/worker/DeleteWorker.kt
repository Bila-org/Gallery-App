package com.example.mygallery.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mygallery.GalleryApplication
import kotlinx.coroutines.delay

class DeleteWorker (
    context: Context, workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {



    override suspend fun doWork(): Result {
        try {
            (applicationContext as GalleryApplication)
                .appContainer.mediaRepositoryImpl
                .cleanupOldTrashItems()

            return Result.success()

        } catch (e: Exception){
            return Result.retry()
        }
        }
}