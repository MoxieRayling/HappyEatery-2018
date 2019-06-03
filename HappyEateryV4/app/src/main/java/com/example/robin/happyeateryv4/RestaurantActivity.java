package com.example.robin.happyeateryv4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RestaurantActivity extends Activity {

    private MapView mMapView;
    private LatLng loc;
    private GoogleMap googleMap;
    private Restaurant rest;
    private TextView name;
    private TextView desc;
    private RestaurantDatabaseHelper dbHelper;
    private Button favouriteButton;
    private boolean exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        Intent intent = getIntent();
        loc = new LatLng(intent.getDoubleExtra("latitude", 53), intent.getDoubleExtra("longitude", -3));
        rest = intent.getParcelableExtra("Restaurant");
        mMapView = (MapView) findViewById(R.id.uiRestaurantMap);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                LatLng loc = new LatLng(rest.getLatitude(), rest.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.addMarker(new MarkerOptions().position(loc).title(rest.getName()).snippet(rest.getType() + " " + rest.getRating() + "/5"));

            }
        });
        name = findViewById(R.id.uiNameTextView);
        name.setText(rest.getName());
        desc = findViewById(R.id.uiDescTextView);
        desc.setText("Type: " + rest.getType() + "\n\nRating: " + rest.getRating() + "/5 \n\n"
                + String.format("%.2f miles away\n\nAddress:\n",rest.distance(loc)) + rest.getAdress());
        favouriteButton = findViewById(R.id.uiFaveButton);
        dbHelper = new RestaurantDatabaseHelper(this);
        setExists(dbHelper.getRestById(String.valueOf(rest.getId())) != null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = new RestaurantDatabaseHelper(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dbHelper != null)
            dbHelper.close();
    }

    public void favourite(View view) {
        if (exists) {
            dbHelper.deleteRest(String.valueOf(rest.getId()));
            setExists(false);
        } else {
            setExists(dbHelper.insertRestaurant(rest));
        }
    }

    private void setExists(boolean exists) {
        this.exists = exists;
        if (exists) {
            favouriteButton.setText("Unfave");
        } else {
            favouriteButton.setText("Fave");
        }
    }
}
