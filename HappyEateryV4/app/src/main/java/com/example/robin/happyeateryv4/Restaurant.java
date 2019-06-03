package com.example.robin.happyeateryv4;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant implements Parcelable {

    private String name;
    private String type;
    private int id;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String postcode;
    private double longitude;
    private double latitude;
    private int rating;

    public Restaurant(String name, String type, int id,
                      String addressLine1, String addressLine2,
                      String addressLine3, String addressLine4,
                      String postcode, double longitude, double latitude,
                      int rating) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.addressLine4 = addressLine4;
        this.postcode = postcode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rating = rating;
    }

    protected Restaurant(Parcel in) {
        name = in.readString();
        type = in.readString();
        id = in.readInt();
        addressLine1 = in.readString();
        addressLine2 = in.readString();
        addressLine3 = in.readString();
        addressLine4 = in.readString();
        postcode = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        rating = in.readInt();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public String getAddressLine4() {
        return addressLine4;
    }

    public String getPostcode() {
        return postcode;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(id);
        dest.writeString(addressLine1);
        dest.writeString(addressLine2);
        dest.writeString(addressLine3);
        dest.writeString(addressLine4);
        dest.writeString(postcode);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(rating);
    }

    public double distance(LatLng loc) {

        Log.d("intent", String.valueOf(loc.latitude));
        Log.d("intent", String.valueOf(loc.longitude));
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(latitude - loc.latitude);
        double lonDistance = Math.toRadians(longitude - loc.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(loc.latitude)) * Math.cos(Math.toRadians(latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 0.621371192; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public String getAdress() {
        String address = "";
        if (!addressLine1.equals("")) {
            address += addressLine1 + "\n";
            Log.d("restaurant",addressLine1);
        }
        if (!addressLine2.equals("")) {
            address += addressLine2 + "\n";
            Log.d("restaurant",addressLine2);
        }
        if (!addressLine3.equals("")) {
            address += addressLine3 + "\n";
            Log.d("restaurant",addressLine3);
        }
        if (!addressLine4.equals("")) {
            address += addressLine4 + "\n";
            Log.d("restaurant",addressLine4);
        }
        address += postcode;
        return address;
    }
}
