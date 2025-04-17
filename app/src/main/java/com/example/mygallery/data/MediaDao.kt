package com.example.mygallery.data

import com.example.mygallery.data.MediaItem
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Query("SELECT * FROM MediaItem WHERE isTrashed = 0")
    suspend fun getMediaItems(): List<MediaItem>

    @Query("SELECT * FROM MediaItem WHERE isFavorite = 1 AND isTrashed = 0")
    suspend fun getFavorites(): List<MediaItem>

    @Query("SELECT * FROM MediaItem WHERE isTrashed = 1")
    suspend fun getTrashedItems(): List<MediaItem>

    @Query("UPDATE MediaItem SET isTrashed = 1, dateTrashed = :timestamp WHERE id = :id")
    suspend fun trashMedia(id: Long, timestamp: Long)

    @Query("DELETE FROM MediaItem WHERE isTrashed = 1")
    suspend fun emptyTrash()

    @Delete
    suspend fun deleteMedia(mediaItem: MediaItem)

    @Upsert
    suspend fun upsertMedia(mediaItem : MediaItem)

    @Query("SELECT * FROM MediaItem WHERE isVideo = 0 AND isTrashed = 0")
    suspend fun getImageItems(): List<MediaItem>

    @Query("SELECT * FROM MediaItem WHERE isVideo = 1 AND isTrashed = 0")
    suspend fun getVideoItems(): List<MediaItem>
}