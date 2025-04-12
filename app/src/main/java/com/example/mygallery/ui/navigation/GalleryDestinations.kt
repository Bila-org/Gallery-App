package com.example.mygallery.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument


sealed interface GalleryDestination{
    val route: String
}

val GalleryScreens = listOf(Home, AnyFolder,Videos, Images, Favorites, Trash)


data object Home: GalleryDestination {
    override val route = "Home"
}

data object AnyFolder: GalleryDestination{
    override val route = "AnyFolder"
}

data object Videos : GalleryDestination {
    override val route = "Videos"
}


data object Images : GalleryDestination {
    override val route = "Images"
}

data object Favorites : GalleryDestination {
    override val route = "Favorites"
}

data object Trash : GalleryDestination {
    override val route = "Trash"
}


data object MediaEditor: GalleryDestination{
    override val route = "MediaEditor"
    const val mediaUriArg = "media_uri"
    const val isVideoMediaArg = "position"
    val routeWithArgs = "$route/{$mediaUriArg}/{$isVideoMediaArg}"
    val arguments = listOf(
        navArgument(mediaUriArg){type = NavType.StringType},
        navArgument(isVideoMediaArg){type = NavType.BoolType}
    )
}


data object  ImageEditor: GalleryDestination{
    override val route = "ImageEditor"
    const val imageUriArg = "image_uri"
    val routeWithArgs = "$route/{$imageUriArg}"
    val arguments = listOf(navArgument(imageUriArg){type = NavType.StringType})
}

data object VideoEditor: GalleryDestination{
    override val route = "VideoEditor"
    const val videoUriArg = "video_uri"
    val routeWithArgs = "$route/{$videoUriArg}"
    val arguments = listOf(navArgument(videoUriArg) { type = NavType.StringType})
}


data object MediaViewer: GalleryDestination{
    override val route = "MediaViewer"
    const val positionArg = "position"
    val routeWirthArgs = "$route/{$positionArg}"
    val arguments = listOf(
        navArgument(positionArg){type = NavType.IntType}
    )
}

/*
data object MediaViewer: GalleryDestination{
    override val route = "MediaViewer"
    const val mediaListArg = "media_list"
    const val positionArg = "position"
    val routeWirthArgs = "$route/{$mediaListArg}/{$positionArg}"
    val arguments = listOf(
        navArgument(mediaListArg){type = NavType.StringType},
        navArgument(positionArg){type = NavType.IntType}
    )
}*/