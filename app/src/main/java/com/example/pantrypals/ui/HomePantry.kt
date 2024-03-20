package com.example.pantrypals.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController


/*TODO Create popup Warning user if item is already in database */
@Composable
fun HomePantry(){
    val DarkGreen = Color(0, 100, 0)
    Box(
        contentAlignment = Alignment.BottomCenter
    ){
        OutlinedButton(
            onClick = { },
            enabled = true,
            colors = ButtonDefaults.buttonColors(DarkGreen)
            ) {
            Text(text = "Add Item",
                color = Color.White)
        }
    }
}

