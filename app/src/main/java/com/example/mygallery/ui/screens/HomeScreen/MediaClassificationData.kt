package com.example.mygallery.ui.screens.HomeScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mygallery.data.MediaFilter

// Data class to hold information for each card
data class MediaClassificationData(
    val imageVector: ImageVector,
    val contentDescription: String,
    val iconTint: Color,
    val cardBackground: Brush,
    val text: String,
    val filter: MediaFilter = MediaFilter.ALL,
)


val mediaClassifications = listOf(
    MediaClassificationData(
        imageVector = Icons.Default.VideoLibrary,
        contentDescription = "Videos",
        iconTint = Color(0xFF42A5F5), // Very light blue icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)) // Very light blue gradient
        ),
        text = "Videos",
        filter = MediaFilter.VIDEOS
    ),
    MediaClassificationData(
        imageVector = Icons.Default.Image,
        contentDescription = "Images",
        iconTint = Color(0xFF66BB6A), // Very light green icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)) // Very light green gradient
        ),
        text = "Images",
        filter = MediaFilter.IMAGES
    ),
    MediaClassificationData(
        imageVector = Icons.Default.Star,
        contentDescription = "Favorite",
        iconTint = Color(0xFFFF7043), // Very light orange icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2)) // Very light orange gradient
        ),
        text = "Favorites",
        filter = MediaFilter.ALL
    ),
    MediaClassificationData(
        imageVector = Icons.Default.Delete,
        contentDescription = "Trash",
        iconTint = Color(0xFFEF5350), // Very light red icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFEBEE), Color(0xFFFFCDD2)) // Very light red gradient
        ),
        text = "Trash",
        filter = MediaFilter.ALL
    )
)

/*

// Create a list of data for the cards
val mediaClassifications = listOf(
    MediaClassificationData(
        imageVector = Icons.Default.VideoLibrary,
        contentDescription = "Videos",
        iconTint = Color(0xFF0D47A1), // Dark blue icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFF64B5F6), Color(0xFF90CAF9)) // Light blue gradient
        ),
        text = "Videos",
    ),
    MediaClassificationData(
        imageVector = Icons.Default.Image,
        contentDescription = "Images",
        iconTint = Color(0xFF1B5E20), // Dark green icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFFA5D6A7)) // Light green gradient
        ),
        text = "Images",
    ),
    MediaClassificationData(
        imageVector = Icons.Default.Star,
        contentDescription = "Favorite",
        iconTint = Color(0xFFBF360C), // Dark orange icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFAB91), Color(0xFFFFCCBC)) // Light orange gradient
        ),
        text = "Favorite",
    ),
    MediaClassificationData(
        imageVector = Icons.Default.Delete,
        contentDescription = "Trash",
        iconTint = Color(0xFFB71C1C), // Dark red icon
        cardBackground = Brush.verticalGradient(
            colors = listOf(Color(0xFFEF9A9A), Color(0xFFFFCDD2)) // Light red gradient
        ),
        text = "Trash",
    )
)
*/