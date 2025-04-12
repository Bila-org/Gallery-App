package com.example.mygallery.ui.screens.MediaViewerPagerScreen


//import androidx.compose.foundation.layout.FlowRowScopeInstance.align

import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage


@Suppress("DEPRECATION")
@Composable
fun FullScreenZoomableImage(
    imageUri: Uri,
    modifier: Modifier = Modifier,
    enablePagerSwiping: (Boolean)-> Unit = {}
) {
    var scale by remember { mutableStateOf(1f) } // Scale for zoom
    var offset by remember { mutableStateOf(Offset.Zero) }  // Offset for panning
    var rotation by remember { mutableStateOf(0f) } // Rotation
    val doubleTapScale = remember { mutableStateOf(1f) }

    //val snackbarHostState = remember { SnackbarHostState() }

    val isZoomed = scale > 1f || rotation != 0f || offset != Offset.Zero

    LaunchedEffect(isZoomed) {
          enablePagerSwiping(!isZoomed)
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ) // Smooth animation over 300ms
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            //.align(Alignment.Center),
            //.aspectRatio()
            //.clipToBounds()
            .background(Color.Black)
    ) {
        val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
                scale = (scale * zoomChange).coerceIn(1f, 5f)

                val extraWidth = (scale - 1) * constraints.maxWidth
                val extraHeight = (scale - 1) * constraints.maxHeight

                val maxX = extraWidth / 2
                val maxY = extraHeight / 2

                offset = Offset(
                    x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY)
                )
                rotation += rotationChange
        }
            Box(
                modifier = Modifier
                    // .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            rotationZ = rotation
                        }
                        .transformable(state)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale > 1f
                                        || rotation > 0
                                        || offset.x > 0
                                        || offset.y > 0
                                    ) {
                                        doubleTapScale.value = 1f
                                        scale = 1f
                                        offset = Offset.Zero
                                        rotation = 0f
                                    } else {
                                        doubleTapScale.value = 2f
                                        scale = 2f
                                    }
                                }
                            )
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
        // Snackbar host
        /*SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = dimensionResource(R.dimen.snackbar_padding))
        )*/
}


/*
@Composable
fun ZoomableImage(
    imageUri: Uri,
    onBackClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    isFavorite: Boolean,
    onShareClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1f) } // Scale for zoom
    var offset by remember { mutableStateOf(Offset.Zero) }  // Offset for panning
    var rotation by remember { mutableStateOf(0f) } // Rotation


    val doubleTapScale = remember { mutableStateOf(1f) }
    //val snackbarHostState = remember { SnackbarHostState() }
    var isUiVisible by remember { mutableStateOf(true) }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ) // Smooth animation over 300ms
    )

    /*
    val state = rememberTransformableState { zoomChange, _, _ ->
        scale =(scale * zoomChange).coerceIn(1f,5f)
    }*/

    /*    LaunchedEffect(isUiVisible) {
            val window = (context as? android.app.Activity)?.window
            window?.let {
                // Set system UI bars to black
                it.statusBarColor = android.graphics.Color.BLACK
                it.navigationBarColor = android.graphics.Color.BLACK

                // Hide or show system UI bars
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                    ){
                    val controller = view.windowInsetsController
                    if(controller != null){
                        if(isUiVisible){
                         //   controller.hide(WindowInsets.Type.systemBars())
                            controller.setSystemBarsAppearance(
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                            )
                            controller.setSystemBarsAppearance(
                                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                            )
                        }else
                        {

                        }
                    }
                }else {
                    @Suppress("DEPRECATION")
                    if(isUiVisible) {
                        view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    }else{
                        view.systemUiVisibility = (
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                )
                    }

                }

            }
        }
    */

    /*  Box(
          modifier = modifier
              .fillMaxSize()
              .background(Color.Black),
      ) {
  */
    BoxWithConstraints(
        modifier = modifier
            .clipToBounds()
            .background(Color.Black),
    ) {


        Box(modifier = modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = animatedScale,
                scaleY = animatedScale,
                translationX = offset.x,
                translationY = offset.y,
                rotationZ = rotation
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, panChange, zoom, rotationChange ->
                    // Update scale
                    scale = (scale * zoom).coerceIn(1f, 5f)

                    // Calculate maximum allowed offset based on container and image size
                    val containerWidth = constraints.maxWidth.toFloat()
                    val containerHeight = constraints.maxHeight.toFloat()

                    // Calculate the scaled image size
                    val scaledImageWidth = containerWidth * scale
                    val scaledImageHeight = containerHeight * scale

                    val maxOffsetX = if (scaledImageWidth > containerWidth) {
                        (scaledImageWidth - containerWidth) / 2
                    } else {
                        0f
                    }

                    val maxOffsetY = if (scaledImageHeight > containerHeight) {
                        (scaledImageHeight - containerHeight) / 2
                    } else {
                        0f
                    }


                    offset = Offset(
                        x = (offset.x + panChange.x).coerceIn(-maxOffsetX, maxOffsetX),
                        y = (offset.y + panChange.y).coerceIn(-maxOffsetY, maxOffsetY)
                    )
                    /*
                    offset = Offset(
                        x = (offset.x + panChange.x).coerceIn(-10f, 500f),
                        y = (offset.y + panChange.y).coerceIn(-10f, 500f)
                    )*/

                    rotation += rotationChange
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isUiVisible = !isUiVisible
                    },
                    onDoubleTap = {
                        if (scale > 1f
                            || rotation > 0
                            || offset.x > 0
                            || offset.y > 0
                        ) {
                            doubleTapScale.value = 1f
                            scale = 1f
                            offset = Offset.Zero
                            rotation = 0f
                        } else {
                            doubleTapScale.value = 2f
                            scale = 2f
                        }
                    }
                )
            }
            //   .transformable(state)
            .clipToBounds()
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                contentScale = ContentScale.Fit
            )
        }


        if (isUiVisible) {
            MediaTopBar(
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick,
                isFavorite = isFavorite,
                //snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )

            MediaBottomBar(
                onShareClick = onShareClick,
                onDeleteClick = onDeleteClick,
                onEditClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }

        // Snackbar host
        /*SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = dimensionResource(R.dimen.snackbar_padding))
        )*/
    }
}
*/