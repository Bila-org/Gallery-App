package com.example.mygallery.ui.common

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.mygallery.R
import com.example.mygallery.data.MediaItem
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.screens.HomeScreen.MediaClassificationCard
import com.example.mygallery.ui.screens.HomeScreen.mediaClassifications

@Composable
fun MediaItemGrid(
    groupedMedia: Map<String, List<MediaItem>>,
    onItemSelection: (Uri, Boolean)->Unit,
    modifier: Modifier,
    contentPadding: PaddingValues
){
    LazyVerticalGrid(
        //columns = GridCells.Fixed(3),
        columns = GridCells.Adaptive(minSize = 120.dp),
        //modifier = modifier
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background),
        contentPadding = contentPadding
    ) {
        groupedMedia.forEach { (month, mediaItems) ->
            item(span = {
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
            }

            items(
                mediaItems,
                key = {
                    media ->
                    media.id
                }) { media ->
                MediaItemView(
                    mediaItem = media,
                    onItemClick = {
                        onItemSelection(media.toUri(), media.isVideo)
                    }
                )
            }
        }
    }
}
