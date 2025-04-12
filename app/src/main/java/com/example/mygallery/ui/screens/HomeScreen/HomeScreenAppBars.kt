package com.example.mygallery.ui.screens.HomeScreen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.PermMedia
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mygallery.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        isGranted ->
        if(isGranted) launchCamera(context)
        else
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
   ) {
       // result ->
    }


    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            IconButton(
                onClick = {
                    //permissionLauncher.launch(Manifest.permission.CAMERA)
                    launchCamera(context)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Open Camera"
                )
            }
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
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            //   scrolledContainerColor: Color = Color.Unspecified,
            // navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier,
        scrollBehavior = scrollBehavior

    )
}


private fun launchCamera(context: Context) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    //val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun HomeBottomAppBar(
    onFolderSelect: () -> Unit,
    onMediaSelect: () -> Unit,
    isFolderView: Boolean = false,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = NavigationBarDefaults.Elevation,

        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (isFolderView) Icons.Outlined.PermMedia else Icons.Default.PermMedia,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.media),
                    fontWeight = if (!isFolderView) FontWeight.Bold else {
                        FontWeight.Normal
                    }
                )
            },
            selected = !isFolderView,
            onClick = onMediaSelect,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary, // Color when selected
                selectedTextColor = MaterialTheme.colorScheme.primary, // Color when selected
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // Color when unselected
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant, // Color when unselected

                indicatorColor = MaterialTheme.colorScheme.inversePrimary,
                //disabledIconColor =,
                //disabledTextColor =,
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = if (isFolderView) Icons.Default.Folder else Icons.Outlined.Folder,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "Folders",
                    fontWeight = if (isFolderView) FontWeight.Bold else {
                        FontWeight.Normal
                    }
                )
            },
            selected = isFolderView,
            onClick = onFolderSelect,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary, // Color when selected
                selectedTextColor = MaterialTheme.colorScheme.primary, // Color when selected
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // Color when unselected
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant, // Color when unselected

                indicatorColor = MaterialTheme.colorScheme.inversePrimary,
                //disabledIconColor =,
                //disabledTextColor =,
            )
        )
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    //showSystemUi = true
)
@Composable
fun HomeTopAppBarPreview() {
    HomeTopAppBar(
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        modifier = Modifier
    )
}


@Preview(
    showBackground = true,
//    showSystemUi = true
)
@Composable
fun HomeBottomAppBarPreview() {
    HomeBottomAppBar(
        onFolderSelect = { },
        onMediaSelect = { },
        isFolderView = false,
        modifier = Modifier
    )
}
