package com.example.mygallery.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.exoplayer.source.MediaSource.MediaPeriodId
import com.example.mygallery.GalleryApplication
import com.example.mygallery.data.MediaFilter
import com.example.mygallery.data.MediaItem
import com.example.mygallery.data.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*sealed interface GalleryUiState {
    data class Success(val mediaList: List<MediaItem>) : GalleryUiState
    object Error: GalleryUiState
    object Loading: GalleryUiState
}*/

data class GalleryUiState(
    val mediaItems: List<MediaItem> = emptyList(),
    val filteredMediaItems: List<MediaItem> = emptyList(),
    // val trashedMediaItems: List<MediaItem> = emptyList(),
    val currentFilter: MediaFilter = MediaFilter.ALL,
//    var isFavorite: Boolean = false,
    val isLoading: Boolean  = false,
    val error: String? = null
)



class GalleryViewModel (
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState


    init{
        loadAllMedia()
    }

    fun loadAllMedia(filter: MediaFilter = MediaFilter.ALL) {
        _uiState.update{
            it.copy(
                currentFilter = filter,
                mediaItems = emptyList(),
                isLoading = true)}
        viewModelScope.launch {
            /*  _uiState.update { it.copy(
                mediaItems = emptyList(),
               filteredMediaItems = emptyList(),
               isLoading = true, error = null) }
            */
            val mediaList = mediaRepository.loadMedia()
            _uiState.update { it.copy(
                mediaItems = mediaList,
                // filteredMediaItems = mediaList,
                isLoading = false
            ) }
        }
    }

    fun getMedia(filter:MediaFilter)  {
        /*_uiState.update{
            it.copy(
                //filteredMediaItems = emptyList(),
                currentFilter = filter,
                isLoading = true
            )
        }*/

        viewModelScope.launch {
            val mediaList = mediaRepository.getMedia(filter)
            _uiState.update { it.copy(
                filteredMediaItems = mediaList,
                isLoading = false
            ) }
        }
    }

    fun toggleFavorite(uri: Uri) = viewModelScope.launch {
        // Find the media item based on the Uri
        val mediaItem = _uiState.value.mediaItems.find{
            it.toUri() == uri
        }
        mediaItem?.let { mediaRepository.toggleFavorite(it) }
        //  refreshMedia()
    }

    fun trashMedia(uri: Uri) = viewModelScope.launch {
        // Find the media item based on the Uri
        val mediaItem = _uiState.value.mediaItems.find{
            it.toUri() == uri
        }
        mediaItem?.let { mediaRepository.trashMedia(it) }

        //  refreshMedia()
        /*
        _uiState.value.mediaItems.
        _uiState.{ it.copy(
            filteredMediaItems = mediaList,
            isLoading = false
        ) }
*/
    }

    suspend fun restoreMedia(mediaItem: MediaItem)
    {
        mediaRepository.restoreMedia(mediaItem)
    }

    fun emptyTrash() = viewModelScope.launch {
        mediaRepository.emptyTrash()
    }

    fun calculateRemainingTime(mediaItem: MediaItem): Long {
        return mediaRepository.calculateRemainingTime(mediaItem)
    }


    /*
    fun isFavorite(uri: Uri): Boolean{
        // Find the media item based on the Uri
        val mediaItem = _uiState.value.mediaItems.find{
            it.toUri() == uri
        }
        mediaItem
        uiState.mediaItems.find{it.toUri() == uri}?.isFavorite?:false
    }
    */

    fun loadAllFolders(): Map<String, List<MediaItem>>{
        return _uiState.value.filteredMediaItems.filter{it.folderName != null && !it.isTrashed}.groupBy {it.folderName!!}
        // .map{(folderName, mediaList) -> Folder(folderName, mediaList)}
    }


    suspend fun deletePermanently(mediaItem: MediaItem){
        //viewModelScope.launch {
        mediaRepository.deletePermanently(mediaItem)
        // }
    }



    private suspend fun refreshMedia() {
        val currentFilter = _uiState.value.currentFilter
        val mediaItems = mediaRepository.loadMedia()
        val filteredMediaItems = mediaRepository.getMedia(currentFilter)
        _uiState.update {
            it.copy(
                mediaItems = mediaItems,
                filteredMediaItems = filteredMediaItems,
                isLoading = false
            )
        }
    }




    /**
     * Factory for [MarsViewModel] that takes [MarsPhotosRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GalleryApplication)
                val mediaRepositoryImpl = application.appContainer.mediaRepositoryImpl
                GalleryViewModel(mediaRepository = mediaRepositoryImpl)
            }
        }
    }
}


/*  fun getFavorites() {
      viewModelScope.launch {
          val favoritesMediaItems = mediaRepository.getFavorites()
          _uiState.update { it.copy(
              filteredMediaItems = favoritesMediaItems,
              isLoading = false
          ) }
      }
  }

  fun getTrashedItems(){
      viewModelScope.launch {
          val trashedMediaItems = mediaRepository.getTrashedItems()
          _uiState.update { it.copy(
              filteredMediaItems = trashedMediaItems,
              isLoading = false
          ) }
      }
  }
*/

/*fun applyFilter(filter: MediaFilter){
       // _uiState.value = _uiState.value.copy(currentFilter = filter)
        val newFilter = filter
        viewModelScope.launch {
            val filteredItems = withContext(Dispatchers.Default) {
                _uiState.value.mediaItems.filter { item ->
                    when (newFilter) {
                        MediaFilter.ALL -> !item.isTrashed
                        // Add other filters as needed
                        MediaFilter.IMAGES -> !item.isVideo && !item.isTrashed
                        MediaFilter.VIDEOS -> item.isVideo && !item.isTrashed
                        MediaFilter.SELFIES -> true         // Not implemented yet
                        MediaFilter.SCREENSHOTS -> true     // Not implemented yet
                        MediaFilter.FOLDERS -> true         // Not implemented yet
                        MediaFilter.FAVORITE -> item.isFavorite && !item.isTrashed
                        MediaFilter.TRASH -> item.isTrashed
                    }
                }
            }
            _uiState.update{it.copy(
                currentFilter = newFilter,
                filteredMediaItems = filteredItems
            )}
        }
    }
*/

/*
    fun onFavoriteClick(imageUri: Uri){
        viewModelScope.launch {
            // Find the media item based on the Uri
            val mediaItem = _uiState.value.mediaItems.find{
                it.toUri() == imageUri
            }
            mediaItem?.let {item ->
                // Toggle the isFavorite state of the media item
                val updatedMediaItem = item.copy(isFavorite = !item.isFavorite)

                // Update the media item in the repository
                mediaRepository.updateMedia(updatedMediaItem)

                // Update the UI state
                _uiState.update{state ->
                    state.copy(
                        mediaItems = state.mediaItems.map{
                            if(it.id == item.id) updatedMediaItem else it
                        },
                        filteredMediaItems = state.filteredMediaItems.map{
                            if(it.id == item.id) updatedMediaItem else it
                        }
                        //     isFavorite = updatedMediaItem.isFavorite
                    )
                }
            }
        }
    }



    // Move the items to trash bin
    fun moveToTrash(imageUri: Uri){
        viewModelScope.launch {
            // Find the media item based on the Uri
            val mediaItem = _uiState.value.mediaItems.find{
                it.toUri() == imageUri
            }
            mediaItem?.let {item ->
                val updatedMediaItem = item.copy(
                    isTrashed = true,
                    dateTrashed = System.currentTimeMillis()
                )

                // Update the media item in the repository
                mediaRepository.updateMedia(updatedMediaItem)

                // Update the UI state
                _uiState.update{state ->
                    state.copy(
                        mediaItems = state.mediaItems.map{
                            if(it.id == mediaItem.id)
                                updatedMediaItem
                            else
                                it
                        },
                    )
                }
            }
        }
    }

    // Restore the images from the Trash
    fun restoreMedia(imageUri: Uri){
        viewModelScope.launch {
            // Find the media item based on the Uri
            val mediaItem = _uiState.value.mediaItems.find{
                it.toUri() == imageUri
            }
            mediaItem?.let {item ->
                // Toggle the isFavorite state of the media item
                val updatedMediaItem = item.copy(
                    isTrashed = false,
                    dateTrashed = null
                )

                // Update the media item in the repository
                mediaRepository.updateMedia(updatedMediaItem)

                // Update the UI state
                _uiState.update{state ->
                    state.copy(
                        mediaItems = state.mediaItems.map{
                            if(it.id == mediaItem.id)
                                updatedMediaItem
                            else
                                it
                        },
                    )

                }
            }
        }
    }
*/

/*
class GalleryViewModelFactory(
    private val mediaRepository: MediaRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GalleryViewModel::class.java)) {
            return GalleryViewModel(mediaRepository) as T
        }
        throw IllegalArgumentException("Unknown")
    }
}*/