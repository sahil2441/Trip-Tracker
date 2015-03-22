package me.sahiljain.locationstat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import me.sahiljain.locationstat.enitity.Trip;

/**
 * Created by sahil on 22/3/15.
 */
public class DataBaseTrips extends SQLiteOpenHelper {

    public static final String DB_NAME = "DataBaseTrips";
    /**
     * First table
     */
    public static final String TABLE_LIST_OF_TRIPS = "TABLE_LIST_OF_TRIPS";
    //Trip ID is Auto Generated
    public static final String TRIP_ID = "TRIP_ID";
    public static final String TRIP_NAME = "TRIP_NAME";
    public static final String LAT_SOURCE = "LAT_SOURCE";
    public static final String LONG_SOURCE = "LONG_SOURCE";
    public static final String LAT_DESTINATION = "LAT_DESTINATION";
    public static final String LONG_DESTINATION = "LONG_DESTINATION";

    /**
     * Second Table with One to many relationship between trip id and user id.
     */
    public static final String TABLE_TRIPS_AND_USERS = "TABLE_TRIPS_AND_USERS";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";


    /*
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public DataBaseTrips(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //TODO
    public void insert(Trip trip) {

    }

    //TODO
    public List<Trip> fetch() {
        return null;
    }
}
