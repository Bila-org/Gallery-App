@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mygallery.ui.screens.ImageEditorScreen

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.mygallery.ui.navigation.ImageEditor

/*

@Composable
fun ImageEditorScreen(
    imageUri: Uri,
    onSave: (Uri) -> Unit,
    onCancel: ()-> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = rememberImageBitmap(imageUri)

    var selectedFilter by remember { mutableStateOf<ImageFilter>(ImageFilter.None)}

    Box(
        modifier = modifier
            .fillMaxSize()
    ){
        // Main Image Preview
        FilterPreview(
            bitmap = bitmap,
            filter = selectedFilter,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                //.height(200.dp)
        )

        EditorControls(
            selectedFilter = selectedFilter,
            onFilterSelected = {selectedFilter = it},
            imageUri = imageUri,
            onSave = {
                saveFilteredImage(context,bitmap,selectedFilter,onSave)
            },
            onCancel = onCancel,
            modifier= Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                //.padding(bottom = 50.dp)//
        )
    }

}



@Composable
fun rememberImageBitmap(uri: Uri): Bitmap?{
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(uri) {
        val loader = ImageLoader(context)
        val imageBitmap = loader.execute(
            ImageRequest.Builder(context)
                .data(uri)
                .allowHardware(false)
                .build()).drawable?.toBitmap()
        bitmap = imageBitmap
    }

    return bitmap
}


@Composable
private fun FilterPreview(
    bitmap: Bitmap?,
    filter: ImageFilter,
    modifier: Modifier = Modifier
){
    bitmap?.let{bmp ->

        Canvas(modifier = modifier
        ){
            //  val paint = Paint().apply{
            val paint = androidx.compose.ui.graphics.Paint().apply {

                asFrameworkPaint().colorFilter = ColorMatrixColorFilter(filter.matrix)
                //     colorFilter = ColorMatrixColorFilter(filter.matrix)
            }
            drawIntoCanvas {canvas ->
                canvas.drawImage(
                    image = bmp.asImageBitmap(),
                    topLeftOffset = Offset.Zero,
                    paint = paint
                )
            }
        }
    }
}


@Composable
private fun EditorControls(
    selectedFilter: ImageFilter,
    onFilterSelected: (ImageFilter) -> Unit,
    imageUri: Uri,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        // Filter Selection
        FilterSelector(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelected,
            imageUri = imageUri
        )

        // Action Buttons
        Row {
            Button(
                onClick = onCancel
            ){
                Text("Cancel")
            }
            //Spacer(Modifier.width(8.dp))
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onSave
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun FilterSelector(
    selectedFilter: ImageFilter,
    onFilterSelected: (ImageFilter) -> Unit,
    imageUri: Uri
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

    LazyRow {
        items(filters) { filter ->
            FilterOption(
                filter = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                imageUri = imageUri
            )
        }
    }
}

@Composable
private fun FilterOption(
    filter: ImageFilter,
    isSelected: Boolean,
    onClick: () -> Unit,
    imageUri: Uri,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
                .width(64.dp)
                //.size(64.dp)
                .border(
                    width = 2.dp,
                    color = if (isSelected) Color.Blue else Color.Transparent
                )
        ) {
            // You can add small previews here if needed
        }
        Text(
            text = filter.name,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


private fun saveFilteredImage(
    context: Context,
    originalBitmap: Bitmap?,
    filter: ImageFilter,
    onSave: (Uri) -> Unit
) {
    originalBitmap?.let { bmp ->
        val editedBitmap = bmp.applyColorMatrix(filter.matrix)
        saveBitmapToStorage(context, editedBitmap)?.let(onSave)
    }
}

private fun Bitmap.applyColorMatrix(matrix: ColorMatrix): Bitmap {
    val result = copy(config ?:Bitmap.Config.ARGB_8888, true)
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(matrix)
    }


    val canvas = android.graphics.Canvas(result) // ✅ This is android.graphics.Canvas
    canvas.drawBitmap(this@applyColorMatrix, 0f, 0f, paint)


    // Canvas(result).apply {
    //    drawBitmap(this@applyColorMatrix, 0f, 0f, paint)
    // }
    return result
}

private fun saveBitmapToStorage(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "edited_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            }
        }

        uri
    } catch (e: Exception) {
        null
    }
}


 */


