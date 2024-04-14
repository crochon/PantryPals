import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.pantrypals.PantryModel
import java.lang.Exception

private const val DATABASE_NAME = "PantryDB"
private const val DATABASE_VERSION = 1
private const val TABLE_ITEMS = "items"
private const val ITEM_ID_COL = "item_id"
private const val ITEM_NAME_COL = "item_name"
private const val ITEM_COUNT_COL = "item_count"
private const val ITEM_EXPIRATION_COL = "item_expiration"
private const val PANTRY_ID_COL = "pantry_id"
private const val TABLE_PANTRIES = "pantries"
private const val PANTRY_NAME_COL = "pantry_name"

class DBHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create items table
        val createItemsTable = ("CREATE TABLE $TABLE_ITEMS ($ITEM_ID_COL INTEGER PRIMARY KEY, $ITEM_NAME_COL TEXT, $ITEM_COUNT_COL INTEGER, $ITEM_EXPIRATION_COL TEXT, $PANTRY_ID_COL INTEGER)")
        db.execSQL(createItemsTable)

        // Create pantries table
        val createPantriesTable = ("CREATE TABLE $TABLE_PANTRIES ($PANTRY_ID_COL INTEGER PRIMARY KEY, $PANTRY_NAME_COL TEXT)")
        db.execSQL(createPantriesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PANTRIES")
        onCreate(db)
    }

    fun addNewGrocery(pantryId: Long, itemName: String, quantity: Int, expirationDate: String) {
        val values = ContentValues()
        values.put(ITEM_NAME_COL, itemName)
        values.put(ITEM_COUNT_COL, quantity)
        values.put(ITEM_EXPIRATION_COL, expirationDate)
        values.put(PANTRY_ID_COL, pantryId)
        val db = this.writableDatabase
        db.insert(TABLE_ITEMS, null, values)
        db.close()
    }

    fun readGroceries(pantryId: Long): ArrayList<PantryModel>? {
        val db = this.readableDatabase
        val itemList: ArrayList<PantryModel> = ArrayList()

        try {
            val cursor = db.rawQuery("SELECT * FROM $TABLE_ITEMS WHERE $PANTRY_ID_COL = ?", arrayOf(pantryId.toString()))

            while (cursor.moveToNext()) {
                val itemIdIndex = cursor.getColumnIndex(ITEM_ID_COL)
                val itemNameIndex = cursor.getColumnIndex(ITEM_NAME_COL)
                val itemCountIndex = cursor.getColumnIndex(ITEM_COUNT_COL)
                val itemExpirationIndex = cursor.getColumnIndex(ITEM_EXPIRATION_COL)
                val pantryIdIndex = cursor.getColumnIndex(PANTRY_ID_COL)

                // Create PantryModel object and add it to the list
                itemList.add(
                    PantryModel(
                        cursor.getInt(itemIdIndex),
                        cursor.getString(itemNameIndex),
                        cursor.getInt(itemCountIndex),
                        cursor.getString(itemExpirationIndex),
                        cursor.getLong(pantryIdIndex)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error reading groceries", e)
        } finally {
            db.close() //close database connection
        }

        return itemList
    }

    fun removeGrocery(itemId: Int) {
        val db = this.writableDatabase
        try {
            val deletedRows = db.delete(TABLE_ITEMS, "$ITEM_ID_COL = ?", arrayOf(itemId.toString()))
            if (deletedRows == 0) {
                Log.e("DBHandler", "No rows deleted for itemId: $itemId")
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error removing grocery with itemId: $itemId", e)
        } finally {
            db.close()
        }
    }

    fun EditQuantity(itemId: Int, newQuantity: Int) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put(ITEM_COUNT_COL, newQuantity)
            }
            val rowsAffected = db.update(TABLE_ITEMS, values, "$ITEM_ID_COL = ?", arrayOf(itemId.toString()))
            if (rowsAffected == 0) {
                Log.e("DBHandler", "No rows updated for itemId: $itemId")
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error updating quantity for itemId: $itemId", e)
        } finally {
            db.close()
        }
    }

    fun EditExpirationDate(itemId: Int, newExpirationDate: String) {
        val db = this.writableDatabase
        try {
            val values = ContentValues().apply {
                put(ITEM_EXPIRATION_COL, newExpirationDate)
            }
            val rowsAffected = db.update(TABLE_ITEMS, values, "$ITEM_ID_COL = ?", arrayOf(itemId.toString()))
            if (rowsAffected == 0) {
                Log.e("DBHandler", "No rows updated for itemId: $itemId")
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error updating expiration date for itemId: $itemId", e)
        } finally {
            db.close()
        }
    }

    fun addPantry(pantryName: String) {
        val values = ContentValues()
        values.put(PANTRY_NAME_COL, pantryName)
        val db = this.writableDatabase
        db.insert(TABLE_PANTRIES, null, values)
        db.close()
    }
    fun readPantries(): ArrayList<String>? {
        val db = this.readableDatabase
        val pantryList: ArrayList<String> = ArrayList()
        try {
            // Query to select all rows from the pantries table
            val cursor = db.rawQuery("SELECT * FROM $TABLE_PANTRIES", null)
            while (cursor.moveToNext()) {
                val pantryNameIndex = cursor.getColumnIndex(PANTRY_NAME_COL)
                val pantryName = cursor.getString(pantryNameIndex)
                // Add unique pantry names to the list
                if (!pantryList.contains(pantryName)) { // Check if pantry name already exists in the list
                    pantryList.add(pantryName)
                }
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error reading pantries", e)
        } finally {
            db.close()
        }
        return pantryList
    }

    fun getPantryID(pantryName: String): Long? {
        val db = this.readableDatabase
        var pantryID: Long? = null

        try {
            // Query to select pantry ID based on pantry name
            val cursor = db.rawQuery("SELECT $PANTRY_ID_COL FROM $TABLE_PANTRIES WHERE $PANTRY_NAME_COL = ?", arrayOf(pantryName))

            if (cursor.moveToFirst()) {
                val pantryIdIndex = cursor.getColumnIndex(PANTRY_ID_COL)
                pantryID = cursor.getLong(pantryIdIndex)
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error retrieving pantryID", e)
        } finally {
            db.close()
        }


        return pantryID
    }
    fun getPantryName(pantryId: Long): String? {
        val db = this.readableDatabase
        var pantryName: String? = null

        try {
            // Query to select pantry name based on pantry ID
            val cursor = db.rawQuery("SELECT $PANTRY_NAME_COL FROM $TABLE_PANTRIES WHERE $PANTRY_ID_COL = ?", arrayOf(pantryId.toString()))

            if (cursor.moveToFirst()) {
                val pantryNameIndex = cursor.getColumnIndex(PANTRY_NAME_COL)
                pantryName = cursor.getString(pantryNameIndex)
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error retrieving pantry name", e)
        } finally {
            db.close()
        }

        return pantryName
    }

    fun SearchPantry(text: String, pantryId: Long): ArrayList<PantryModel> {
        val db = this.readableDatabase
        val itemList: ArrayList<PantryModel> = ArrayList()

        try {
            val cursor = db.rawQuery("SELECT * FROM $TABLE_ITEMS WHERE $ITEM_NAME_COL LIKE ? AND $PANTRY_ID_COL = ?", arrayOf("%$text%", pantryId.toString()))

            while (cursor.moveToNext()) {
                val itemIdIndex = cursor.getColumnIndex(ITEM_ID_COL)
                val itemNameIndex = cursor.getColumnIndex(ITEM_NAME_COL)
                val itemCountIndex = cursor.getColumnIndex(ITEM_COUNT_COL)
                val itemExpirationIndex = cursor.getColumnIndex(ITEM_EXPIRATION_COL)
                val pantryIdIndex = cursor.getColumnIndex(PANTRY_ID_COL)

                itemList.add(
                    PantryModel(
                        cursor.getInt(itemIdIndex),
                        cursor.getString(itemNameIndex),
                        cursor.getInt(itemCountIndex),
                        cursor.getString(itemExpirationIndex),
                        cursor.getLong(pantryIdIndex)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("DBHandler", "Error searching pantry", e)
        } finally {
            db.close()
        }

        return itemList
    }
}
