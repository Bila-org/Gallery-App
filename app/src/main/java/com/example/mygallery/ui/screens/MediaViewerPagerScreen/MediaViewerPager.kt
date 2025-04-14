package com.example.mygallery.ui.screens.MediaViewerPagerScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mygallery.data.MediaFilter
import com.example.mygallery.model.GalleryUiState
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.common.MediaBottomBar
import com.example.mygallery.ui.common.MediaTopBar
import com.example.mygallery.ui.navigation.ImageEditor
import com.example.mygallery.ui.navigation.MediaEditor
import com.example.mygallery.ui.navigation.VideoEditor
import com.example.mygallery.ui.navigation.navigateSingleTopTo
import com.example.mygallery.ui.shareMedia
import kotlinx.coroutines.launch
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

    val coroutineScope = rememberCoroutineScope()

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
      //  mediaItems.get(currentActivePage)
        mediaItems.get(pagerState.currentPage)
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

                if (currentFilter == MediaFilter.TRASH) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    try{
                                        viewModel.deletePermanently(currentMedia)
                                        pagerState.animateScrollToPage(
                                            page = minOf(pagerState.pageCount,pagerState.currentPage + 1)
                                        )
                                        //onDismiss()
                                        Toast.makeText(context, "Media item is permanently deleted.", Toast.LENGTH_SHORT).show()
                                    }catch (e:Exception){
                                        Toast.makeText(context,
                                            "Delete failed: ${e.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()

                                    }

                                }
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                )
                                Text(
                                    text = "Delete",
                                    color = Color.White
                                )
                            }


                        }
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        viewModel.restoreMedia(currentMedia)
                                        pagerState.animateScrollToPage(
                                            page = minOf(pagerState.pageCount,pagerState.currentPage + 1)
                                        )
                                        //    onDismiss()
                                        Toast.makeText(context, "Media restored successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    catch (e:Exception){
                                        Toast.makeText(
                                            context,
                                            "Restore failed: ${e.message ?: "Unknown error"}", Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = "Restore",
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                )

                                Text(
                                    text = "Restore",
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
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
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = minOf(pagerState.pageCount,pagerState.currentPage + 1)
                                )
                            }
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
}