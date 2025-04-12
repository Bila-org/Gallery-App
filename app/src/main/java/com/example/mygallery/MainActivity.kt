package com.example.mygallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygallery.ui.screens.LandingScreen
import com.example.mygallery.ui.theme.MyGalleryTheme


class MainActivity : ComponentActivity() {

    /*    private val appContainer by lazy{
            (application as GalleryApplication).appContainer
        }

        private val viewModel: GalleryViewModel by viewModels{
            appContainer.provideGalleryViewModelFactory()
        }
    */
    // val viewModel: GalleryViewModel =
    //   viewModel(factory = GalleryViewModel.Factory)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGalleryTheme(
                  //darkTheme = true
            ) {
                Surface(
                    tonalElevation = 5.dp
                ) {
                    LandingScreen(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}
