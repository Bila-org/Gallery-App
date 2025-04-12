package com.example.mygallery.ui


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Gallery
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.common.MediaBottomBar
import com.example.mygallery.ui.navigation.GalleryDestination
import com.example.mygallery.ui.navigation.GalleryNavHost
import com.example.mygallery.ui.navigation.GalleryScreens
import com.example.mygallery.ui.navigation.Home
import com.example.mygallery.ui.navigation.Videos


@Composable
fun GalleryApp(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen =
        GalleryScreens.find { it.route == currentDestination?.route } ?: Home

    val uiState by viewModel.uiState.collectAsState()

    GalleryNavHost(
        navController = navController,
        viewModel = viewModel,
        uiState = uiState,
        currentScreen = currentScreen,
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryAppTopBar(
    currentScreen: GalleryDestination,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(text = currentScreen.route)
        },
        navigationIcon = {
            if (currentScreen.route != Home.route) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { showMenu = true }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.width(200.dp)
            ) {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                    },
                    text = { Text("Option 1") },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                    },
                    text = { Text("Option 2") },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                    },
                    text = { Text("Option 3") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            //   scrolledContainerColor: Color = Color.Unspecified,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    )
}


fun shareMedia(context: Context, mediaUri: Uri, mimeType: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, mediaUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share Media"))
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Preview(showSystemUi = true)
@Composable
fun GallAppTopBarPreview() {
    GalleryAppTopBar(
        currentScreen = Videos,
        onBackClick = {},
        modifier = Modifier
    )
}