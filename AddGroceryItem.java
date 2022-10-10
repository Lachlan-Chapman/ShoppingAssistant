package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddGroceryItem extends AppCompatActivity {
    private EditText id, name, quantity;
    private ImageView image;
    private String imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grocery_item);

        //get all EditText widgets
        id = findViewById(R.id.GroceryIdEntry);
        name = findViewById(R.id.GroceryNameEntry);
        quantity = findViewById(R.id.GroceryQuantityEntry);

        //set button functionality
        findViewById(R.id.SubmitBtn).setOnClickListener(v -> InsertRecord());
        findViewById(R.id.ClearBtn).setOnClickListener(v -> ClearPage());
        findViewById(R.id.HomeBtn).setOnClickListener(v -> { //Open home page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.BackBtn).setOnClickListener(v -> { //Open View Grocery Page
            Intent intent = new Intent(this, ViewGroceryItem.class);
            startActivity(intent);
        });
        findViewById(R.id.GroceryImageView).setOnClickListener(v -> { //Open gallery to pick image
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);
        });
        image = findViewById(R.id.GroceryImageView); //get ImageView
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //if image was selected
        if(resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData(); //Get image as URI
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) { //if the current sdk is beyond jelly bean mr2, where this persistable permission issue forms
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //get persistable permission
            }

            image.setImageURI(uri); //Set image
            imageUri = uri.toString(); //Used to store in database as string
        }


    }

    private void InsertRecord() {
        DatabaseManagerGrocery dbManager = new DatabaseManagerGrocery(AddGroceryItem.this);
        if(id.getText().toString().matches("") || name.getText().toString().matches("") || quantity.getText().toString().matches("")){ //if any fields are empty
            Toast.makeText(this, "Enter All Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(dbManager.addRow(Integer.parseInt(id.getText().toString()), name.getText().toString(), Integer.parseInt(quantity.getText().toString()), imageUri)) {ClearPage();} //if record is inserted correctly
    }

    private void ClearPage() { //Clears all text and resets image
        id.setText("");
        name.setText("");
        quantity.setText("");
        image.setImageResource(R.drawable.no_image);
    }


}