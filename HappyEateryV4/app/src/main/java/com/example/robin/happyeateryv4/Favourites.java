package com.example.robin.happyeateryv4;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Favourites extends Activity {

    private RestaurantDatabaseHelper dbHelper;
    private ListView listView;
    private LatLng loc;
    private TextView results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Intent intent = getIntent();
        loc = new LatLng(intent.getDoubleExtra("latitude", 53), intent.getDoubleExtra("longitude", -3));
        Log.d("intent", String.valueOf(loc.latitude));
        Log.d("intent", String.valueOf(loc.longitude));
        listView = findViewById(R.id.uiFavouritesListView);
        results = findViewById(R.id.uiResultsTextView);
        dbHelper = new RestaurantDatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = new RestaurantDatabaseHelper(this);
        loadFromDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dbHelper != null)
            dbHelper.close();
    }

    private void loadFromDatabase() {
        AsyncLoad task = new AsyncLoad();
        task.execute();
    }


    private class AsyncLoad extends AsyncTask<Void, Void, ArrayList<Restaurant>> {
        @Override
        protected ArrayList<Restaurant> doInBackground(Void... voids) {
            Log.d("async", "i executed");
            return dbHelper.getAll();
        }

        @Override
        protected void onPostExecute(ArrayList<Restaurant> rests) {
            final ArrayAdapter<Restaurant> adapter = new RestaurantArrayAdapter(Favourites.this, 0, rests, loc);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Favourites.this, RestaurantActivity.class);
                    Restaurant rest = adapter.getItem(position);
                    intent.putExtra("Restaurant", rest);
                    intent.putExtra("latitude", loc.latitude);
                    intent.putExtra("longitude", loc.longitude);
                    startActivity(intent);
                }
            });
            results.setText("Showing " + rests.size() + " results from favourites");
        }
    }
}

