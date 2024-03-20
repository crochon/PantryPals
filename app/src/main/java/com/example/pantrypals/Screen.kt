package com.example.pantrypals

sealed class Screen(val route: String){
    object HomePantry : Screen("Pantry_Home")
    object LoadScreen : Screen("Landing_Page")
}