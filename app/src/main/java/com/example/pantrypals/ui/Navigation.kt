package com.example.pantrypals.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pantrypals.Screen

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.LoadScreen.route){
        composable(route = Screen.LoadScreen.route) {
            LoadScreen(navController = navController)
        }
        composable(route = Screen.HomePantry.route){
            HomePantry()
        }
    }
}