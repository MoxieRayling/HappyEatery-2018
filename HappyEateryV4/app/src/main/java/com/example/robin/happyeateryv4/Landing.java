package com.example.robin.happyeateryv4;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class Landing extends Activity {

    private EditText postcode;
    private CheckBox thisLoc;
    static final int REQUEST_LOCATION = 1;
    private LocationManager lm;
    private double latitude = 53;
    private double longitude = -3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        postcode = findViewById(R.id.uiPostcodeEditText);
        thisLoc = findViewById(R.id.uiLocationCheckBox);
    }

    public void tryLocation(View view) {
        if (getLocation() && thisLoc.isChecked()) {
            search();
        } else if (getLocationFromPostcode(postcode.getText().toString())) {
            search();
        } else {
            Toast.makeText(this, "Could not resolve postcode",
                    Toast.LENGTH_SHORT).show();
        }
        Log.d("location", latitude + " " + longitude);
    }

    private void search() {
        Intent intent = new Intent(this, ResultTabs.class);

        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("distance", "50");
        intent.putExtra("rating", "1");
        intent.putExtra("comparator", "GreaterThanOrEqual");

        Log.d("intent", String.valueOf(longitude));
        Log.d("intent", String.valueOf(latitude));

        startActivity(intent);
    }

    private boolean getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                return true;
            }
        }
        return false;
    }

    private boolean getLocationFromPostcode(String postcode) {
        final Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(postcode, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                latitude = address.getLatitude();
                longitude = address.getLongitude();
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    public void advancedSearch(View view){
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

    public void favourites(View view){
        getLocation();
        getLocationFromPostcode(postcode.getText().toString());
        Intent intent = new Intent(this, Favourites.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        Log.d("intent", String.valueOf(longitude));
        Log.d("intent", String.valueOf(latitude));
        startActivity(intent);
    }
}
