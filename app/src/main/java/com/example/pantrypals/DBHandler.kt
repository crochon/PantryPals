package com.example.pantrypals


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHandler  // creating a constructor for our database handler.
    (context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    // below method is for creating a database by running a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // on below line we are creating an sqlite query and we are
        // setting our column names along with their data types.
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + COUNT_COL + " TEXT,"
                + EXPIRATION_COL + " TEXT);")

        // at last we are calling a exec sql method to execute above sql query
        db.execSQL(query)
    }

    // this method is use to add new course to our sqlite database.
    public fun addNewGrocery(
        ItemName: String?,
        ItemCount: Int?,
        ItemExpiration: Int?,
    ) {
        // on below line we are creating a variable fors
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        val db = this.writableDatabase
        // on below line we are creating a
        // variable for content values.
        val values = ContentValues()
        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, ItemName)
        values.put(COUNT_COL, ItemCount)
        values.put(EXPIRATION_COL, ItemExpiration)
        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values)
        // at last we are closing our
        // database after adding database.
        db.close()
    }

    /*
        Removes a Grocery Item Based on it's ID
     */
    public fun removeGrocery(ItemID: Int?){
        val db = this.writableDatabase

        println("$ItemID")
        // delete row where id matches given parameters.
        db.delete(TABLE_NAME, "$ID_COL=?", arrayOf(ItemID.toString()))

        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    companion object {
        // creating a constant variables for our database.
        // below variable is for our database name.
        private const val DB_NAME = "Pantry_DB"

        // below int is our database version
        private const val DB_VERSION = 1

        // below variable is for our table name.
        private const val TABLE_NAME = "Pantry_Name"

        // below variable is for our id column.
        private const val ID_COL = "id"

        // below variable is for our course name column
        private const val NAME_COL = "Grocery_Name"

        // below variable id for our course duration column.
        private const val COUNT_COL = "Grocery_Count"

        // below variable for our course description column.
        private const val EXPIRATION_COL = "Grocery_Expiration"
    }

    // we have created a new method for reading all the courses.
    public fun readGroceries(): ArrayList<PantryModel>? {
        // on below line we are creating a database for reading our database.
        val db = this.readableDatabase

        // on below line we are creating a cursor with query to read data from database.
        val cursorPantry: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        // on below line we are creating a new array list.
        val courseModelArrayList: ArrayList<PantryModel> = ArrayList()

        // moving our cursor to first position.
        if (cursorPantry.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModelArrayList.add(
                    PantryModel(
                        cursorPantry.getInt(0),
                        cursorPantry.getString(1),
                        cursorPantry.getInt(2),
                        cursorPantry.getInt(3)
                    )
                )
            } while (cursorPantry.moveToNext())
            // moving our cursor to next.
        }
        // at last closing our cursor and returning our array list.
        cursorPantry.close()
        return courseModelArrayList
    }
}