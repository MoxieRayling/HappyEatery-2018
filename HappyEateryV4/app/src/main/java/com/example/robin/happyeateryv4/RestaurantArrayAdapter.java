package com.example.robin.happyeateryv4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class RestaurantArrayAdapter extends ArrayAdapter<Restaurant> {

    private Context context;
    private List<Restaurant> listItems;
    private LatLng loc;

    public RestaurantArrayAdapter(Context context, int resource, ArrayList<Restaurant> details, LatLng loc) {
        super(context, resource, details);
        this.context = context;
        this.listItems = details;
        this.loc = loc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_layout, parent, false);
            viewHolder.businessTextView = convertView.findViewById(R.id.uiBusinessTextView);
            viewHolder.typeTextView = convertView.findViewById(R.id.uiTypeTextView);
            viewHolder.distanceTextView = convertView.findViewById(R.id.uiDistanceTextView);
            viewHolder.ratingTextView = convertView.findViewById(R.id.uiRatingTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.businessTextView.setText("Name: " + listItems.get(position).getName());
        viewHolder.typeTextView.setText("Type: " + listItems.get(position).getType());
        viewHolder.distanceTextView.setText(String.format("%.2f miles away",listItems.get(position).distance(loc)));
        viewHolder.ratingTextView.setText("Rating: " + String.valueOf(listItems.get(position).getRating()));

        return convertView;
    }

    @Override
    public Restaurant getItem(int position) {
        return super.getItem(position);
    }

    private static class ViewHolder {
        TextView businessTextView;
        TextView typeTextView;
        TextView distanceTextView;
        TextView ratingTextView;
    }
}