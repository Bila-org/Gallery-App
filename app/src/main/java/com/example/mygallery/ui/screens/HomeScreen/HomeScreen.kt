@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mygallery.ui.screens.HomeScreen

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.mygallery.R
import com.example.mygallery.data.MediaFilter
import com.example.mygallery.data.MediaItem
import com.example.mygallery.model.GalleryUiState
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.common.MediaItemView
import com.example.mygallery.ui.common.groupMediaByMonth
import com.example.mygallery.ui.screens.FolderViewGrid
import com.example.mygallery.ui.theme.shapes


@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(
    viewModel: GalleryViewModel,
    uiState: GalleryUiState,
    onFolderClick: (String) -> Unit ,
    onMediaCardSelection: (String) -> Unit,
    onItemSelection: (Uri, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    //viewModel.loadMedia(MediaFilter.ALL)

    //viewModel.applyFilter(MediaFilter.ALL)
    // val mediaList = uiState.mediaItems
    //val filterMediaList = uiState.filteredMediaItems
    //val groupedMedia = groupMediaByMonth(filterMediaList)

   // viewModel.loadMedia(MediaFilter.ALL)


    viewModel.getMedia(MediaFilter.ALL)
    val groupedMedia = groupMediaByMonth(uiState.filteredMediaItems)

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

  //  var selectedFolder by remember { mutableStateOf<String?>(null) }

    var isFolderView by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HomeTopAppBar(scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            HomeBottomAppBar(
                onMediaSelect = { isFolderView = false },
                onFolderSelect = { isFolderView = true },
                isFolderView = isFolderView
            )
        },
      //  containerColor = MaterialTheme.colorScheme.secondaryContainer ,
     //   contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) { innerPadding ->

        if (isFolderView)
        {
            val folders: Map<String, List<MediaItem>> = viewModel.loadAllFolders()
                FolderViewGrid(
                    groupedMedia = folders,
                    onFolderClick =  onFolderClick,
                  //  modifier = modifier
                    //    .padding(top = 100.dp),
                    contentPadding = innerPadding
                )

        } else {
            HomeMediaItemGrid(
                groupedMedia = groupedMedia,
                onItemSelection = onItemSelection,
                onMediaCardSelection = onMediaCardSelection,
                modifier = modifier,
                contentPadding = innerPadding
            )
        }
    }
}

@Composable
fun HomeMediaItemGrid(
    groupedMedia: Map<String, List<MediaItem>>,
    onMediaCardSelection: (String) -> Unit,
    onItemSelection: (Uri, Boolean) -> Unit,
    modifier: Modifier,
    contentPadding : PaddingValues
) {
    LazyVerticalGrid(
        //columns = GridCells.Fixed(3),
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
        //  contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
            contentPadding = contentPadding
    ) {
        item(span = {
            GridItemSpan(maxLineSpan)
        }) {
            // Use LazyRow to display the cards horizontally
            Card(
                modifier = Modifier
                    .padding(top = 6.dp),
                //    .padding(dimensionResource( R.dimen.padding_very_small)),
                //shape = shapes.extraLarge,
                shape = CircleShape,
               // elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceDim,
                    contentColor = Color.Black
                ),
                //border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Green border
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                        //.padding(top = 100.dp, start = 16.dp, end = 16.dp),
                      //  .background(MaterialTheme.colorScheme.surfaceVariant),
                    horizontalArrangement = Arrangement.SpaceEvenly, // Add spacing between cards
                ) {
                    itemsIndexed(mediaClassifications) { index, item ->
                        MediaClassificationCard(
                            imageVector = item.imageVector,
                            contentDescription = item.contentDescription,
                            iconTint = item.iconTint,
                            cardBackground = item.cardBackground,
                            text = item.text,
                            onClick = { onMediaCardSelection(item.text) },
                            //{viewModel.applyFilter(item.filter) }
                        )
                    }
                }
            }
        }

        groupedMedia.forEach { (month, mediaItems) ->
            item(span = {
                GridItemSpan(maxLineSpan)
            }) {
                Text(
                    text = month, modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_medium),
                        vertical = dimensionResource(R.dimen.header_padding)
                    ),
                    //    .fillMaxWidth(),
                    // fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(mediaItems, key = { media ->
                media.id
            }) { media ->
                MediaItemView(mediaItem = media, onItemClick = {
                    onItemSelection(media.toUri(), media.isVideo)
                })
            }
        }
    }
}


/*
LazyVerticalGrid(
columns = GridCells.Fixed(3),
//  contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
contentPadding = innerPadding
) {
    groupedMedia.forEach { (month, mediaItems) ->
        item {
            Text(
                text = month,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(mediaItems) { media ->
            MediaItemView(
                mediaItem = media,
                onItemClick = {
                    if (media.isVideo) {
                        selectedVideoUri = media.uri
                    } else {
                        selectedImageUri = media.uri
                    }
                }
            )
        }
    }
}
*/

/*
@Composable
private fun MediaViewGrid(
    groupedMedia: Map<String, MutableList<MediaItem>>,
    modifier: Modifier = Modifier
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
          contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
        //contentPadding = modifier
    ) {
        groupedMedia.forEach { (month, mediaItems) ->
            item {
                Text(
                    text = month,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(mediaItems) { media ->
                MediaItemView(
                    mediaItem = media,
                    onItemClick = {
                        if (media.isVideo) {
                            selectedVideoUri = media.uri
                        } else {
                            selectedImageUri = media.uri
                        }
                    }
                )
            }
        }
    }
}*/