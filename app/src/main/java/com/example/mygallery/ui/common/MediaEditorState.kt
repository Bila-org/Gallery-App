
@file:OptIn(ExperimentalMaterial3Api::class)

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaMuxer
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.outlined.RotateLeft
import androidx.compose.material.icons.automirrored.outlined.RotateRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.mygallery.ui.screens.ImageEditorScreen.ImageFilter
import com.example.mygallery.ui.screens.VideoEditor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MediaEditorState(
    val originalUri: Uri,
    val type: MediaType
) {
    // Real time preview
    var rotation by mutableStateOf(0f)
    var scale by mutableStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)
    var alpha by mutableStateOf(1f)
    var colorFilter by mutableStateOf<ColorFilter?>(null)

    // For final rendering
    var finalBitmap by mutableStateOf<Bitmap?>(null)
    var finalVideoUri by mutableStateOf<Uri?>(null)

    // Video specific
    var videoTrimRange by mutableStateOf(0f..1f)
    var videoPlayer: ExoPlayer? = null
}

@Composable
fun rememberEditorState(uri: Uri, type: MediaType): MediaEditorState{
    return remember(uri){
        MediaEditorState(uri,type)
    }
}


enum class MediaType{
    IMAGE,
    VIDEO
}

/*
@Composable
fun ImageEditorScreen(
    imageUri: Uri,
    onSave: (Uri) -> Unit,
    onCancel: ()-> Unit,
    modifier: Modifier = Modifier

){*/

@Composable
fun MediaEditorScreen(
    mediaUri: Uri,
    isVideoMedia: Boolean = false,
    onSave: (Uri) -> Unit,
    onCancel: ()-> Unit,
    modifier: Modifier = Modifier

){

    val mediaType = if(isVideoMedia) MediaType.VIDEO else
        MediaType.IMAGE
    val state = rememberEditorState(mediaUri, mediaType)
    var showSaveDialog by remember { mutableStateOf(false) }

    /*
    Scaffold(
        topBar ={
            TopAppBar(
                actions = {
                    IconButton(
                        onClick = { showSaveDialog = true }
                    ) {
                        Icon(Icons.Default.Save, "Save")
                    }
                },
                title = { Text(text = "Media Editor") },
                //modifier = TODO(),
               // navigationIcon = TODO(),
               // expandedHeight = TODO(),
               // windowInsets = TODO(),
               // colors = TODO(),
               // scrollBehavior = TODO()
            )
        }
    ) { padding ->

     */
    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .statusBarsPadding()
    ){
        when(mediaType){
            MediaType.IMAGE ->
                ImageEditor(
                    state = state,
                    onEditApplied = {bitmap ->
                        state.finalBitmap = bitmap
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                    //.fillMaxSize()

                )
            MediaType.VIDEO -> {
                VideoEditor(
                    state = state,
                    onEditApplied = {uri ->
                        state.finalVideoUri = uri
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                    //.fillMaxSize()
                )
            }
        }
        // Spacer(modifier = Modifier
        //   .weight(1f))
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 20.dp, start = 30.dp, end= 30.dp)
        ){
            Button(
                onClick = {},
                //colors = ButtonDefaults.buttonColors(Color.Red),
            ) {
                Text(
                    text = "Cancel"
                )
            }
            Spacer(modifier = Modifier
                .weight(1f))
            Button(
                onClick = {showSaveDialog = true},
                //colors = ButtonDefaults.buttonColors(Color.Green),
            ) {
                Text(
                    text = "Save"
                )
            }
        }
    }

    if(showSaveDialog){
        SaveDialog(
            state = state,
            onDismiss = {
                showSaveDialog = false
            }
        )
    }
}


@Composable
fun ImageEditor(
    state: MediaEditorState,
    onEditApplied:(Bitmap) -> Unit = {},
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment =  Alignment.CenterHorizontally,
    ){
        AsyncImage(
            model = state.originalUri,
            contentDescription = "Editable Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                //.fillMaxSize()
                .graphicsLayer {
                    rotationZ = state.rotation
                    scaleX = state.scale
                    scaleY = state.scale
                    translationX = state.offset.x
                    translationY = state.offset.y
                    alpha = state.alpha
                },
            colorFilter = state.colorFilter
        )
        Spacer(modifier = Modifier.height(50.dp))

        ImageEditControls(
            state = state,
            onApply = {
                applyImageEdits(state, onEditApplied)
            }
        )
    }
}

