package com.example.robin.happyeateryv4;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResultsList extends Fragment
{
    private TextView results;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.results_list,container,false);
        results = rootView.findViewById(R.id.uiResultsTextView);
        return rootView;
    }

    public void updateResultsText(int loaded, int total){
        results.setText("Showing " + loaded + " out of " + total + " results");
    }
}