package com.example.pantrypals.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pantrypals.R
import com.example.pantrypals.Screen

/*
Creates the main screen with a "Touch To Enter Pantry" text field
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadScreen(navController: NavController) {
    Surface(
        onClick = {
            navController.navigate(Screen.HomePantry.route)
        }
    ) {

    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = with (Modifier) {
            fillMaxSize()
            paint(
                painterResource(id = R.drawable.logo),
                contentScale = ContentScale.FillBounds,
                alignment = Alignment.Center,
            )
        }){

        Text(
            text = "Touch to Enter Pantry",
            fontSize = 25.sp,
            modifier = Modifier.padding(23.dp),
            textAlign = TextAlign.Center,
        )
    }
}

/*
List of the different screens our app will have to navigate
 */
enum class PantryScreens{
    Start,
    Pantry,
    AddItems,
    RemoveItems
}