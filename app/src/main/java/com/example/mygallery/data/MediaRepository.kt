package com.example.mygallery.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mygallery.worker.DeleteWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.none
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit
import kotlin.math.max


@Entity
data class MediaItem(
    @PrimaryKey
    val id: Long,
    val uri: String,
    val name: String,
    val size: Long,
    val dateAdded: Long?,
    val mimeType: String,
    val duration: Long? = null,
    val folderName: String? = null,
    val isVideo: Boolean,
    val isFavorite: Boolean = false,
    val isTrashed: Boolean = false,
    val dateTrashed: Long? = null // For tracking when the media item was deleted
) {
    fun toUri(): Uri = Uri.parse(uri)
}

enum class MediaFilter {
    ALL, IMAGES, VIDEOS, FAVORITE, TRASH,
    SELFIES, SCREENSHOTS, FOLDERS
}


interface MediaRepository{

    suspend fun loadMedia():List<MediaItem>
    suspend fun getMedia(filter: MediaFilter):List<MediaItem>
    suspend fun updateMedia(mediaItem: MediaItem)


    // Functions for trash/favorites
//    suspend fun getFavorites(): List<MediaItem>
  //  suspend fun getTrashedItems(): List<MediaItem>

    fun calculateRemainingTime(mediaItem: MediaItem):Long


    suspend fun toggleFavorite(mediaItem: MediaItem)
    suspend fun trashMedia(mediaItem: MediaItem)
    suspend fun restoreMedia(mediaItem: MediaItem)
    suspend fun deleteMedia(mediaItem: MediaItem)
    suspend fun emptyTrash()
}


const val TRASH_RETENTION_DAYS = 30

