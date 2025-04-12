package com.example.mygallery.ui.screens

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.mygallery.R
import com.example.mygallery.data.MediaItem
import com.example.mygallery.ui.common.MediaItemView

@Composable
fun FolderViewGrid(
    groupedMedia: Map<String, List<MediaItem>>,
    onFolderClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        //  contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        contentPadding = contentPadding,

    ) {
        groupedMedia.forEach { (folderName, mediaItems) ->
            item {
                // Display a single media item as folder card
                val firstMedia = mediaItems.firstOrNull()
                if (firstMedia != null) {
                    FolderCard(
                        mediaItem = firstMedia,
                        folderName = folderName ?: "Uncategorized",
                        onClick = { onFolderClick(folderName) }
                    )
                }
            }

        }
    }
}


@Composable
fun FolderCard(
    mediaItem: MediaItem,
    folderName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val thumbnailBitmap = remember(mediaItem.id){
        if(mediaItem.isVideo) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(
                        mediaItem.toUri(),
                        Size(150,150),
                        null
                    )
                } else {
                    MediaStore.Video.Thumbnails.getThumbnail(
                        context.contentResolver,
                        mediaItem.id,
                        MediaStore.Video.Thumbnails.MINI_KIND,
                        null
                    )
                }
            } catch (e: Exception) {
                Log.e("GalleryApp", "Failed to load thumbnail : ${e.message}")
                null
            }
        } else {
            null
        }
    }



    Column(
    ) {
        // Display the media item (e.g., thumbnail)
        Card(
            onClick = onClick,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            if(thumbnailBitmap != null){
                Image(
                    bitmap = thumbnailBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
            else {
                 Image(
                //SubcomposeAsyncImage(
                      painter = rememberAsyncImagePainter(model = mediaItem.uri),
                    //model = mediaItem.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.medium
                        )
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                            ),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentScale = ContentScale.Crop
                )
            }
        }
        // Display the folder name
        Text(
            text = folderName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun FolderMediaItemGrid(
    mediaItems: List<MediaItem>,
    onItemSelection: (Uri, Boolean) -> Unit,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        //columns = GridCells.Fixed(3),
        columns = GridCells.Adaptive(minSize = 120.dp),
        //  contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small))
            contentPadding = contentPadding
        //modifier = modifier
    ) {
        items(mediaItems) { media ->
            MediaItemView(
                mediaItem = media,
                onItemClick = {
                    onItemSelection(media.toUri(), media.isVideo)
                },
                onBackClick = onBackClick
            )
        }
    }
}
