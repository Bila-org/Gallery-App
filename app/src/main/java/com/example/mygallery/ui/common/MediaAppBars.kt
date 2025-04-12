package com.example.mygallery.ui.common

import android.content.res.Configuration
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mygallery.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaTopBar(
    onBackClick: ()-> Unit,
    onFavoriteClick: ()->Unit,
    isFavorite: Boolean = false,
   // snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    Toast.makeText(
                        context,
                        if (isFavorite) "Removed from favorites" else "Added to favorites",
                        LENGTH_SHORT
                    ).show()
                    onFavoriteClick()
                    /*coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (isFavorite) "Added to favorites" else "Removed from favorites",
                            duration = SnackbarDuration.Short
                        )
                    }*/

                },
                modifier = Modifier.background(Color.Transparent, CircleShape)
            ){
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite",
                )
            }
            // More icon (e.g., for additional options)
            IconButton(
                onClick = { /* Handle more options */ },
                modifier = Modifier.background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.3f),
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White,
            titleContentColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}


@Composable
fun MediaBottomBar(
    onShareClick: ()-> Unit,
    onDeleteClick: ()->Unit,
    onEditClick: ()-> Unit,
    modifier: Modifier = Modifier
){
    // Bottom bar with share, delete, and edit icons
    var showMoveToTrashDialog by remember { mutableStateOf(false) }

    //Column(
      //  modifier = modifier
        //    .fillMaxSize(),
       // verticalArrangement = Arrangement.Bottom
   // ) {
        BottomAppBar(
            modifier = modifier
                .fillMaxWidth(),
            containerColor = Color.Black.copy(alpha = 0.3f), // Set the background color of the bottom bar
            contentColor = Color.White,
            actions = {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                        Text(
                            text = stringResource(R.string.share_button),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Edit action
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                        Text(
                            text = stringResource(R.string.edit_button),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Delete action
                IconButton(
                    onClick = {//onDeleteClick
                        showMoveToTrashDialog = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                        Text(
                            text = stringResource(R.string.delete_button),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        )
    //}
        if (showMoveToTrashDialog) {
            AlertDialog(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoDelete,
                        contentDescription = null
                    )
                },
                onDismissRequest = { showMoveToTrashDialog = false },
                title = { Text("Move the item to the Trash?") },
                text = { Text("Selected item will be moved to trash. It will be permanently deleted after 30 days. Do you want to continue?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showMoveToTrashDialog = false
                            onDeleteClick()
                           // onBackClick()
                        }
                    ) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showMoveToTrashDialog = false }
                    ) { Text("No") }
                }
            )
        }

   // }
}


/*@Composable
fun ZoomableImageTopBar(
    onBackClick: ()-> Unit ,
    onFavoriteClick: ()->Unit,
    isFavorite: Boolean = false,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {

    //val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_very_small)),
          //  .align(Alignment.Top),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                // .size(48.dp)
                .background(Color.Transparent, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Row {
            // Favorite icon
            IconButton(
                onClick =
                {
                    onFavoriteClick
                    Toast.makeText(
                        context,
                        if (isFavorite) "Added to favorites" else "Removed from favorites",
                        LENGTH_SHORT).show()
                   /* coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = if (isFavorite) "Added to favorites" else "Removed from favorites",
                            duration = SnackbarDuration.Short
                        )
                    }*/
                },
                modifier = Modifier
                    //   .size(48.dp)
                    .background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = if(isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite",
                    tint = Color.White
                )
            }

            // More icon (e.g., for additional options)
            IconButton(
                onClick = { /* Handle more options */ },
                modifier = Modifier
                    .background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.White
                )
            }
        }
    }
}
*/

/*@Composable
fun ZoomableImageBottomBar(
    onShareClick: ()-> Unit,
    onDeleteClick: ()->Unit,
    onEditClick: ()-> Unit,
    modifier: Modifier = Modifier
){
    // Bottom bar with share, delete, and edit icons
    Row(
        modifier = modifier
            .fillMaxWidth()
            //   .align(Alignment.BottomCenter)
            .padding(dimensionResource(R.dimen.padding_very_small)),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        // Share icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.White,
                )
            }
            Text(
                text = (stringResource(R.string.share_button)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }


        // Edit icon
        Column(
           // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White
                )
            }
            Text(
                text = (stringResource(R.string.edit_button)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Delete icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .background(Color.Transparent, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
            Text(
                text = (stringResource(R.string.delete_button)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
*/

@Preview(showSystemUi = true,
    showBackground = true)
@Composable
fun AppTopBarPreview(){
    Box (
        modifier = Modifier.background(Color.Black)
    ){
        MediaTopBar(
            onBackClick = {},
            onFavoriteClick = {},
            isFavorite = false,
            //  snackbarHostState = SnackbarHostState()
        )
    }
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
fun ImageBottomBarPreview(){
     Box(
            modifier = Modifier
            .background(Color.Black)
   //         .align(Alignment.BottomCenter)
    ) {
        MediaBottomBar(
            onShareClick = {},
            onDeleteClick = {},
            onEditClick = {}
        )
    }
}


@Preview(showSystemUi = true,
    showBackground = true)
@Composable
fun AppBarsPreview(){
    Column {
        MediaTopBar(
            onBackClick = {},
            onFavoriteClick = {},
            isFavorite = false,
            //  snackbarHostState = SnackbarHostState()
        )
        Spacer(modifier = Modifier.weight(1f))
        MediaBottomBar(
            onShareClick = {},
            onDeleteClick = {},
            onEditClick = {}
        )
    }
}