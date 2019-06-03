package com.example.robin.happyeateryv4;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ResultsMaps extends Fragment
{
    private MapView mMapView;
    private GoogleMap googleMap;
    private LatLng center;
    private ArrayList<Restaurant> restaurants;
    private ArrayList<Marker> markers;
    private int showResults = 0;
    private int pageSize = 0;
    private int total = 0;
    private TextView resultsText;
    private SeekBar resultsSeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.results_map,container,false);
        restaurants = new ArrayList<Restaurant>();
        markers = new ArrayList<Marker>();
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        resultsText = rootView.findViewById(R.id.uiResultsTextView);
        resultsSeek = rootView.findViewById(R.id.uiResultsSeekBar);
        resultsSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showResults = progress;
                removeMarkers();
                updateText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                CameraPosition cameraPosition = new CameraPosition.Builder().target(center).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Log.d("click", "i clikced");
                        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                        intent.putExtra("Restaurant", (Restaurant) marker.getTag());
                        startActivity(intent);
                    }
                });

            }
        });
        if (googleMap != null) {
            addRestaurants(restaurants);
            Log.d("click", "not null");

        }
        return rootView;
    }

    public void addMarker(Restaurant rest){
        LatLng loc = new LatLng(rest.getLatitude(),rest.getLongitude());
        Marker marker = googleMap.addMarker(new MarkerOptions().position(loc).title(rest.getName()).snippet(rest.getType() + " " + rest.getRating()+"/5"));
        marker.setTag(rest);
        markers.add(marker);
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public void addRestaurants(ArrayList<Restaurant> restaurants) {
        resultsSeek.setMax(restaurants.size()-pageSize);
        showResults = markers.size();
        this.restaurants = restaurants;
        for(Restaurant rest: restaurants){
            if(googleMap != null && restaurants.indexOf(rest) >= markers.size()){
                    addMarker(rest);
            }
        }
        removeMarkers();
        updateText();
    }

    private void removeMarkers(){
        for(Marker marker:markers){
            int index = restaurants.indexOf(marker.getTag());
            if(index < showResults || index > showResults + pageSize){
                Log.d("Map","hiding " + index + " out of " + markers.size() + ", " + showResults);
                marker.setVisible(false);
            } else {

                Log.d("Map","showing " + index + " out of " + markers.size() + ", " + showResults);
                marker.setVisible(true);
            }
        }
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    private void updateText(){
        resultsText.setText("Showing results " + Math.min((showResults + 1),total) + " to "+ Math.min(showResults + pageSize,total) + " out of " + markers.size() + " loaded results (" + total + " total results)");
    }
}
