package com.example.grocery002;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap thisMap;
    private DatabaseManagerGrocery dbManager;
    private ListAdapter adapter;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //button functionality
        findViewById(R.id.ShoppingListBtn).setOnClickListener(v -> { //open view shopping page
            Intent intent = new Intent(this, ViewShoppingList.class);
            startActivity(intent);
        });
        findViewById(R.id.GroceryBtn).setOnClickListener(v -> { //open view grocery page
            Intent intent = new Intent(this, ViewGroceryItem.class);
            intent.addFlags(intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        SetList();


    }

    public void onMapReady(GoogleMap googleMap) {
        thisMap = googleMap;
        try{
            googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        } catch (Exception e) {

        }
    }

    public void setMapToLocation(String location) {
        try{
            Geocoder coder = new Geocoder(this);
            List<Address> addressList = coder.getFromLocationName(location, 5);
            double lat = addressList.get(0).getLatitude();
            double lon = addressList.get(0).getLongitude();
            thisMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker"));
            thisMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 15f));
        } catch (Exception e) {}
    }

    public void SetList() {
        dbManager = new DatabaseManagerGrocery(this);
        ArrayList<String> lists = dbManager.retrieveRows(true); //gets the grocery records
        adapter = new ListAdapter(this, lists);
        list = findViewById(R.id.ShoppingLists);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, v, position, id) -> { //set the onclick listener
            String record = lists.get(position).replaceAll("\\s", "");
            String[] elements = record.split(",");
            setMapToLocation(elements[1] + " " + elements[2] + " Australia");
        });
    }


}