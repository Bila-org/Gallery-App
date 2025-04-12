package com.example.mygallery.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mygallery.model.GalleryViewModel
import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygallery.R
import com.example.mygallery.ui.GalleryApp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LandingScreen(
    modifier: Modifier = Modifier
) {

    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    val viewModel: GalleryViewModel =
        viewModel(factory = GalleryViewModel.Factory)

    LaunchedEffect(permissionState) {
        if(!permissionState.status.isGranted){
            permissionState.launchPermissionRequest()
        }else{
            viewModel.loadAllMedia()
        }
    }

    if(permissionState.status.isGranted){
        GalleryApp(
            viewModel = viewModel,
            modifier = modifier
        )
    } else{
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = stringResource(R.string.permission_denied),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

