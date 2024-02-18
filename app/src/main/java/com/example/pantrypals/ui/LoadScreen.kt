package com.example.pantrypals.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pantrypals.R
import com.example.pantrypals.ui.theme.PantryPalsTheme

@Composable
fun PantryPalsApp() {
    Box(
        modifier = with (Modifier) {
            fillMaxSize()
                .paint(
                    painterResource(id = R.drawable.logo),
                    contentScale = ContentScale.FillBounds
                )
        }){
        Column(
            modifier = Modifier.
                padding(30.dp)
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
        ){
            Row(
                modifier = Modifier.

                    weight(1f, false)
            ) {
                Text(
                    text = "Press to Enter Pantry",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PantryPalsTheme {
        PantryPalsApp()
    }
}