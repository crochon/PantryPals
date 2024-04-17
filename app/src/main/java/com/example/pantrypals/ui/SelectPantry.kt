import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pantrypals.Screen
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPantry(navController: NavController, dbHandler: DBHandler) {
    val pantries = remember { mutableStateListOf<String>() }

    // Fetch pantries from the database
    val pantryNames = dbHandler.readPantries()
    if (pantryNames != null) {
        pantries.clear()
        pantries.addAll(pantryNames)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pantries", fontWeight = FontWeight.Bold) },
                actions = {
                    AddPantryButton(navController, dbHandler) { pantryName ->
                        pantries.add(pantryName)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (pantries.isEmpty()) {
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
                    items(pantries) { pantryName ->
                        PantryEntry(
                            pantryName = pantryName, //Current pantry name
                            pantryId = dbHandler.getPantryID(pantryName), // ID of the pantry retrieved from the database

                            // Action to navigate to the homePantry screen when clicked
                            onItemClick = { pantryName -> navController.navigate(Screen.HomePantry.route + "/$pantryName") },

                            // Action to edit the pantry name
                            onEditPantryName = { pantryName, newName ->
                                // Call to edit the pantry name in the database
                                dbHandler.editPantryName(pantryName, newName)

                                // Update the pantry name in the pantries list to update UI
                                val index = pantries.indexOf(pantryName)
                                if (index != -1) {
                                    pantries[index] = newName
                                }
                    },
                            // Action to remove the pantry
                            onRemovePantry = { pantryName, pantryId ->
                                // Call to remove the pantry from the database
                                dbHandler.removePantry(pantryName, pantryId)
                                // Remove the pantry from the list
                                pantries.remove(pantryName)
                    })
                }
            }
        }
    } }
}

@Composable
fun PantryEntry(
    pantryName: String,
    pantryId: Long?,
    onItemClick: (String) -> Unit,
    onEditPantryName: (String, String) -> Unit,
    onRemovePantry: (String, Long) -> Unit
) {

    // State to control visibility of options dialog.
    var showOptions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(pantryName) }
    ) {
        // Row for pantry name and options icon
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display pantry name.
            Text(
                text = pantryName,
                modifier = Modifier.weight(1f)
            )
            // Options icon button
            IconButton(onClick = { showOptions = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
        }
    }
    if (showOptions) {
        if (pantryId != null) {
            PantryOptionsDialog(
                pantryName = pantryName,
                pantryId = pantryId,
                onEditPantryName = onEditPantryName,
                onRemovePantry = onRemovePantry,
                onClose = { showOptions = false }
            )
        }
    }
}

@Composable
fun PantryOptionsDialog(
    pantryName: String,
    pantryId: Long,
    onEditPantryName: (String, String) -> Unit,
    onRemovePantry: (String, Long) -> Unit,
    onClose: () -> Unit
) {
    var showEditNameDialog by remember { mutableStateOf(false) } // State to control visibility of edit name dialog.
    val newName by remember { mutableStateOf(pantryName) } // State to hold edited name.

    // Dialog to display options.
    Dialog(
        onDismissRequest = onClose
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title for options dialog.
                Text(
                    text = "Pantry Options",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                // Button to edit pantry name.
                Button(
                    onClick = {
                        showEditNameDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("Edit Pantry Name")
                }
                // Button to remove pantry.
                Button(
                    onClick = {
                        onRemovePantry(pantryName, pantryId)
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove Pantry")
                }
            }
        }
    }

    // Display edit name dialog if showEditNameDialog is true.
    if (showEditNameDialog) {
        EditPantryNameDialog(
            pantryName = pantryName,
            newName = newName,
            onEditPantryName = { newName ->
                onEditPantryName(pantryName, newName)
                showEditNameDialog = false
                onClose()
            },
            onClose = {
                showEditNameDialog = false
                onClose()
            }
        )
    }
}

@Composable
fun EditPantryNameDialog(
    pantryName: String,
    newName: String,
    onEditPantryName: (String) -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose
    ) {
        var editedName by remember { mutableStateOf(newName) } // State to hold new pantry name.

        // Dialog to edit pantry name
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Text field to enter new pantry name
                TextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("New Pantry Name") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                // Row with save and cancel buttons.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onEditPantryName(editedName)
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Save")
                    }
                    Button(
                        onClick = onClose
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
@Composable
fun AddPantryButton(navController: NavController, dbHandler: DBHandler, onPantryAdded: (String) -> Unit) {
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
                    // Call the function to add the new pantry to the database
                    dbHandler.addPantry(pantryName)
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
                    // Call the callback function to create the pantry with the entered name
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
