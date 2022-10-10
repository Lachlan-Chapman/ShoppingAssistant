package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewShoppingList extends AppCompatActivity {
    ExpandAdapter expandAdapter;
    ExpandableListView listView;
    List<String> headerData;
    HashMap<String, List<String>> childData;
    DatabaseManagerGrocery dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shopping_list);

        //implements button functionality
        findViewById(R.id.AddRecordBtn).setOnClickListener(v -> { //open add shopping list page
            Intent intent = new Intent(this, AddShoppingList.class);
            startActivity(intent);
        });
        findViewById(R.id.BackBtn).setOnClickListener(v -> { //open home page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.DeleteRecordBtn).setOnClickListener(v -> DeleteSelectedRecords());
        findViewById(R.id.EditList).setOnClickListener(v -> { //open page which shows the lists to be able to edit them
            Intent intent = new Intent(this, ViewEditShoppingList.class);
            startActivity(intent);
        });
        listView = findViewById(R.id.ListView);
        SetList();
    }

    public void SetList() {
        PrepareData(); //orders data based on date
        expandAdapter = new ExpandAdapter(this, headerData, childData);
        listView.setAdapter(expandAdapter);
    }

    public void PrepareData() { //prepares data with some rules before setting the adapter with the data
        dbManager = new DatabaseManagerGrocery(this);
        headerData = new ArrayList<>(); //this stores the header data
        childData = new HashMap<>(); //this stores the child data for each row
        ArrayList<String> rowData = dbManager.retrieveRows(true);
        rowData = SortRecordByDate(rowData); //sort the lists by their date
        String header, record, groceryList;
        String[] recordElement;
        for(int i = 0; i < rowData.size(); i++) { //for each list
            List<String> childString = new ArrayList<>(); //the list of grocery items for the current list
            record = rowData.get(i);
            record = record.replaceAll("\\s", ""); //remove whitespaces
            recordElement = record.split(","); //split into elements
            groceryList = recordElement[4];
            header = recordElement[0] + " | "  + recordElement[3] + " | " + recordElement[1] + " - " + recordElement[2]; //set the header text with my chosen formatting
            headerData.add(header);
            String[] groceryIds = groceryList.split("@"); //split the grocery ids by @ symbol
            for (String groceryId : groceryIds) { //for each grocery item
                childString.add(dbManager.retrieveRow(groceryId)); //retrieve the grocery by the id, append it the current list child data
            }
            childData.put(headerData.get(i), childString);
        }
    }

    public ArrayList<String> SortRecordByDate(ArrayList<String> oldData) { //fun fact, i full miss read the "view past and current" part in the marking criteria and made a custom insertion sort implementation for the lists. then realised and made the colored lists based on date
        String record, dateText, dateNum;
        String[] infoElements, dateElements;
        ArrayList<String> unsortedData = new ArrayList<>(); //this is the List to be sorted
        for(int i = 0; i < oldData.size(); i++) {
            record = oldData.get(i); //get current record
            dateText = record.replaceAll("\\s", ""); //remove all whitespaces
            infoElements = dateText.split(","); //split into fields
            try{
                dateText = infoElements[3];
                dateElements = dateText.split("/"); //split into day, month and year
                if(dateElements[0].length() < 2){dateElements[0] = "0"+dateElements[0];} //date will be accepted by user if there is no 0 example 7/8/2022 is valid
                if(dateElements[1].length() < 2){dateElements[1] = "0"+dateElements[1];} //since this converts the date into a number and sorts based on size. a bigger date will seem smaller. this keeps it all consistent so that year can be as long and be valid
                dateNum = dateElements[2]+dateElements[1]+dateElements[0]; //year then month then day EG: 7/8/2022 = 20220807
                unsortedData.add(dateNum + ", " + record); //add the number with the record to the to be sorted list
            }catch (Exception e) { //worst case the list gets put to the top (on screen) of the grocery lists
                unsortedData.add("-1, " + record);
            }
        }

        int currentValue, position, brokeCounter = 0;
        //Insertion sort
        for(int i = 1; i < unsortedData.size(); i++) { //start at 2nd element
            try {
                record = unsortedData.get(i); //get current record
                position = i-1;//get previous record
                currentValue = Integer.parseInt(record.substring(0, record.indexOf(","))); //current record date value (not a date, the converted to number)

                //the int parsing has to be done at the start of each loop, hence its long in the while condition rather than set outside of the loop as a neat variable
                while (position >= 0 && Integer.parseInt(unsortedData.get(position).substring(0, unsortedData.get(position).indexOf(","))) > currentValue) { //position is more than 0 and our prior records date is larger than the current record date
                    unsortedData.set(position+1, unsortedData.get(position)); //set the current record string to the prior record string (moving it up in the list)
                    position--; //change the position we are looking at the be one less
                }
                //while loop has come to a date which is less than current record's date
                unsortedData.set(position+1, record); //overwrite the last, larger record's date position
            } catch (Exception e) { //used all for debugging, this now never occurs
                brokeCounter++;
                Toast.makeText(this, "Something broke: " + brokeCounter, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        for(int i = 0; i < unsortedData.size(); i++) { //removes the date number from the list
            record = unsortedData.get(i);
            record = record.substring(record.indexOf(",")+1, record.length()-1); //from the comma to the end
            unsortedData.set(i, record);
        }
        ArrayList<String> sortedData = unsortedData; //for naming conventions sake
        return sortedData;
        // -> old code (bad naming conventions) return unsortedData; //now sorted haha
    }

    public void DeleteSelectedRecords() { //deletes selected records
        ExpandAdapter.ListHolder[] listHolders = expandAdapter.GetListHolders(); //gets the list holder object array
        for (ExpandAdapter.ListHolder listHolder : listHolders) { //for each list holder
            if (listHolder.checkBox.isChecked()) { //if the checkbox widget is checked
                dbManager.DeleteRecord("ListID", listHolder.listId, "Shopping"); //delete by ListID attribute, from the Shopping table
            }
        }
        SetList(); //reset the list
    }

}