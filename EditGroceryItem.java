package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditGroceryItem extends AppCompatActivity {
    String[] passedValue;
    private DatabaseManagerGrocery dbManager;
    EditText id, name, quantity;
    ImageView image;
    String imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_grocery_item);
        Bundle value = getIntent().getExtras(); //get row data passed in from view grocery page
        if(value != null) { //make sure data is actually passed in
            String temp = value.getString("RecordInfo");
            temp = temp.replaceAll("\\s", ""); //remove all whitespaces
            passedValue = temp.split(","); //split into elements by ','
        }

        //get used views
        id = findViewById(R.id.GroceryIdEntry);
        id.setText(passedValue[0]);
        name = findViewById(R.id.GroceryNameEntry);
        name.setText(passedValue[1]);
        quantity = findViewById(R.id.GroceryQuantityEntry);
        quantity.setText(passedValue[2]);
        image = findViewById(R.id.GroceryImageView);
        image.setImageURI(Uri.parse(passedValue[3]));

        findViewById(R.id.GroceryImageView).setOnClickListener(v -> { //on click, opens device gallery
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //open gallery to pick and return an image URI
            startActivityForResult(intent, 3); //start activity with this activity specific request code
        });


        //implement button functionality
        findViewById(R.id.ClearBtn).setOnClickListener(v -> ClearPage());
        findViewById(R.id.HomeBtn).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.BackBtn).setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewGroceryItem.class);
            startActivity(intent);
        });

        findViewById(R.id.UpdateBtn).setOnClickListener(v -> Update());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //process the selected image
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData(); //Get image as URI

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) { //if the current sdk is beyond jelly bean mr2, where this persistable permission issue forms
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //get persistable permission
            }

            image.setImageURI(uri); //Set image
            imageUri = uri.toString(); //Used to store in database as string
        }
    }

    private void ClearPage() { //resets all page widgets
        id.setText("");
        name.setText("");
        quantity.setText("");
        image.setImageResource(R.drawable.no_image);
    }

    private void Update() { //updates the database row
        dbManager = new DatabaseManagerGrocery(this);
        if(id.getText().toString().matches("") || name.getText().toString().matches("") || quantity.getText().toString().matches("")){ //data validation
            Toast.makeText(this, "Enter All Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(dbManager.updateRow(passedValue[0], name.getText().toString(), Integer.parseInt(quantity.getText().toString()), imageUri) == 1) { //if row updated successfully
            Toast.makeText(this, "ITEM UPDATED", Toast.LENGTH_SHORT).show();
        }
    }
}