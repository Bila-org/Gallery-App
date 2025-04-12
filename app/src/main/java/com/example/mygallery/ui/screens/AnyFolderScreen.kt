package com.example.mygallery.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygallery.data.MediaFilter
import com.example.mygallery.data.MediaItem
import com.example.mygallery.model.GalleryUiState
import com.example.mygallery.model.GalleryViewModel
import com.example.mygallery.ui.GalleryAppTopBar
import com.example.mygallery.ui.common.MediaItemGrid
import com.example.mygallery.ui.common.groupMediaByMonth
import com.example.mygallery.ui.navigation.GalleryDestination
import com.example.mygallery.ui.navigation.Home

@Composable
fun AnyFolderScreen(
    viewModel: GalleryViewModel,
    uiState: GalleryUiState,
    onItemSelection: (Uri, Boolean) -> Unit,
    selectedFolder: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val folders: Map<String, List<MediaItem>> = viewModel.loadAllFolders()
    // Show media grid for the selected folder
    val folderMediaItems = folders[selectedFolder] ?: emptyList()


  //  viewModel.applyFilter(MediaFilter.TRASH)

    //val filterMediaList = uiState.filteredMediaItems
    val groupedMedia = groupMediaByMonth(folderMediaItems)

    Scaffold(
        topBar =
        {
            AnyFolderTopBar(
                currentScreen = selectedFolder,
                onBackClick = onBackClick
            )
        }
    )
    { innerPadding ->

        /*FolderMediaItemGrid(
            mediaItems = groupedMedia,
            onItemSelection = onItemSelection,
            onBackClick = onBackClick,
            contentPadding = innerPadding,
            modifier = modifier
              //  .padding(top = 100.dp)
        )*/
        MediaItemGrid(
            groupedMedia = groupedMedia,
            onItemSelection = onItemSelection,
            modifier = modifier,
            contentPadding = innerPadding
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnyFolderTopBar(
    currentScreen: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(text = currentScreen)
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back"
                )
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
        modifier = modifier
    )
}
