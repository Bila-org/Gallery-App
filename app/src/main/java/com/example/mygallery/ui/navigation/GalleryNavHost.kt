package com.example.mygallery.ui.navigation

//import com.example.mygallery.ui.navigation.VideoPlayer.videoUriArg
//import ImageEditorScreen
import MediaEditorScreen
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mygallery.model.GalleryUiState
import com.example.mygallery.model.GalleryViewModel

import com.example.mygallery.ui.screens.MediaViewerPagerScreen.MediaViewerPager
import com.example.mygallery.ui.screens.AnyFolderScreen
import com.example.mygallery.ui.screens.FavoritesScreen
import com.example.mygallery.ui.screens.HomeScreen.HomeScreen
import com.example.mygallery.ui.screens.ImagesScreen
import com.example.mygallery.ui.screens.TrashScreen
import com.example.mygallery.ui.screens.VideoEditorScreen
import com.example.mygallery.ui.screens.VideosScreen
import java.net.URLDecoder


@Composable
fun GalleryNavHost(
    navController: NavHostController,
    viewModel: GalleryViewModel,
    uiState: GalleryUiState,
    currentScreen: GalleryDestination,
    modifier: Modifier = Modifier
) {

    var selectedFolder by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ){
        composable(route = Home.route ){
            HomeScreen(
                viewModel = viewModel,
                uiState = uiState,
                onMediaCardSelection = { route ->
                    navController.navigateSingleTopTo(route)
                },
                onItemSelection = {uri, isVideo ->
                    val allMedia = uiState.filteredMediaItems
                    val position = allMedia.indexOfFirst {it.toUri() == uri }
                    if(position >= 0) {
                        navController.navigateSingleTopTo("${MediaViewer.route}/$position")
                    }

                    /*
                    val encodedUri = URLEncoder.encode(uri.toString(), "UTF-8")
                    if(isVideo){
                        Log.d("HomeScreen", "Navigating to video player with URI: $uri") // Log the URI
                        navController.navigateSingleTopTo("${VideoPlayer.route}/${encodedUri}")
                    }else {
                       navController.navigateSingleTopTo("${ImageViewer.route}/${encodedUri}")
                    }
                     */
                },
                onFolderClick = {folderName ->
                    selectedFolder = folderName
                    navController.navigateSingleTopTo(AnyFolder.route)
                },
                modifier = modifier
            )
        }

        composable(route = Videos.route ){
            VideosScreen(
                uiState = uiState,
                viewModel = viewModel,
                onItemSelection = { uri, isVideo ->
                    val allMedia = uiState.filteredMediaItems
                    val position = allMedia.indexOfFirst {it.toUri() == uri }
                    if(position >= 0) {
                        navController.navigateSingleTopTo("${MediaViewer.route}/$position")
                    }
                },
                currentScreen = currentScreen,
                onBackClick = {navController.navigateUp()},
                modifier = modifier
            )
        }


        composable(route = Images.route ){
            ImagesScreen(
                uiState = uiState,
                viewModel = viewModel,
                onItemSelection = {uri, isVideo ->
                    val allMedia = uiState.filteredMediaItems
                    val position = allMedia.indexOfFirst {it.toUri() == uri }
                    if(position >= 0) {
                        navController.navigateSingleTopTo("${MediaViewer.route}/$position")
                    }
                },
                currentScreen = currentScreen,
                onBackClick = {navController.navigateUp()},
                modifier = modifier
            )
        }

        composable(route = Favorites.route ){
            FavoritesScreen(
                uiState = uiState,
                viewModel = viewModel,
                onItemSelection = {uri, isVideo ->
                    val allMedia = uiState.filteredMediaItems
                    val position = allMedia.indexOfFirst {it.toUri() == uri }
                    if(position >= 0) {
                        navController.navigateSingleTopTo("${MediaViewer.route}/$position")
                    }
                },
                currentScreen = currentScreen,
                onBackClick = {navController.navigateUp()},
                modifier = modifier
            )
        }

        composable(route = Trash.route ){
            TrashScreen(
                uiState = uiState,
                viewModel = viewModel,
                onItemSelection = {uri, isVideo ->
                    val allMedia = uiState.filteredMediaItems
                    val position = allMedia.indexOfFirst {it.toUri() == uri }
                    if(position >= 0) {
                        navController.navigateSingleTopTo("${MediaViewer.route}/$position")
                    }
                },
                currentScreen = currentScreen,
                onBackClick = {navController.navigateUp()},
                modifier = modifier
            )
        }

        composable(route = AnyFolder.route ){
            AnyFolderScreen(
                uiState = uiState,
                viewModel = viewModel,
                onItemSelection = {uri, isVideo ->
                    val allMedia = uiState.filteredMediaItems
                    val position = allMedia.indexOfFirst {it.toUri() == uri }
                    if(position >= 0) {
                        navController.navigateSingleTopTo("${MediaViewer.route}/$position")
                    }
                },
                selectedFolder = selectedFolder,
                onBackClick = {navController.navigateUp()},
                modifier = modifier
            )
        }


        composable(
            route = MediaViewer.routeWirthArgs,
            arguments = MediaViewer.arguments
        ){
                navBackStackEntry ->
            val position = navBackStackEntry.arguments?.getInt(MediaViewer.positionArg) ?: 0

            MediaViewerPager(
                navController = navController,
                initialPosition = position,
                onDismiss = {navController.navigateUp()},
                viewModel = viewModel,
                uiState = uiState,
                context = LocalContext.current
            )
        }


        composable(
            route = MediaEditor.routeWithArgs,
            arguments = MediaEditor.arguments
        ){
                navBackStackEntry ->

            val isVideoMedia = navBackStackEntry.arguments?.getBoolean(MediaEditor.isVideoMediaArg)?:false
            val encodedUri = navBackStackEntry.arguments?.getString(MediaEditor.mediaUriArg)?: ""
            val uri = Uri.parse(URLDecoder.decode(encodedUri, "UTF-8"))

            MediaEditorScreen(
                mediaUri = uri,
                isVideoMedia = isVideoMedia,
                onSave = {
                    //editedUri ->
                    // Handle saving the edited image
                    //viewModel.updateMediaItem(uri, editedUri)
                    navController.navigateUp()
                },
                onCancel = {
                    navController.navigateUp()
                }
            )
        }


        /*
        composable(
            route = ImageEditor.routeWithArgs,
            arguments = ImageEditor.arguments
        ){
                navBackStackEntry ->
            val encodedUri = navBackStackEntry.arguments?.getString(ImageEditor.imageUriArg)?: ""
            val uri = Uri.parse(URLDecoder.decode(encodedUri, "UTF-8"))

            ImageEditorScreen(
                imageUri = uri,
                onSave = {
                    //editedUri ->
                    // Handle saving the edited image
                    //viewModel.updateMediaItem(uri, editedUri)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
*/
        /*
        composable(
            route = ImageEditor.routeWithArgs,
            arguments = ImageEditor.arguments
        ){
            navBackStackEntry ->
            val encodedUri = navBackStackEntry.arguments?.getString(ImageEditor.imageUriArg)?: ""
            val uri = Uri.parse(URLDecoder.decode(encodedUri, "UTF-8"))

            ImageEditorScreen(
                imageUri = uri,
                onSave = {
                    //editedUri ->
                    // Handle saving the edited image
                    //viewModel.updateMediaItem(uri, editedUri)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = VideoEditor.routeWithArgs,
            arguments = VideoEditor.arguments
        ){
            navBackStackEntry ->
            val encodedUri = navBackStackEntry.arguments?.getString(VideoEditor.videoUriArg) ?: ""
            val uri = Uri.parse(URLDecoder.decode(encodedUri, "UTF-8"))

            VideoEditorScreen(
                videoUri = uri,
                onSave = {
                    //editedUri ->
                    // Handle saving the edited video
                  //  viewModel.updateMediaItem(uri, editedUri)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )

        } */

    }
}


fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        //popUpTo(
          //  this@navigateSingleTopTo.graph.findStartDestination().id
       // ) {
         //   saveState = true
       // }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
