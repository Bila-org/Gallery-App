package com.example.mygallery.ui.screens.MediaViewerPagerScreen


import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


@Composable
fun FullScreenVideoPlayer (
    uri: Uri,
    isActive: Boolean = true,
    onPagerSwipeStateChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply{
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        //    playWhenReady = true // Play the video automatically
        }
    }


    LaunchedEffect(isActive) {
        onPagerSwipeStateChanged(!isActive)
        exoPlayer.playWhenReady = isActive
        if(!isActive) exoPlayer.pause()
    }


    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
        Box(
            modifier = Modifier
                .fillMaxSize()
                // .clipToBounds()
                .background(Color.Black)
                /*.pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isUiVisible = !isUiVisible
                        }
                    )
                }*/
          //      .navigationBarsPadding()
            //    .clipToBounds()
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    //.height(300.dp)
                    //.width(300.dp)
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 56.dp)
            )


        }
  //  }
     //BackHandler(onBack = onBackPressed)
}
