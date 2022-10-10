package com.example.grocery002;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;

public class DatabaseManagerGrocery {
    public static final String DB_NAME = "GroceryInventory"; //Database name
    public static final String DB_TABLE = "Groceries"; //First table name
    public static final String DB_TABLE2 = "Shopping"; //Second table name
    public static final int DB_VERSION = 8; //Increment this to reset database and recreate table
    public SQLHelper helper;
    private final SQLiteDatabase db;
    private final Context context;

    public DatabaseManagerGrocery(Context c) {
        this.context = c; //set activity context
        helper = new SQLHelper(this.context); //creates helper object with activity context
        this.db = helper.getWritableDatabase(); //assign a writable database
    }

    //--UPDATE--
    public int updateRow(String id, String name, Integer quantity, String image) { //Grocery item update
        SQLiteDatabase database = helper.getWritableDatabase(); //get a database to perform write operations
        ContentValues updatedData = new ContentValues(); //record object
        updatedData.put("Name", name);
        updatedData.put("Quantity", quantity);
        updatedData.put("Image", image);
        return database.update(DB_TABLE, updatedData, "GroceryID = '" + id + "'",null); //update query
    }

    public int updateRow(String id, String name, String location, String date, String groceryList) { //Shopping List update
        SQLiteDatabase database = helper.getWritableDatabase(); //get writable database
        ContentValues updatedData = new ContentValues(); //record object
        updatedData.put("Name", name);
        updatedData.put("Location", location);
        updatedData.put("Date", date);
        updatedData.put("GroceryList", groceryList);
        return database.update(DB_TABLE2, updatedData, "ListID = '" + id + "'",null); //update query
    }
    //--END UPDATE--

    //--DELETE--
    public void DeleteRecord(String key, String id, String tableName) { //delete record using row, value and table name
        db.execSQL("DELETE FROM " + tableName + " WHERE " + key + "= '" + id + "'");
    }
    //--END DELETE--

    //--INSERT--
    public boolean addRow(Integer id, String name, Integer quantity, String image) { //Grocery item add
        synchronized (this.db) {
            ContentValues newRow = new ContentValues(); //record object
            try{
                newRow.put("GroceryID", id);
                newRow.put("Name", name);
                newRow.put("Quantity", quantity);
                newRow.put("Image", image);
                db.insertOrThrow(DB_TABLE, null, newRow); //insert record or create an error
                Toast.makeText(context.getApplicationContext(), "Record Inserted", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) { //something went wrong inserting, data is validated only id exists
                Toast.makeText(context.getApplicationContext(), "ID Already Exists", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    public boolean addRow(Integer id, String name, String location, String date, String groceryList) { //Shopping list add
        synchronized (this.db) {
            ContentValues newRow = new ContentValues(); //record object
            try{
                newRow.put("ListID", id);
                newRow.put("Name", name);
                newRow.put("Location", location);
                newRow.put("Date", date);
                newRow.put("GroceryList", groceryList);
                db.insertOrThrow(DB_TABLE2, null, newRow); //insert record or create an error
                Toast.makeText(context.getApplicationContext(), "RECORD INSERTED", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) { //data is validated, id can only already exist
                Toast.makeText(context.getApplicationContext(), "ID Already Exists", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
    //--END INSERT--

    //--RETRIEVE--
    public String retrieveRow(String id) { //Grocery single row by id retrieve
        String text = "DELETED"; //this must be changed unless a record has been deleted and is being called
        Cursor cursor = db.rawQuery("SELECT GroceryID, Name FROM " + DB_TABLE + " WHERE GroceryID = '" + id + "';", null); //row retrieve query
        cursor.moveToFirst(); //move to first row
        while(!cursor.isAfterLast()) { //while isn't after the final row
            text = cursor.getInt(0) + ", " + cursor.getString(1); //get the elements of the row (attributes) as the correct data type
            cursor.moveToNext();
        }
        if(!cursor.isClosed()) {cursor.close();} //close the cursor if it isn't
        return text;
    }

    public ArrayList<String> retrieveRows() { //Grocery table retrieval
        ArrayList<String> groceryRow = new ArrayList<>(); //to be filled and returned
        String[] columns = new String[] {"GroceryID", "Name", "Quantity", "Image"}; //attribute names
        Cursor cursor = db.query(DB_TABLE, columns, null, null, null, null, null); //select all columns from table query
        cursor.moveToFirst();//move to first row
        while(!cursor.isAfterLast()) { //while isn't after last row
            groceryRow.add(cursor.getInt(0) + ", " + cursor.getString(1) + ", " + cursor.getInt(2) + ", " + cursor.getString(3)); //get the elements of the row (attributes) as the correct data type
            cursor.moveToNext(); //move to next row
        }
        if(!cursor.isClosed()) {cursor.close();} //if cursor is open, then close
        return groceryRow;
    }

    public ArrayList<String> retrieveRows(boolean overflow) { //Shopping List retrieve
        ArrayList<String> listRow = new ArrayList<>(); //to be filled and returned
        String[] columns = new String[] {"ListID", "Name", "Location", "Date", "GroceryList"}; //attribute names
        Cursor cursor = db.query(DB_TABLE2, columns, null, null, null, null, null); //select all from table query
        cursor.moveToFirst(); //move to first row
        while (!cursor.isAfterLast()) { //while isn't after last row
            listRow.add(cursor.getInt(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3) + ", " + cursor.getString(4));
            cursor.moveToNext(); //move to next row
        }
        if(!cursor.isClosed()) {cursor.close();} //close cursor if it isn't
        if(overflow){Log.d("EASTER EGG", "USED OVERFLOW, HAHA ;)");} //please don't mark me down for this or something, its LITERALLY just so the yellow warning goes away!!
        return listRow;
    }
    //--END RETRIEVE--

    public static class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context c) {
            super(c, DB_NAME, null, DB_VERSION); //link the SQLHelper to this activity
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("DEBUG", "TABLES CREATED");
            db.execSQL("CREATE TABLE " + DB_TABLE + " (GroceryID INTERGER PRIMARY KEY, Name TEXT, Quantity INTEGER, Image TEXT);"); //create grocery item table query
            db.execSQL("CREATE TABLE " + DB_TABLE2 + " (ListID INTERGER PRIMARY KEY, Name TEXT, Location TEXT, DATE TEXT, GroceryList TEXT);"); //create shopping list table
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //When version is changed, drop and recreate the tables
            Log.w("Products table", "Upgrading database i.e. dropping table and re creating it");
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE2);
            onCreate(db);
        }


    }
}
