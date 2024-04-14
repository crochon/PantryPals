package com.example.pantrypals.ui


import DBHandler
import HomePantry
import SelectPantry
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pantrypals.Screen

@Composable
fun Navigation(){
    // Create a NavHost with a NavController
    val navController = rememberNavController()

    // Define the navigation routes
    NavHost(navController = navController, startDestination = Screen.LoadScreen.route){

        // Load Screen route
        composable(route = Screen.LoadScreen.route) {
            LoadScreen(navController = navController)
        }

        // Home Pantry route
        composable(route = Screen.HomePantry.route + "/{pantryName}") { backStackEntry ->
            val pantryName = backStackEntry.arguments?.getString("pantryName") // Extract pantryName from route arguments
            val dbHandler = DBHandler(context = LocalContext.current) // Initialize DBHandler with context
            val pantryID = pantryName?.let { dbHandler.getPantryID(it) } // Retrieve pantryID using pantryName from DBHandler
            val context = LocalContext.current

            if (pantryID != null) {
                HomePantry(navController = navController, pantryID = pantryID, context = context)  // Navigate to HomePantry screen and pass pantryID
            }
        }

        // Select Pantry route
        composable(route = Screen.SelectPantry.route){
            val dbHandler = DBHandler(LocalContext.current)
            SelectPantry(navController = navController, dbHandler = dbHandler)
        }
    }
}
