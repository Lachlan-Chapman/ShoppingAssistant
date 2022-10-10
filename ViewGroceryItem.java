package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;

public class ViewGroceryItem extends AppCompatActivity {
    private DatabaseManagerGrocery dbManager;
    private ListView list;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grocery_item);

        //button functionality
        findViewById(R.id.HomeBtn).setOnClickListener(v -> { //open home page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.AddRecordBtn).setOnClickListener(v -> { //open add grocery page
            Intent intent = new Intent(this, AddGroceryItem.class);
            startActivity(intent);
        });
        findViewById(R.id.DeleteRecordBtn).setOnClickListener(v -> DeleteSelected());
        SetList();
    }


    public void SetList() { //sets the grocery list
        dbManager = new DatabaseManagerGrocery(this);
        ArrayList<String> tableContent = dbManager.retrieveRows(); //gets grocery data from database
        adapter = new ListAdapter(this, tableContent);
        list = findViewById(R.id.GroceryRecordListView);
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, v, position, id) -> { //sets the onclick listener to open edit grocery page
            Intent intent = new Intent(this, EditGroceryItem.class);
            intent.putExtra("RecordInfo", list.getAdapter().getItem(position).toString()); //passes the selected grocery database data into the edit grocery page
            startActivity(intent);
        });
    }

    public void DeleteSelected() {
        ListAdapter.ViewHolder[] records = adapter.GetViewHolders();
        for(int i = 0; i < records.length; i++) {
            if(records[i].checkBox.isChecked()) {
                String id = list.getAdapter().getItem(i).toString();
                id = id.substring(0, id.indexOf(","));
                dbManager.DeleteRecord("GroceryID", id, "Groceries");
            }
        }
        SetList();
    }

}