class MediaRepositoryImpl (
    private val context: Context,
    private val mediaDao: MediaDao
): MediaRepository{

    private val workManager = WorkManager.getInstance(context)

    init{
        // starts when repository is first created
       // schedulePeriodicCleanup()
    }


    override suspend fun deleteMedia(mediaItem: MediaItem) =
        mediaDao.deleteMedia(mediaItem)

    /*override suspend fun getFavorites(): List<MediaItem> =
        mediaDao.getFavorites()
*/
    /*
    override suspend fun getTrashedItems(): List<MediaItem> =
        mediaDao.getTrashedItems()
*/

    override suspend fun updateMedia(mediaItem: MediaItem){
        mediaDao.upsertMedia(mediaItem)

        /* val existingItem = _mediaSet.find{it.id == mediaItem.id}
        if(existingItem != null) {
            _mediaSet.remove(existingItem)
            _mediaSet.add(mediaItem)
        }*/

        /*val index = mediaList.indexOfFirst { it.id == mediaItem.id}
        if(index != -1){
            mediaList[index] = mediaItem
        } */
    }

    override suspend fun toggleFavorite(mediaItem: MediaItem){
        mediaDao.upsertMedia(mediaItem.copy
            (isFavorite =  !mediaItem.isFavorite))
    }

    override suspend fun trashMedia(mediaItem: MediaItem){
        mediaDao.trashMedia(mediaItem.id, System.currentTimeMillis())

      /*  mediaDao.upsertMedia(mediaItem.copy(
            isTrashed = true,
            dateAdded = System.currentTimeMillis()
        ))
*/
    }

    override suspend fun restoreMedia(mediaItem: MediaItem){
        mediaDao.upsertMedia(mediaItem.copy(
            isTrashed = false,
            dateAdded = null
        ))
    }

    override suspend fun emptyTrash(){
        mediaDao.emptyTrash()
    }

    suspend fun deletePermanently(mediaItem: MediaItem){
        // Delete from storage
        context.contentResolver.delete(mediaItem.toUri(), null, null)
        // Delete from Database
        mediaDao.deleteMedia(mediaItem)
    }

    override fun calculateRemainingTime(mediaItem: MediaItem): Long {
        if (mediaItem.dateTrashed == null)
            return 0L
        val timePassed = System.currentTimeMillis() - mediaItem.dateTrashed

        return (max(0, (TRASH_RETENTION_DAYS * TimeUnit.DAYS.toMillis(1)) - timePassed)) / (1000*60*60*24)
    }

    suspend fun cleanupOldTrashItems(){
        // Determine the cutOffTime corresponds to TRASH_RETENTION_DAYS
        val cutOffTime = System.currentTimeMillis()
        - (TimeUnit.DAYS.toMillis(1) * TRASH_RETENTION_DAYS)

        // Get expired media list
        val expiredMediaList = mediaDao.getTrashedItems()
            .filter{it.dateTrashed ?: Long.MIN_VALUE <= cutOffTime}

        // Delete expired media items
        expiredMediaList.forEach { item ->
            deletePermanently(item)
        }

    }

    fun schedulePeriodicCleanup(){
        // Cancel any exiting work first to avoid duplicates
        workManager.cancelUniqueWork("periodic_trash_cleanup")


        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<DeleteWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodic_trash_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    val _mediaSet = mutableSetOf<MediaItem>()


    override suspend fun loadMedia(): List<MediaItem> {
        return  withContext(Dispatchers.IO) {
                    _mediaSet.clear()
                    val deviceMedia = loadAllMedia()
                    syncWithDatabase(deviceMedia)
                    mediaDao.getMediaItems()
        }
    }


    override suspend fun getMedia(filter: MediaFilter): List<MediaItem> {
         return  withContext(Dispatchers.IO) {
                 when (filter) {
                     MediaFilter.ALL -> {
                         mediaDao.getMediaItems()
                     }

                     MediaFilter.IMAGES -> {
                         mediaDao.getMediaItems().filter { !it.isVideo }
                     }

                     MediaFilter.VIDEOS -> {
                         mediaDao.getMediaItems().filter { it.isVideo }
                     }

                     MediaFilter.FAVORITE -> {
                         mediaDao.getFavorites()
                     }

                     MediaFilter.TRASH -> {
                         mediaDao.getTrashedItems()
                     }

                     else -> emptyList()
                 }
         }
     }
       /*  return withContext(Dispatchers.IO)
        {
            when(filter) {
                MediaFilter.ALL -> {
                    _mediaSet.clear()
                    loadAllMedia()
                    syncWithDatabase(_mediaSet.toList())
                    mediaDao.getMediaItems().first()
                    // _mediaSet.toList()
                }

                MediaFilter.IMAGES -> {
                   mediaDao.getMediaItems().filter {
                        !it.isVideo
                    }
                }
                    MediaFilter.VIDEOS -> {
                        mediaDao.getMediaItems().filter {
                            it.isVideo
                        }
                    }
                    MediaFilter.FAVORITE ->
                        mediaDao.getFavorites()
                    MediaFilter.TRASH ->
                        mediaDao.getTrashedItems()
                    else -> emptyList()
                }
        }
     }

*/


    private suspend fun syncWithDatabase(deviceMedia: List<MediaItem>) {
        val dbMedia = mediaDao.getMediaItems() +
                mediaDao.getFavorites() +
                mediaDao.getTrashedItems()

        // 1. Handle new media from device (not in database)
        val newMedia = deviceMedia.filter { deviceItem ->
            dbMedia.none { it.id == deviceItem.id }
        }.map { it.copy(isFavorite = false, isTrashed = false) } // New items default to not favorite/not trashed

        // 2. Handle existing media - preserve special states
        val existingMedia = deviceMedia.map { deviceItem ->
            dbMedia.find { it.id == deviceItem.id }?.let { dbItem ->
                // Keep the existing favorite/trash states from database
                deviceItem.copy(
                    isFavorite = dbItem.isFavorite,
                    isTrashed = dbItem.isTrashed,
                    dateTrashed = dbItem.dateTrashed
                )
            } ?: deviceItem // Shouldn't happen due to step 1
        }

        // 3. Remove only non-special items that disappeared from device
        val missingMedia = dbMedia.filter { dbItem ->
            deviceMedia.none { it.id == dbItem.id } &&
                    !dbItem.isFavorite &&
                    !dbItem.isTrashed // Don't delete trashed items
        }

        // Execute all operations
        //mediaDao.upsertMedia(newMedia + existingMedia)

        existingMedia.forEach{
            mediaDao.upsertMedia(it)
        }
        newMedia.forEach{
            mediaDao.upsertMedia(it)
        }
        missingMedia.forEach { mediaDao.deleteMedia(it) }
    }


    /*
    private suspend fun syncWithDatabase(deviceMedia: List<MediaItem>){
        val dbMedia = mediaDao.getMediaItems()

        // Add new media to Database
        val newMedia = deviceMedia.filter{deviceItem ->
            dbMedia.none{it.id == deviceItem.id} ?: true
        }

        // Insert each item one by one
        newMedia.forEach{item ->
            mediaDao.upsertMedia(item)
        }


        // Remove media that no longer exists on device
        val missingMedia = dbMedia.filter{ dbItem ->
            deviceMedia.none{it.id == dbItem.id} &&
                    !dbItem.isFavorite &&
                    !dbItem.isTrashed
        }

        missingMedia.forEach{
            mediaDao.deleteMedia(it)
        }

    }
*/
    private suspend fun loadAllMedia(): List<MediaItem>{
        return loadImages() + loadVideos()
    }

    private suspend fun loadImages(): List<MediaItem>{
        return queryMedia(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            ),
            sordOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC",
            isVideo = false
        )
    }

    private suspend fun loadVideos(): List<MediaItem>{
        val videos =  queryMedia(
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
            ),
            sordOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC",
            isVideo = true
        )
        Log.d("GalleryApp", "Fetched ${videos.size} videos")
        return videos
    }

    /*
    private suspend fun loadSelfies(): List<MediaItem>{
        return queryMedia(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            ),
            selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?",
            selectionArgs = arrayOf("Selfies"),
            sordOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC",
            isVideo = false
        )
    }
*/

    /*
    private suspend fun loadScreenshots(): List<MediaItem>{
        return queryMedia(
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            ),
            selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?",
            selectionArgs = arrayOf("Screenshots"),
            sordOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC",
            isVideo = false
        )
    }
    */


    private suspend fun queryMedia (
        uri: Uri,
        projection: Array<String>,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        sordOrder: String?= null,
        isVideo: Boolean
    ): List<MediaItem>{
       // return withContext(Dispatchers.IO)
      //  {
        //val mediaList = mutableListOf<MediaItem>()
            try {
                val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs,
                    sordOrder)
                cursor?.use{
                    val idColumn = it.getColumnIndexOrThrow(if(isVideo) MediaStore.Video.Media._ID else MediaStore.Images.Media._ID)
                    val nameColumn = it.getColumnIndexOrThrow(if(isVideo) MediaStore.Video.Media.DISPLAY_NAME else MediaStore.Images.Media.DISPLAY_NAME)
                    val sizeColumn = it.getColumnIndexOrThrow(if(isVideo) MediaStore.Video.Media.SIZE else MediaStore.Images.Media.SIZE)
                    val dateAddedColumn = it.getColumnIndexOrThrow(if(isVideo) MediaStore.Video.Media.DATE_ADDED else MediaStore.Images.Media.DATE_ADDED)
                    val mimTypeColumn = it.getColumnIndexOrThrow(if(isVideo) MediaStore.Video.Media.MIME_TYPE else MediaStore.Images.Media.MIME_TYPE)
                    val durationColumn = if (isVideo) {
                        it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                    } else {
                        null
                    }
                    val folderNameColumn = it.getColumnIndexOrThrow(if(isVideo) MediaStore.Video.Media.BUCKET_DISPLAY_NAME else MediaStore.Images.Media.BUCKET_DISPLAY_NAME)


                    while(it.moveToNext()){
                        val id = it.getLong(idColumn)
                        val name = it.getString(nameColumn)
                        val size = it.getLong(sizeColumn)
                        val dateAdded = it.getLong(dateAddedColumn) * 1000
                        val mimeType = it.getString(mimTypeColumn)
                        val folderName = it.getString(folderNameColumn)
                        val duration = if(isVideo) {it.getLong(durationColumn!!)} else {null}

                        val contentUri = if(isVideo){
                            ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                        } else {
                            ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        }
                         _mediaSet.add(MediaItem(
                            id = id,
                            uri = contentUri.toString(),
                            name = name,
                            size = size,
                            dateAdded = dateAdded,
                            mimeType = mimeType,
                            isVideo = isVideo,
                            folderName = folderName,
                            duration = duration
                        )
                        )
                    }
                }
            }
            catch (e: SecurityException){
                e.printStackTrace()
            } catch (e: Exception){
                e.printStackTrace()
            }
            return _mediaSet.toList()
        //}
    }
}