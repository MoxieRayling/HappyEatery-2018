package com.example.robin.happyeateryv4;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class RestaurantDatabaseHelper extends SQLiteOpenHelper {

    private Resources res;

    public static final String DATABASE_NAME = "HappyEateryFavourites.db";
    private  String FAVOURITES;
    private String NAME;
    private String TYPE;
    private String ID;
    private String ADDRESSS_LINE_1;
    private String ADDRESSS_LINE_2;
    private String ADDRESSS_LINE_3;
    private String ADDRESSS_LINE_4;
    private String POSTCODE;
    private String LONGITUDE;
    private String LATITUDE;
    private String RATING;
    public static final int DATABASE_VERSION = 1;

    public RestaurantDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        res = context.getResources();
        FAVOURITES = res.getString(R.string.db_favourites);
        NAME = res.getString(R.string.db_name);
        TYPE = res.getString(R.string.db_type);
        ID = res.getString(R.string.db_id);
        ADDRESSS_LINE_1 = res.getString(R.string.db_address_line_1);
        ADDRESSS_LINE_2 = res.getString(R.string.db_address_line_2);
        ADDRESSS_LINE_3 = res.getString(R.string.db_address_line_3);
        ADDRESSS_LINE_4 = res.getString(R.string.db_address_line_4);
        POSTCODE = res.getString(R.string.db_postcode);
        LONGITUDE = res.getString(R.string.db_logitude);
        LATITUDE = res.getString(R.string.db_latitude);
        RATING = res.getString(R.string.db_rating);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE FAVOURITES ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "TYPE TEXT, "
                + "ID INTEGER, "
                + "ADDRESSLINE1 TEXT, "
                + "ADDRESSLINE2 TEXT, "
                + "ADDRESSLINE3 TEXT, "
                + "ADDRESSLINE4 TEXT, "
                + "POSTCODE TEXT, "
                + "LONGITUDE REAL, "
                + "LATITUDE REAL, "
                + "RATING INTEGER"
                + ")";
        sqLiteDatabase.execSQL(sql);/*
        for(int i = 0; i< 5; i++ ){
            Restaurant rest = new Restaurant("name","type",4,
                    "address1","address2",
                    "address3","address4",
                    "postcode",53,-3,5,3);
            insertRestaurant(rest);
        }*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS FAVOURITES");
        onCreate(sqLiteDatabase);
    }

    public boolean insertRestaurant(Restaurant rest) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues restaurantValues = new ContentValues();
        restaurantValues.put(NAME, rest.getName());
        restaurantValues.put(TYPE, rest.getType());
        restaurantValues.put(ID, rest.getId());
        restaurantValues.put(ADDRESSS_LINE_1, rest.getAddressLine1());
        restaurantValues.put(ADDRESSS_LINE_2, rest.getAddressLine2());
        restaurantValues.put(ADDRESSS_LINE_3, rest.getAddressLine3());
        restaurantValues.put(ADDRESSS_LINE_4, rest.getAddressLine4());
        restaurantValues.put(POSTCODE, rest.getPostcode());
        restaurantValues.put(LONGITUDE, rest.getLongitude());
        restaurantValues.put(LATITUDE, rest.getLatitude());
        restaurantValues.put(RATING, rest.getRating());
        return db.insert(FAVOURITES, null, restaurantValues) != -1;
    }

    public Restaurant getRestById(String id) {
        //TODO: What happens if it doesn't exist?
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FAVOURITES, new String[]{
                        NAME, TYPE, ID, ADDRESSS_LINE_1, ADDRESSS_LINE_2
                        ,ADDRESSS_LINE_3, ADDRESSS_LINE_4, POSTCODE, LONGITUDE,
                        LATITUDE, RATING},
                ID + " = ?", new String[]{id}, null, null, null);
        Restaurant rest = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                rest = new Restaurant(cursor.getString(0)
                        ,cursor.getString(1)
                        ,Integer.parseInt(cursor.getString(2))
                        ,cursor.getString(3)
                        ,cursor.getString(4)
                        ,cursor.getString(5)
                        ,cursor.getString(6)
                        ,cursor.getString(7)
                        ,Double.parseDouble(cursor.getString(8))
                        ,Double.parseDouble(cursor.getString(9))
                        ,Integer.parseInt(cursor.getString(10)));
            }
            cursor.close();
            db.close();
        }
        return rest;
    }

    public ArrayList<Restaurant> getAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FAVOURITES, new String[]{
                        NAME, TYPE, ID, ADDRESSS_LINE_1, ADDRESSS_LINE_2
                        ,ADDRESSS_LINE_3, ADDRESSS_LINE_4, POSTCODE, LONGITUDE,
                        LATITUDE, RATING},
                null, null, null, null, null);
        ArrayList<Restaurant> rests = new ArrayList<Restaurant>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    rests.add(new Restaurant(cursor.getString(0)
                            , cursor.getString(1)
                            , Integer.parseInt(cursor.getString(2))
                            , cursor.getString(3)
                            , cursor.getString(4)
                            , cursor.getString(5)
                            , cursor.getString(6)
                            , cursor.getString(7)
                            , Double.parseDouble(cursor.getString(8))
                            , Double.parseDouble(cursor.getString(9))
                            , Integer.parseInt(cursor.getString(10))));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return rests;
    }

    public void deleteRest(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FAVOURITES, ID + " = ?", new String[]{id});
    }

}
