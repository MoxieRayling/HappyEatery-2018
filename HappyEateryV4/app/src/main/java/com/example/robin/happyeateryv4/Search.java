package com.example.robin.happyeateryv4;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import static com.example.robin.happyeateryv4.Landing.REQUEST_LOCATION;

public class Search extends Activity {

    private EditText postcode;
    private CheckBox thisLoc;
    static final int REQUEST_LOCATION = 1;
    private LocationManager lm;
    private String distance = "10";
    private double longitude = -3;
    private double latitude = 53;
    private String rating = "1";
    private Resources res;
    private String comparator;
    private String lessThan;
    private String greaterThan;
    private String equal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        res = getResources();
        lessThan = res.getString(R.string.comparator_less_than);
        greaterThan = res.getString(R.string.comparator_greater_than);
        comparator = res.getString(R.string.comparator_greater_than);
        equal = res.getString(R.string.comparator_equal);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        postcode = findViewById(R.id.uiPostcodeEditText);
        thisLoc = findViewById(R.id.uiLocationCheckBox);
        final TextView ratingText = findViewById(R.id.uiRatingTextView);
        NumberPicker rating = (NumberPicker) findViewById(R.id.uiRatingNumberPicker);
        rating.setMaxValue(5);
        rating.setMinValue(1);
        rating.setWrapSelectorWheel(true);
        rating.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                ratingText.setText("Rating : " + newVal);
                setRating(newVal);
            }
        });


        final TextView distanceText = findViewById(R.id.uiDistanceTextView);
        final SeekBar distance = findViewById(R.id.uiDistanceSeekBar);
        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                distanceText.setText("Distance in Miles: " + i);
                setDistance(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        RadioGroup comparators = findViewById(R.id.uiComparatorGroup);
        comparators.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("radio", String.valueOf(i));
                switch (i) {
                    case 2131231008:
                        setComparator(lessThan);
                        break;
                    case 2131231006:
                        setComparator(greaterThan);
                        break;
                    case 2131231002:
                        setComparator(equal);
                        break;
                    default:
                        setComparator(greaterThan);
                        break;
                }
            }
        });
    }

    public void tryLocation(View view){
        if (getLocation() && thisLoc.isChecked()) {
            search();
        } else if (getLocationFromPostcode(postcode.getText().toString())) {
            search();
        } else {
            Toast.makeText(this, "Could not resolve postcode",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void search() {
        Intent intent = new Intent(this, ResultTabs.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("distance", String.valueOf(distance));
        intent.putExtra("rating", String.valueOf(rating));
        intent.putExtra("comparator", comparator);
        startActivity(intent);
    }



    public void setDistance(int distance) {
        this.distance = String.valueOf(distance);
    }

    public void setRating(int rating) {
        this.rating = String.valueOf(rating);
    }

    public void setComparator(String comparator){
        this.comparator = comparator;
    }

    private boolean getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
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
}

