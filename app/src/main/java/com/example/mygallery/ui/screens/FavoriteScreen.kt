package com.example.mygallery.ui.screens

import android.net.Uri
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mygallery.data.MediaFilter
import com.example.mygallery.data.MediaItem
import com.example.mygallery.model.GalleryUiState
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.GalleryAppTopBar
import com.example.mygallery.ui.common.MediaItemGrid
import com.example.mygallery.ui.common.groupMediaByMonth
import com.example.mygallery.ui.navigation.GalleryDestination

@Composable
fun FavoritesScreen(
    viewModel: GalleryViewModel,
    uiState: GalleryUiState,
    onItemSelection: (Uri, Boolean) -> Unit,
    currentScreen: GalleryDestination,
    onBackClick : ()->Unit,
    modifier: Modifier = Modifier
){
    //viewModel.applyFilter(MediaFilter.FAVORITE)
    //val filterMediaList = uiState.filteredMediaItems
    //val groupedMedia = groupMediaByMonth(filterMediaList)


    viewModel.getMedia(MediaFilter.FAVORITE)
    val groupedMedia = groupMediaByMonth(uiState.filteredMediaItems)


    Scaffold(
        topBar = {
            GalleryAppTopBar(
                currentScreen = currentScreen,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding->

        MediaItemGrid(
            groupedMedia = groupedMedia,
            onItemSelection = onItemSelection,
            modifier = modifier,
            contentPadding = innerPadding
        )
    }

}