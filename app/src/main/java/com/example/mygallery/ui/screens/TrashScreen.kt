package com.example.mygallery.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygallery.R
import com.example.mygallery.data.MediaFilter
import com.example.mygallery.data.MediaItem
import com.example.mygallery.model.GalleryUiState
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.GalleryAppTopBar
import com.example.mygallery.ui.common.MediaItemView
import com.example.mygallery.ui.common.groupMediaByMonth
import com.example.mygallery.ui.navigation.GalleryDestination

@Composable
fun TrashScreen(
    viewModel: GalleryViewModel,
    uiState: GalleryUiState,
    onItemSelection: (Uri, Boolean) -> Unit,
    currentScreen: GalleryDestination,
    onBackClick: ()-> Unit,
    modifier: Modifier = Modifier
) {
    // viewModel.applyFilter(MediaFilter.TRASH)
    // val filterMediaList = uiState.filteredMediaItems
    // val groupedMedia = groupMediaByMonth(filterMediaList)


    // LaunchedEffect(Unit) {
    viewModel.getMedia(MediaFilter.TRASH)
    //}
    //val groupedMedia = groupMediaByMonth(uiState.filteredMediaItems)
    val groupedMedia by remember(uiState.filteredMediaItems) {
        derivedStateOf { groupMediaByMonth(uiState.filteredMediaItems) }
    }

    Scaffold(
        topBar = {
            GalleryAppTopBar(
                currentScreen = currentScreen,
                onBackClick = onBackClick
            )
        }

    ) { innerPadding ->
        if(uiState.isLoading){
            Box(){
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }else{
            TrashItemGrid(
                groupedMedia = groupedMedia,
                onItemSelection = onItemSelection,
                modifier = modifier,
                contentPadding = innerPadding,
                viewModel = viewModel
            )

        }
    }
}



@Composable
fun TrashItemGrid(
    viewModel: GalleryViewModel,
    groupedMedia: Map<String, List<MediaItem>>,
    onItemSelection: (Uri, Boolean) -> Unit,
    modifier: Modifier,
    contentPadding: PaddingValues
) {
    LazyVerticalGrid(
        //columns = GridCells.Fixed(3),
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
        contentPadding = contentPadding
        //modifier = modifier
    ) {
        groupedMedia.forEach { (month, mediaItems) ->
            /*item(span = {
                GridItemSpan(maxLineSpan)
            }) {
                Text(
                    text = month,
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.padding_medium),
                            vertical = dimensionResource(R.dimen.header_padding)
                        ),
                    //    .fillMaxWidth(),
                    // fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }*/

            items(
                mediaItems,
                key = { media ->
                    media.id
                }) { media ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    MediaItemView(
                        mediaItem = media,
                        onItemClick = {
                            onItemSelection(media.toUri(), media.isVideo)
                        }
                    )
                    val duration = (viewModel.calculateRemainingTime(media)).toString()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .align(Alignment.BottomCenter)
                            //.background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.7f),
                                        Color.Transparent
                                    )
                                ),
                                RoundedCornerShape(4.dp)
                            )
                    ){

                        Text(
                            text = "$duration days",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier
                                //   .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .align(Alignment.BottomStart)
                            // .padding(8.dp)
                        )
                    }

                }
            }
        }
    }
}
