package com.example.mygallery.ui.screens.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mygallery.R




@Composable
fun MediaClassificationCard(
    imageVector: ImageVector,
    contentDescription: String = "Icon",
    iconTint:Color = Color.Unspecified,
    cardBackground: Brush,
    text:String,
    onClick: ()->Unit = {},
    modifier: Modifier = Modifier
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
        //     .padding(dimensionResource(R.dimen.padding_medium))
        //   .clickable(onClick = onClick)
    ) {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
            ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                //containerColor = Color.Transparent,
                ),
            modifier = Modifier
                .size(width = 40.dp, height = 30.dp)
                //   .size(width = 60.dp, height = 50.dp)
                .clickable(onClick = onClick)
        ) {
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardBackground) // Apply gradient background
                    .padding(vertical = dimensionResource(R.dimen.padding_very_small)),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = imageVector,
                    contentDescription = contentDescription,
                    Modifier.size(35.dp),
                    tint = iconTint
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            //fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}



@Preview(
    showSystemUi = true,
    showBackground = true
)
/*
@Composable
private fun MediaClassificationCardPreview(){
    MediaClassificationCard(
        imageVector = Icons.Default.VideoLibrary,

        text = "Videos"
    )
}
*/

@Preview(
    showSystemUi = true,
    showBackground = true,
   // device = "spec:width=428dp,height=856dp,dpi=269"
)
@Composable
private fun MediaClassificationAllCardsPreview(){
    // Use LazyRow to display the cards horizontally
    Card(
        modifier = Modifier.
        padding(top = 100.dp),
        //    .padding(dimensionResource( R.dimen.padding_very_small)),
        //shape = shapes.extraLarge,
        shape = CircleShape,
        // elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Black
        ),
        //border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline) // Green border

    ) {
        LazyRow(
            modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
            //.padding(top = 100.dp, start = 16.dp, end = 16.dp),
            //  .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.SpaceEvenly, // Add spacing between cards

       // Add padding to the row
        ) {
            itemsIndexed(mediaClassifications) { index, item ->
                MediaClassificationCard(
                    imageVector = item.imageVector,
                    contentDescription = item.contentDescription,
                    iconTint = item.iconTint,
                    cardBackground = item.cardBackground,
                    text = item.text,
                    onClick = {
                        // Handle click for each card
                        println("${item.contentDescription} clicked!")
                    }
                )
            }
        }
    }
}

