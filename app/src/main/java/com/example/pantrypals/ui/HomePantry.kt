import android.app.DatePickerDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
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

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dbHandler = DBHandler(context)
    var groceries = dbHandler.readGroceries()

    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    var isEditQuantityDialogVisible by remember {mutableStateOf(false)}
    var isEditExpirationDialogVisible by remember { mutableStateOf(false) }
    var targetItem by remember { mutableStateOf(PantryModel(-1,"",-1,"")) }

    var pressOffset by remember{ mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    val interactionSource = remember{ MutableInteractionSource() }
    val density = LocalDensity.current


    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.padding(16.dp)
    ){
        var text by remember {mutableStateOf("")}
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "Search")}
        )
    }
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 70.dp)
    ) {
        // list out the pantry in a column style.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // loop over all items in the pantry
            groceries?.forEachIndexed { index, grocery ->
                var expandedContextMenu by remember { mutableStateOf(false) }


                Row(Modifier
                    .indication(interactionSource, LocalIndication.current)
                    .pointerInput(true) {
                        detectTapGestures(
                            onLongPress = {
                                expandedContextMenu = true;
                                targetItem = grocery
                                pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                            })
                    }
                    .onSizeChanged {
                        itemHeight = with(density) { it.height.toDp() }
                    })
                {

                    // Content of grocery item
                    Text("${grocery.itemName} Quantity: ${grocery.itemCount} Expires: ${grocery.itemExpiration}",
                        Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp))

                    // context menu
                    DropdownMenu(expanded = expandedContextMenu,
                        offset= pressOffset.copy(y=pressOffset.y - itemHeight),
                        onDismissRequest = { expandedContextMenu = false }) {
                        DropdownMenuItem(text = { Text(text = "Edit Quantity") }, onClick = {
                            isEditQuantityDialogVisible =true
                            expandedContextMenu =false;
                        })
                        DropdownMenuItem(text = { Text(text = "Edit Expiration Date") }, onClick = {
                            isEditExpirationDialogVisible=true
                            expandedContextMenu =false
                        })
                        DropdownMenuItem(text = { Text(text = "Remove") }, onClick = {
                            isDeleteDialogOpen =true
                            expandedContextMenu =false;
                        })

                    }

                }
            }
        }
    }


    // Add Item Button
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

    var newExpirationDate by remember { mutableStateOf("") }
    if(isEditExpirationDialogVisible){
        LaunchedEffect(isEditExpirationDialogVisible) {
            newExpirationDate = ""
        }
        AlertDialog(
            onDismissRequest = { isEditExpirationDialogVisible = false },
            title = { Text(text = "Edit Expiration Date of ${targetItem.itemName}") },
            confirmButton = {

                Button(
                    onClick = {
                        // remove item from database
                        dbHandler.EditExpirationDate(targetItem.itemID, newExpirationDate)

                        isEditExpirationDialogVisible = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isEditExpirationDialogVisible = false }
                ) {
                    Text("Cancel")
                }
            },
            text = {
                Box {

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
                                        newExpirationDate = SimpleDateFormat(
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
                            Text(text = newExpirationDate.ifEmpty { "Select Date" })
                        }
                    }

                }
            }
        )
    }

    
    var newQuantity by remember { mutableStateOf(0) }
    if(isEditQuantityDialogVisible){
        LaunchedEffect(isEditQuantityDialogVisible) {
            newQuantity = 0
        }
        AlertDialog(
            onDismissRequest = { isEditQuantityDialogVisible = false },
            title = { Text(text = "Edit Quantity of ${targetItem.itemName}") },
            confirmButton = {

                Button(
                    onClick = {
                        // remove item from database
                        if (newQuantity <= 0) dbHandler.removeGrocery(targetItem.itemID)
                        else dbHandler.EditQuantity(targetItem.itemID, newQuantity)

                        isEditQuantityDialogVisible = false
                    }
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isEditQuantityDialogVisible = false }
                ) {
                    Text("Cancel")
                }
            },
            text = {
                Box {
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
                            onClick = { if (newQuantity > 0) newQuantity-- },
                            enabled = newQuantity > 0
                        ) {
                            Text("-")
                        }
                        Text(
                            text = newQuantity.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Button(
                            onClick = { newQuantity++ },
                            enabled = true
                        ) {
                            Text("+")
                        }
                    }
                }
            }
        )
    }

    // Delete Item (manual) Alert
    if (isDeleteDialogOpen) {

        AlertDialog(
            onDismissRequest = { isDeleteDialogOpen = false },
            title = { Text(text = "Remove ${targetItem.itemName}?") },
            confirmButton = {

                Button(
                    onClick = {
                        // remove item from database
                        dbHandler.removeGrocery(targetItem.itemID)

                        isDeleteDialogOpen = false
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isDeleteDialogOpen = false }
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

    // Add Item Dialog
    if (isAddDialogVisible) {
        LaunchedEffect(isAddDialogVisible) {
            if (isAddDialogVisible) {
                // Reset fields when dialog becomes visible
                itemName = ""
                quantity = 0
                expirationDate = ""
            }
        }
        AlertDialog(
            onDismissRequest = { isAddDialogVisible = false
                // Reset fields when dialog is dismissed
                itemName = ""
                quantity = 0
                expirationDate = ""
            },
            title = { Text(text = "Add New Item") },
            confirmButton = {
                Button(
                    onClick = {
                        // Add the item details to the database
                        dbHandler.addNewGrocery(itemName ,quantity, expirationDate)
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