@Composable
fun ImageEditControls(
    state: MediaEditorState,
    onApply: @Composable ()-> Unit
){
    Column(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // Rotation control
        RotationControl(state)

        // Scale control
        ScaleControl(state)

        // color filters
        FilterSelector(state)

        Button(
            onClick = {onApply},
            modifier = Modifier.padding(top = 8.dp)
        ){
            Text("Apply Edits")
        }
    }
}


@Composable
private fun RotationControl(
    state: MediaEditorState
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ){
        IconButton(
            onClick = {state.rotation = (state.rotation - 90f) % 360},
            modifier =  Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(Color.White)
        ) {
            Icon(Icons.AutoMirrored.Outlined.RotateLeft, "Rotate left")
        }

        Slider(
            value = state.rotation,
            onValueChange = {state.rotation = it},
            valueRange = 0f..360f,
            colors = SliderDefaults.colors(Color.White),
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {state.rotation = (state.rotation + 90f) % 360},
            modifier =  Modifier.size(48.dp),
            colors = IconButtonDefaults.iconButtonColors(Color.White)
        ) {
            Icon(Icons.AutoMirrored.Outlined.RotateRight, "Rotate right")
        }
    }
}


@Composable
private fun ScaleControl(state: MediaEditorState){
    Slider(
        value = state.scale,
        onValueChange = {state.scale = it},
        valueRange = 0.1f..3f,
        steps = 20,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FilterSelector(
    state: MediaEditorState
) {
    val filters = listOf(
        ImageFilter.None,
        ImageFilter.Invert,
        ImageFilter.Sepia,
        ImageFilter.Grayscale,
        ImageFilter.Vintage,
        ImageFilter.Cool,
        ImageFilter.RedHighlight,
        ImageFilter.WarmVintage,
        ImageFilter.NeonGlow,
        ImageFilter.CinematicTeal,
        ImageFilter.Pastel,
        ImageFilter.DuotonePurple,
        ImageFilter.Cyberpunk,
        ImageFilter.HighContrastBW
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterItem(
                name = filter.name,  // Access the name property
                isSelected = state.colorFilter == ColorFilter.colorMatrix(filter.matrix),
                onClick = {
                    state.colorFilter = ColorFilter.colorMatrix(filter.matrix)
                },
            )
            /*(name,  filter) ->
            FilterItem(
                name = name,
                isSelected = state.colorFilter == filter,
                onClick = { state.colorFilter = filter },
            )*/
        }
    }
}


@Composable
fun applyImageEdits(
    state: MediaEditorState,
    onResult: (Bitmap) -> Unit
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        scope.launch(Dispatchers.IO) {
            val original = context.contentResolver
                .openInputStream(state.originalUri)
                ?.use { BitmapFactory.decodeStream(it) }?:
            return@launch

            val matrix = android.graphics.Matrix().apply {
                postRotate(state.rotation)
                postScale(state.scale, state.scale)
                postTranslate(state.offset.x, state.offset.y)
            }

            val transformed = Bitmap.createBitmap(
                original, 0, 0,
                original.width, original.height,
                matrix, true
            )

            state.colorFilter?.let{
                    filter ->
                val canvas = android.graphics.Canvas(transformed)
                val paint = android.graphics.Paint().apply {
                    colorFilter = filter.asAndroidColorFilter()
                    isAntiAlias = true
                }
                canvas.drawBitmap(transformed, 0f, 0f, paint)
            }

            withContext(Dispatchers.Main){
                state.finalBitmap = transformed
                onResult(transformed)
            }
        }
    }

}

@Composable
fun FilterItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
    ){
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(
                    width = 2.dp,
                    color = if(isSelected) Color.White else Color.Transparent,
                    shape = CircleShape
                )
                .padding(4.dp)
                .background(Color.Gray, CircleShape)
        )
        Text(
            text = name,
            color = Color.White,
            fontSize =  12.sp,
            modifier = Modifier
                .padding(top=4.dp)
        )
    }
}


@Composable
fun SaveDialog(
    state: MediaEditorState,
    onDismiss: ()-> Unit
){
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {Text("Save Media")},
        text = {Text("Save you edited media?")},
        confirmButton = {
            TextButton(
                onClick = {
                    //when(state.type){
                    //       MediaType.IMAGE -> state.finalBitmap?.let { saveImage(context, it) }
                    //     MediaType.VIDEO -> state.finalVideoUri?.let { saveVideo(context,it) }
                    // }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss()}
            ) { Text("Cancel") }
        }
    )
}