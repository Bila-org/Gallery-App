package com.example.mygallery.ui.common

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.isTraceInProgress
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.example.mygallery.R
import com.example.mygallery.data.MediaItem


@Composable
fun MediaItemView(
    mediaItem: MediaItem,
    onItemClick : (MediaItem) -> Unit,
    onBackClick: ()-> Unit = {}
){
    val context = LocalContext.current
    val thumbnailBitmap = remember(mediaItem.id){
        if(mediaItem.isVideo) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(
                        mediaItem.toUri(),
                        Size(512, 512),
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

    Box(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_extra_small))
            .aspectRatio(1f)
            //   .clip(shapes.small)
            .clickable { onItemClick(mediaItem) }
            .fillMaxSize()
            //.size(width = 300.dp, height = 400.dp)
    ){
        if(thumbnailBitmap != null){
            Image(
                bitmap = thumbnailBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        else {
            SubcomposeAsyncImage(
                model = mediaItem.uri,
                contentDescription = null,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        // If Video then display the Video Duration and Play icon in bottom end
        if(mediaItem.isVideo)
        {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimensionResource(R.dimen.padding_very_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(mediaItem.duration != null){
                    Text(
                        text = formatDuration(mediaItem.duration),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                        // .padding(dimensionResource(R.dimen.padding_extra_small))
                    )
                }
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Video",
                    modifier = Modifier
                        // .align(Alignment.TopEnd)
                        .padding(dimensionResource(R.dimen.padding_extra_small))
                        .size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}





@Composable
fun MediaItemViewSlow(
    mediaItem: MediaItem,
    onItemClick : (MediaItem) -> Unit
){
    val context = LocalContext.current
    val thumbnailBitmap = remember(mediaItem.id){
        if(mediaItem.isVideo) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(
                        mediaItem.toUri(),
                        Size(512, 512),
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

    Box(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_extra_small))
            .aspectRatio(1f)
            //   .clip(shapes.small)
            .clickable { onItemClick(mediaItem) }
    ){
        if(thumbnailBitmap != null){
            Image(
                bitmap = thumbnailBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        else {
            SubcomposeAsyncImage(
                model = mediaItem.uri,
                contentDescription = null,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        // If Video then display the Video Duration and Play icon in bottom end
        if(mediaItem.isVideo)
        {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimensionResource(R.dimen.padding_very_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(mediaItem.duration != null){
                    Text(
                        text = formatDuration(mediaItem.duration),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                        // .padding(dimensionResource(R.dimen.padding_extra_small))
                    )
                }
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Video",
                    modifier = Modifier
                        // .align(Alignment.TopEnd)
                        .padding(dimensionResource(R.dimen.padding_extra_small))
                        .size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}


/*
@Composable
fun MediaItemView(
    mediaItem: MediaItem,
    onItemClick : (MediaItem) -> Unit
){
   // val context = LocalContext.current
  //  val thumbnailBitmap = remember(mediaItem.id){
    /*    if(mediaItem.isVideo) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(
                        mediaItem.uri,
                        Size(512, 512),
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
*/
    Box(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_extra_small))
            .aspectRatio(1f)
            //   .clip(shapes.small)
            .clickable { onItemClick(mediaItem) }
    ){
        if(mediaItem.isVideo){
            VideoThumbnail(mediaItem.uri)
        }else {
            Image(
                painter = rememberAsyncImagePainter(model = mediaItem.uri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        /*
        if(thumbnailBitmap != null){
            Image(
                bitmap = thumbnailBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        else {
            AsyncImage(
                model = mediaItem.uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }*/


        // If Video then display the Video Duration and Play icon in bottom end
        if(mediaItem.isVideo)
        {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimensionResource(R.dimen.padding_very_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(mediaItem.duration != null){
                    Text(
                        text = formatDuration(mediaItem.duration),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                        // .padding(dimensionResource(R.dimen.padding_extra_small))
                    )
                }
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Video",
                    modifier = Modifier
                        // .align(Alignment.TopEnd)
                        .padding(dimensionResource(R.dimen.padding_extra_small))
                        .size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun VideoThumbnail(videoUri: Uri) {
    // Use Coil to load the video thumbnail
    val context = LocalContext.current
    val thumbnailPainter = // Use the first frame as the thumbnail
        rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = videoUri).apply(block = fun ImageRequest.Builder.() {
                videoFrameMillis(1000) // Use the first frame as the thumbnail
            }).build()
        )
    Image(
        painter = thumbnailPainter,
        contentDescription = "Video Thumbnail",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
*/

