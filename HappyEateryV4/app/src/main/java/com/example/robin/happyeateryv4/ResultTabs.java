package com.example.robin.happyeateryv4;


import android.app.Activity;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultTabs extends Activity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<Restaurant> estabs = new ArrayList<>();
    private double longitude = -3;
    private double latitude = 53;
    private String distance = "5";
    private Resources res;
    private String rating;
    private String comparator;
    private int page = 1;
    private int pageSize = 20;
    private ListView listView;
    private ResultsList list;
    private ResultsMaps map;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tabs);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        res = getResources();
        rating = res.getString(R.string.api_rating_system);
        comparator = res.getString(R.string.comparator_greater_than);

        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("longitude", -3);
        latitude = intent.getDoubleExtra("latitude", 53);
        distance = intent.getStringExtra("distance");
        rating = intent.getStringExtra("rating");
        comparator = intent.getStringExtra("comparator");
        Log.d("intent", String.valueOf(longitude));
        Log.d("intent", String.valueOf(latitude));
        Log.d("intent", distance);
        Log.d("intent", rating);
        Log.d("intent", comparator);
        list = new ResultsList();
        map = new ResultsMaps();
        map.setCenter(new LatLng(latitude, longitude));
        if(savedInstanceState != null){
            page = savedInstanceState.getInt("page");
            estabs = savedInstanceState.getParcelableArrayList("restaurants");
        }
        loadRests();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("page", page);
        outState.putParcelableArrayList("restaurants",estabs);
    }

    public Restaurant parseRestaurant(JSONObject estab) {
        Restaurant rest = null;
        try {
            String business = estab.getString("BusinessName");
            String type = estab.getString("BusinessType");
            int id = estab.getInt("FHRSID");
            String addressLine1 = estab.getString("AddressLine1");
            String addressLine2 = estab.getString("AddressLine2");
            String addressLine3 = estab.getString("AddressLine3");
            String addressLine4 = estab.getString("AddressLine4");
            String postcode = estab.getString("PostCode");
            JSONObject geocode = estab.getJSONObject("geocode");
            double longitude = geocode.getDouble("longitude");
            double latitude = geocode.getDouble("latitude");
            double distance = estab.getDouble("Distance");
            int rating = estab.getInt("RatingValue");
            rest = new Restaurant(business, type, id, addressLine1, addressLine2,
                    addressLine3, addressLine4, postcode, longitude, latitude, rating);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rest;
    }

    public void loadRests() {
        toast("Loading results...");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String requestUrl = "http://api.ratings.food.gov.uk/establishments?longitude=" + longitude + "&latitude="
                + latitude + "&maxDistanceLimit=" + distance + "&schemeTypeKey=FHRS&ratingKey=" + rating + "&ratingOperatorKey="
                + comparator + "&sortOptionKey=distance&pageNumber=" + page + "&pageSize=" + pageSize;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject meta = response.getJSONObject("meta");
                            total = meta.getInt("totalCount");
                            map.setPageSize(pageSize);
                            map.setTotal(total);
                            int check = Math.min(pageSize, total - (page-1)*pageSize );
                            JSONArray places = response.getJSONArray("establishments");
                            Log.d("api", String.valueOf(pageSize));
                            for (int i = 0; i < check; i++) {
                                Restaurant rest = parseRestaurant(places.getJSONObject(i));
                                estabs.add(rest);
                                Log.d("Restaurant", places.getJSONObject(i).getString("BusinessName"));
                            }
                            map.addRestaurants(estabs);

                            final ArrayAdapter<Restaurant> adapter = new RestaurantArrayAdapter(ResultTabs.this, 0, estabs, new LatLng(latitude, longitude));
                            listView = findViewById(R.id.uiResultsListView);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(ResultTabs.this, RestaurantActivity.class);
                                    Restaurant rest = adapter.getItem(position);
                                    intent.putExtra("Restaurant", rest);
                                    intent.putExtra("latitude", latitude);
                                    intent.putExtra("longitude", longitude);
                                    startActivity(intent);
                                }
                            });
                            listView.setSelection((page - 1) * pageSize);
                            list.updateResultsText(estabs.size(), total);
                            toast("Results loaded");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        toast("Failed to load results");
                        page--;
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-api-version", "2");

                return params;
            }
        };
        requestQueue.add(request);
    }

    private void toast(String text) {
        Toast.makeText(this, text,
                Toast.LENGTH_SHORT).show();
    }

    public void load(View view) {
        page++;
        loadRests();
        Log.d("Load", "loading page " + String.valueOf(page));
    }

    public void favourites(View view) {
        Intent intent = new Intent(this, Favourites.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    public void advancedSearch(View view) {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return list;
            } else if (position == 1) {
                return map;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
