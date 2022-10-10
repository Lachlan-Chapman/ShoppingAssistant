package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ViewEditShoppingList extends AppCompatActivity {
    private DatabaseManagerGrocery dbManager;
    private ListView list;
    private ListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_shopping_list);

        //button functionality
        findViewById(R.id.BackBtn).setOnClickListener(v -> { //open view shopping list
            Intent intent = new Intent(this, ViewShoppingList.class);
            startActivity(intent);
        });
        findViewById(R.id.HomeBtn).setOnClickListener(v -> { //open home page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        SetList();

    }

    public void SetList() {
        dbManager = new DatabaseManagerGrocery(this);
        ArrayList<String> lists = dbManager.retrieveRows(true); //gets the grocery records
        adapter = new ListAdapter(this, lists);
        list = findViewById(R.id.ListView);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, v, position, id) -> { //set the onclick listener
            Intent intent = new Intent(this, EditShoppingList.class);
            intent.putExtra("ListInfo", list.getAdapter().getItem(position).toString()); //passes the selected list data into the edit activity
            startActivity(intent); //open the edit grocery item page
        });
    }
}