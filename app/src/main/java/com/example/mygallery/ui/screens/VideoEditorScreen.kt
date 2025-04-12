package com.example.mygallery.ui.screens

import MediaEditorState
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun VideoEditorScreen(
    videoUri: Uri,
    onSave: (Uri) -> Unit,
    onCancel: ()-> Unit,
    modifier: Modifier = Modifier
) {
}


@Composable
fun VideoEditor(
    state: MediaEditorState,
    onEditApplied: (Uri) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
    ){
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = ExoPlayer.Builder(ctx).build().also {
                        state.videoPlayer = it
                        it.setMediaItem(MediaItem.fromUri(state.originalUri))
                        it.prepare()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .graphicsLayer {
                    rotationZ = state.rotation
                    alpha = state.alpha
                }
        )

        // Trim timeline overlay
        VideoTrimOverlay(state)

        // Editing controls
        VideoEditControls(
            state = state,
            onApply = {
                applyVideoEdits(state = state, onResult = onEditApplied)
            }
        )
    }

    LaunchedEffect(state.videoTrimRange) {
        state.videoPlayer?.let { player ->
            val duration = player.duration.coerceAtLeast(1)
            val start = (state.videoTrimRange.start * duration).toLong()
            player.seekTo(start)
        }
    }
}

@Composable
fun VideoTrimOverlay(state:MediaEditorState){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            // .align(Alignment.BottomCenter)
            .background(Color.Black.copy(alpha = 0.3f))
    ){
        // Thumbnail strip would go here
        RangeSlider(
            value = state.videoTrimRange,
            onValueChange = { state.videoTrimRange = it },
            valueRange = 0f..1f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp))
    }
}


@Composable
fun applyVideoEdits(
    state:MediaEditorState,
    onResult: (Uri) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        scope.launch(Dispatchers.IO) {
            //val outputUri = createTempVideoUri(context)
            val duration = state.videoPlayer?.duration ?: 0L

            // Using MediaCodec (simplified example)
            //val mediaMuxer = MediaMuxer(outputUri.path!!, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)


            try{
                // Actual implementation would:
                // 1. Extract segment between trim points
                // 2. Apply transformations
                // 3. Re-encode with new parameters

                withContext(Dispatchers.Main){
                    //  state.finalVideoUri = ouptputUri
                    //  onResult(outputUri)
                }
            }
            catch (e: Exception){
                // Handle error
            } finally {
                //mediaMuxer.release()
            }
        }
    }
}


@Composable
fun VideoEditControls(
    state: MediaEditorState,
    onApply:@Composable () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = {
                onApply
            },
            modifier = Modifier.padding(top = 8.dp)
        ){
            Text("Apply Edits")
        }
    }
}

