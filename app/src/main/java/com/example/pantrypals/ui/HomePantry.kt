import android.app.DatePickerDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pantrypals.DBHandler
import com.example.pantrypals.PantryModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomePantry(navController: NavController) {
    val DarkGreen = Color(0, 100, 0)
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(0) }
    var expirationDate by remember { mutableStateOf("") }
    var isAddDialogVisible by remember { mutableStateOf(false) }
    var isRemoveDialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dbHandler = DBHandler(context)
    var groceries = dbHandler.readGroceries()
    var targetItem by remember { mutableStateOf(PantryModel(-1,"",-1,"")) }


    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            groceries?.forEachIndexed { index, grocery ->
                Row(Modifier.combinedClickable(onLongClick = {
                    isRemoveDialogVisible = true
                    targetItem = grocery
                }) { }
                ) {
                    Text("${grocery.itemName} Quantity: ${grocery.itemCount} Expires: ${grocery.ItemExpiration}",
                        Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp))
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedButton(
            onClick = { isAddDialogVisible = true },
            enabled = true,
            colors = ButtonDefaults.buttonColors(DarkGreen)
        ) {
            Text(text = "Add Item")
        }
    }

    if (isRemoveDialogVisible) {
        AlertDialog(
            onDismissRequest = { isRemoveDialogVisible = false },
            title = { Text(text = "Remove ${targetItem.itemName}?") },
            confirmButton = {

                Button(
                    onClick = {
                        // remove item from database
                        dbHandler.removeGrocery(targetItem.itemID)

                        isRemoveDialogVisible = false
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                Button(
                    onClick = { isRemoveDialogVisible = false }
                ) {
                    Text("Keep ${targetItem.itemName}")
                }
            },
            text = {
                Box {
                    Text(text = "Are you sure you want to remove ${targetItem.itemName}?")
                }
            }
        )
    }

    if (isAddDialogVisible) {
        AlertDialog(
            onDismissRequest = { isAddDialogVisible = false },
            title = { Text(text = "Add New Item") },
            confirmButton = {
                Button(
                    onClick = {
                        // Add the item details to the database
                        dbHandler.addNewGrocery(itemName,quantity,expirationDate)

                        isAddDialogVisible = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(
                    onClick = { isAddDialogVisible = false }
                ) {
                    Text("Cancel")
                }
            },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    //Pick Quantity
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantity: ",
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { if (quantity > 0) quantity-- },
                            enabled = quantity > 0
                        ) {
                            Text("-")
                        }
                        Text(
                            text = quantity.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Button(
                            onClick = { quantity++ },
                            enabled = true
                        ) {
                            Text("+")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Expiration date selection
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Expiration Date: ",
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                // Open date picker dialog
                                val dateSetListener =
                                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                        val selectedDate = Calendar.getInstance()
                                        selectedDate.set(year, monthOfYear, dayOfMonth)
                                        expirationDate = SimpleDateFormat(
                                            "dd/MM/yyyy",
                                            Locale.getDefault()
                                        ).format(selectedDate.time)
                                    }

                                val datePickerDialog = DatePickerDialog(
                                    context,
                                    dateSetListener,
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                )
                                datePickerDialog.show()
                            }
                        ) {
                            Text(text = expirationDate.ifEmpty { "Select Date" })
                        }
                    }
                }
            }
        )
    }
}










