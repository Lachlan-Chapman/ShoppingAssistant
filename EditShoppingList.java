package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class EditShoppingList extends AppCompatActivity {
    String passedValue[];
    DatabaseManagerGrocery dbManager;
    ListAdapter adapter;
    ListView list;
    EditText id, name, location, date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list);
        Bundle value = getIntent().getExtras(); //gets values passed in
        if(value != null) { //if there are values
            String temp = value.getString("ListInfo");
            String location = temp.substring(temp.indexOf(",") + 1, temp.lastIndexOf(",") -1); //removes the first and last comma, making the first and last comma be either side of the location
            location = location.substring(location.indexOf(",") + 2, location.lastIndexOf(",")); //gets the location component +2 removes the command first whitespace
            temp = temp.replaceAll("\\s", ""); //remove whitespaces
            passedValue = temp.split(","); //split by separating ,
            passedValue[2] = location;
        }
        list = findViewById(R.id.GroceryRecordListView);
        dbManager = new DatabaseManagerGrocery(this);

        //get and set widgets
        id = findViewById(R.id.ShoppingIdEntry);
        id.setText(passedValue[0]);
        name = findViewById(R.id.ShopNameEntry);
        name.setText(passedValue[1]);
        location = findViewById(R.id.ShoppingLocationEntry);
        location.setText(passedValue[2]);
        date = findViewById(R.id.DateEntry);
        date.setText(passedValue[3]);


        //button functionality
        findViewById(R.id.BackBtn).setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewEditShoppingList.class);
            startActivity(intent);
        });
        findViewById(R.id.HomeBtn).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.ClearBtn).setOnClickListener(v -> ClearPage());
        findViewById(R.id.UpdateBtn).setOnClickListener(v -> Update());
        SetList();
    }


    public void SetList() { //set the grocery item list
        dbManager = new DatabaseManagerGrocery(this);
        ArrayList<String> tableContent = dbManager.retrieveRows(); //get the grocery items
        adapter = new ListAdapter(this, tableContent); //create the adapter with appropriate values
        list = findViewById(R.id.GroceryRecordListView); //get the list from the view
        list.setAdapter(adapter); //set the list to the custom adapter
    }

    public void ClearPage() { //resets fields of the page
        id.setText("");
        name.setText("");
        location.setText("");
        date.setText("");
        adapter.UncheckAll();
    }

    public void Update() { //update the row in the database
        dbManager = new DatabaseManagerGrocery(this);
        if(id.getText().toString().matches("") || name.getText().toString().matches("") || location.getText().toString().matches("") || date.getText().toString().matches("")) { //data validation of the editText widgets
            Toast.makeText(this, "Please Fill All Data", Toast.LENGTH_LONG).show();
            return;
        }
        try{ //date data validation
            DateFormat df = new SimpleDateFormat("dd/MM/yyy");
            df.setLenient(false);
            df.parse(date.getText().toString()); //try and parse as a date
        } catch (Exception e) { //if it cannot parse as date (invalid data format)
            Toast.makeText(this, "Invalid Date: dd/mm/yyyy", Toast.LENGTH_LONG).show();
            return;
        }
        String groceryList = ""; //empty grocery list id string
        ListAdapter.ViewHolder[] views = adapter.GetViewHolders(); //get each item of the list as a custom view object
        for(int i = 0; i < views.length; i++) {
            if(views[i].isChecked) { //if the check box is ticked
                String id = list.getAdapter().getItem(i).toString();
                id = id.substring(0, id.indexOf(","));
                groceryList += id + "@"; //add to the grocery list separated by @
            }
        }
        if(groceryList.matches("")) { //if still no grocery id (no selected groceries)
            Toast.makeText(this, "Select Grocery Items", Toast.LENGTH_SHORT).show();
            return;
        }
        if(dbManager.updateRow(passedValue[0], name.getText().toString(), location.getText().toString(), date.getText().toString(), groceryList) > 0) { //the row updated correctly
            Toast.makeText(this, "LIST UPDATED", Toast.LENGTH_SHORT).show();
        }
    }
}