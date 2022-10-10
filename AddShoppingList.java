package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AddShoppingList extends AppCompatActivity {
    DatabaseManagerGrocery dbManager;
    ListAdapter adapter;
    ListView list;
    EditText id, name, location, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);

        //Get used widgets
        list = findViewById(R.id.GroceryRecordListView);
        id = findViewById(R.id.ShoppingIdEntry);
        name = findViewById(R.id.ShopNameEntry);
        location = findViewById(R.id.ShoppingLocationEntry);
        date = findViewById(R.id.DateEntry);
        dbManager = new DatabaseManagerGrocery(this); //set dbManager for this context

        //Set button functionality
        findViewById(R.id.BackBtn).setOnClickListener(v -> { //Open View Shopping page
            Intent intent = new Intent(this, ViewShoppingList.class);
            startActivity(intent);
        });
        findViewById(R.id.HomeBtn).setOnClickListener(v -> { //Open Home Page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.ClearBtn).setOnClickListener(v -> ClearPage());
        findViewById(R.id.SubmitBtn).setOnClickListener(v -> Submit());
        SetList();
    }

    public void SetList() { //Sets the list data
        ArrayList<String> tableContent = dbManager.retrieveRows(); //get the rows from database
        adapter = new ListAdapter(this, tableContent); //Create the custom ListAdapter with retrieved rows
        list = findViewById(R.id.GroceryRecordListView);
        list.setAdapter(adapter);
    }

    public void ClearPage() { //Clears all text field and unchecks boxes
        id.setText("");
        name.setText("");
        location.setText("");
        date.setText("");
        adapter.UncheckAll();
    }

    public void Submit() { //Submits record to database
        if(id.getText().toString().matches("") || name.getText().toString().matches("") || location.getText().toString().matches("") || date.getText().toString().matches("")) { //if any fields are empty
            Toast.makeText(this, "Please Fill All Data", Toast.LENGTH_LONG).show();
            return;
        }

        try{ //Date validation
            DateFormat df = new SimpleDateFormat("dd/MM/yyy"); //data format used throughout this app
            df.setLenient(false); //Date format is strict on the format
            df.parse(date.getText().toString()); //parse the date string to format
        } catch (Exception e) { //Didn't format, incorrect format
            Toast.makeText(this, "Invalid Date: dd/mm/yyyy", Toast.LENGTH_LONG).show();
            return;
        }

        String groceryList = "";
        ListAdapter.ViewHolder[] views = adapter.GetViewHolders(); //get the array of view holders in list order
        for(int i = 0; i < views.length; i++) {
            if(views[i].isChecked) { //for each checked box
                String id = list.getAdapter().getItem(i).toString();
                id = id.substring(0, id.indexOf(",")); //get up until the first comma, which is the id
                groceryList += id + "@"; //@ used to split into each id
            }
        }
        if(groceryList.matches("")) { //if no groceries are selected
            Toast.makeText(this, "Select Grocery Items", Toast.LENGTH_SHORT).show();
            return;
        }
        if(dbManager.addRow(Integer.parseInt(id.getText().toString()), name.getText().toString(), location.getText().toString(), date.getText().toString(), groceryList)){ClearPage();} //clear page if record was inserted

    }
}