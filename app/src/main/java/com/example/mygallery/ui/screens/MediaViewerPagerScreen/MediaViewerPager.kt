package com.example.mygallery.ui.screens.MediaViewerPagerScreen

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavHostController
import com.example.mygallery.model.GalleryUiState
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.common.MediaBottomBar
import com.example.mygallery.ui.common.MediaTopBar
import com.example.mygallery.ui.navigation.ImageEditor
import com.example.mygallery.ui.navigation.MediaEditor
import com.example.mygallery.ui.navigation.VideoEditor
import com.example.mygallery.ui.navigation.navigateSingleTopTo
import com.example.mygallery.ui.shareMedia
import java.net.URLEncoder

@Composable
fun MediaViewerPager(
    navController: NavHostController,
    initialPosition: Int,
    onDismiss: ()-> Unit,
    viewModel: GalleryViewModel,
    uiState: GalleryUiState,
    context: Context
) {
    val mediaItems = uiState.filteredMediaItems
    val currentFilter = uiState.currentFilter

    val pagerState = rememberPagerState(
        initialPage = initialPosition,
        pageCount = { mediaItems.size }
    )

    /*
    val filng = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(1)
    )*/

    val scope = rememberCoroutineScope()

    var pagerSwipingEnabled by remember { mutableStateOf(true) }


    // Track active page for video playback control
    var currentActivePage by remember { mutableStateOf(initialPosition) }
    // Track if user is scrolling
    val isScrolling by remember { derivedStateOf { pagerState.isScrollInProgress } }
    // Update active page when scrolling stops
    LaunchedEffect(isScrolling) {
        if (!isScrolling) {
            currentActivePage = pagerState.currentPage
        }
    }


    var isUiVisible by remember { mutableStateOf(true) }

    // Get the current media item based on active page
    val currentMedia by remember { derivedStateOf {
        mediaItems.get(currentActivePage)
    } }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isUiVisible = !isUiVisible
                    }
                )
            }
    ) {
        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 1,
            userScrollEnabled = pagerSwipingEnabled,
            // flingBehavior = fling
            //    count  = mediaItems.size,
            modifier = Modifier.fillMaxSize()

        ) { page ->
            val currentPageMedia = mediaItems[page]
            val isActivePage = !isScrolling && page == currentActivePage

            if (currentPageMedia.isVideo) {
                FullScreenVideoPlayer(
                    uri = currentPageMedia.toUri(),
                    isActive = isActivePage,
                    onPagerSwipeStateChanged = {},
                    modifier = Modifier
                )
            } else {
                FullScreenZoomableImage(
                    imageUri = currentPageMedia.toUri(),
                    modifier = Modifier.fillMaxSize(),
                    enablePagerSwiping = { enabled ->
                        pagerSwipingEnabled = enabled
                    }
                )
            }
        }
            if (isUiVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                )
                {
                    MediaTopBar(
                        onBackClick = onDismiss,
                        onFavoriteClick = {
                            viewModel.toggleFavorite(currentMedia.toUri())
                                          },
                        isFavorite = currentMedia.isFavorite,
                        //   snackbarHostState = snackbarHostState,
                        modifier = Modifier
                            .fillMaxWidth()
                        //     .align(Alignment.TopStart)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    MediaBottomBar(
                        onShareClick = {
                            if (currentMedia.isVideo) {
                                shareMedia(context, currentMedia.toUri(), mimeType = "video/*")
                            } else {
                                shareMedia(context, currentMedia.toUri(), mimeType = "image/*")
                            }
                        },
                        onDeleteClick =
                        {
                            viewModel.trashMedia(currentMedia.toUri())
                            onDismiss()
                        },
                        onEditClick = {
                            val encodedUri = URLEncoder.encode(currentMedia.uri, "UTF-8")

                            navController.navigateSingleTopTo("${MediaEditor.route}/$encodedUri/${currentMedia.isVideo}")

                            /*if (currentMedia.isVideo) {
                                navController.navigateSingleTopTo("${VideoEditor.route}/$encodedUri")
                        } else {
                                navController.navigateSingleTopTo("${ImageEditor.route}/$encodedUri")
                        }
                              */
                                      },
                        modifier = Modifier
                            .fillMaxWidth()
                        //  .align(Alignment.BottomCenter)
                        //   .navigationBarsPadding()
                    )
                }
            }
        }
    }