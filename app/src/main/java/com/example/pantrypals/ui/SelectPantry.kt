import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.TopAppBarDefaults
import com.example.pantrypals.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPantry(navController: NavController) {
    val pantries = remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text("Pantries", fontWeight = FontWeight.Bold)
                },
                actions = {
                    AddPantryButton(navController) { pantryName ->
                        pantries.value = pantries.value + pantryName
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            if (pantries.value.isEmpty()) { // Display message only if there are no pantries
                Text(
                    text = "Your pantries will appear here",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    //Display pantries
                    items(pantries.value) { pantry ->
                        Text(
                            text = pantry,
                            modifier = Modifier.padding(16.dp).clickable {
                                //Navigate to HomePantry Screen
                                navController.navigate(Screen.HomePantry.route)
                            },
                        fontSize = 16.sp,
                        color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddPantryButton(navController: NavController, onPantryAdded: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.padding(end = 16.dp)
    ) {
        //Button to add new pantry
        Text(
            text = "+",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.Blue,
            modifier = Modifier.clickable { showDialog = true }
        )

        if (showDialog) {
            AddPantryDialog(
                onDismiss = { showDialog = false },
                onPantryCreated = { pantryName ->
                    onPantryAdded(pantryName)
                    showDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPantryDialog(
    onDismiss: () -> Unit,
    onPantryCreated: (pantryName: String) -> Unit
) {
    var pantryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Pantry", fontWeight = FontWeight.Bold) },
        confirmButton = {
            Button(
                onClick = {
                    // Call the callback function to create the pantry
                    onPantryCreated(pantryName)
                }
            ) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Text field for entering pantry name
                TextField(
                    value = pantryName,
                    onValueChange = { pantryName = it },
                    label = { Text("Pantry Name", fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    )
}
