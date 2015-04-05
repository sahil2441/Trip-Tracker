package me.sahiljain.locationstat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.tripTracker.entity.UserTrip;

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
    public static final String ACTIVE = "ACTIVE";
    public static final String ONE_TIME_TRIP = "ONE_TIME_TRIP";
    public static final String TO_FRO = "TO_FRO";
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";


    /**
     * Second Table with One to many relationship between trip id and user id.
     */
    public static final String TABLE_TRIPS_AND_USERS = "TABLE_TRIPS_AND_USERS";
    public static final String TRIP_ID_ = "TRIP_ID_";
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
        db.execSQL("CREATE TABLE " + TABLE_LIST_OF_TRIPS + " (" + TRIP_ID + " TEXT PRIMARY KEY, " +
                        TRIP_NAME + " TEXT, " +
                        LAT_SOURCE + " REAL, " +
                        LONG_SOURCE + " REAL, " +
                        LAT_DESTINATION + " REAL, " +
                        LONG_DESTINATION + " REAL, " +
                        ONE_TIME_TRIP + " TEXT, " +
                        TO_FRO + " TEXT, " +
                        ACTIVE + " TEXT );"
        );

        db.execSQL("CREATE TABLE " + TABLE_TRIPS_AND_USERS + " ( " +
                        TRIP_ID_ + " TEXT, " +
                        USER_ID + " TEXT, " +
                        USER_NAME + " TEXT, " +
                        "PRIMARY KEY (TRIP_ID_,USER_ID));"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_OF_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS_AND_USERS);
        onCreate(db);


    }

    /*
        public boolean insert(Trip trip) {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValuesTrip = new ContentValues();
            contentValuesTrip.put(TRIP_ID, trip.getId());
            contentValuesTrip.put(TRIP_NAME, trip.getTripName());
            contentValuesTrip.put(LAT_SOURCE, trip.getLatSource());
            contentValuesTrip.put(LONG_SOURCE, trip.getLongSource());
            contentValuesTrip.put(LAT_DESTINATION, trip.getLatDestination());
            contentValuesTrip.put(LONG_DESTINATION, trip.getLongDestination());
            contentValuesTrip.put(ACTIVE, trip.getActive().toString());
            contentValuesTrip.put(ONE_TIME_TRIP, trip.getOneTimeTrip().toString());
            contentValuesTrip.put(TO_FRO, trip.getToAndFro().toString());
            contentValuesTrip.put(MONDAY, trip.getWeekDetails().getMonday().toString());
            contentValuesTrip.put(TUESDAY, trip.getWeekDetails().getTuesday().toString());
            contentValuesTrip.put(WEDNESDAY, trip.getWeekDetails().getWednesday().toString());
            contentValuesTrip.put(THURSDAY, trip.getWeekDetails().getThursday().toString());
            contentValuesTrip.put(FRIDAY, trip.getWeekDetails().getFriday().toString());
            contentValuesTrip.put(SATURDAY, trip.getWeekDetails().getSaturday().toString());
            contentValuesTrip.put(SUNDAY, trip.getWeekDetails().getSunday().toString());
            db.insert(TABLE_LIST_OF_TRIPS, null, contentValuesTrip);

            for (int i = 0; i < trip.getFriendList().size(); i++) {
                ContentValues contentValuesUser = new ContentValues();
                contentValuesUser.put(TRIP_ID_, trip.getId());
    //            contentValuesUser.put(USER_ID, trip.getFriendList().get(i).getUserID());
    //            contentValuesUser.put(USER_NAME, trip.getFriendList().get(i).getName());
                db.insert(TABLE_TRIPS_AND_USERS, null, contentValuesUser);
            }
            db.close();
            return true;
        }

    */
/*
    public List<Trip> fetch() {
        SQLiteDatabase database = this.getReadableDatabase();

        //fetch list of trips
        List<Trip> trips = new ArrayList<Trip>();
        String queryTrip = "SELECT "+ TRIP_ID+" FROM " + TABLE_LIST_OF_TRIPS;
        Cursor cursorTrips = database.rawQuery(queryTrip, null);
        if (cursorTrips.moveToFirst()) {
            do {
                Trip trip = new Trip();
                trip.setActive(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(ACTIVE))));
                trip.setOneTimeTrip(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(ONE_TIME_TRIP))));
                trip.setId(cursorTrips.getString(cursorTrips.getColumnIndex(TRIP_ID)));
                trip.setTripName(cursorTrips.getString(cursorTrips.getColumnIndex(TRIP_NAME)));
                trip.setToAndFro(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(TO_FRO))));

                //source
                trip.setLatSource(cursorTrips.getFloat(cursorTrips.getColumnIndex(LAT_SOURCE)));
                trip.setLongSource(cursorTrips.getFloat(cursorTrips.getColumnIndex(LONG_SOURCE)));

                //destination
                trip.setLatDestination(cursorTrips.getFloat(cursorTrips.getColumnIndex(LAT_DESTINATION)));
                trip.setLongDestination(cursorTrips.getFloat(cursorTrips.getColumnIndex(LONG_DESTINATION)));

                //week
                trip.setWeekDetails(new Week());
                trip.getWeekDetails().setMonday(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(MONDAY))));
                trip.getWeekDetails().setTuesday(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(TUESDAY))));
                trip.getWeekDetails().setWednesday(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(WEDNESDAY))));
                trip.getWeekDetails().setThursday(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(THURSDAY))));
                trip.getWeekDetails().setFriday(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(FRIDAY))));
                trip.getWeekDetails().setSaturday(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(SATURDAY))));
                trip.getWeekDetails().setSunday(Boolean.valueOf(cursorTrips.getString(cursorTrips.getColumnIndex(SUNDAY))));

                //Friend list
//                trip.setFriendList(getFriendList(trip.getId()));

                trips.add(trip);
            } while (cursorTrips.moveToNext());
        }

        return trips;
    }

*/
    private List<UserTrip> getFriendList(String tripId) {

        SQLiteDatabase database = this.getReadableDatabase();
        List<UserTrip> userTrips = new ArrayList<>();
        String queryUsers = "SELECT * FROM " + TABLE_TRIPS_AND_USERS + " WHERE " +
                TRIP_ID_ + " = " + tripId;
        Cursor cursorUsers = database.rawQuery(queryUsers, null);
        if (cursorUsers.moveToFirst()) {
            do {
                UserTrip userTrip = new UserTrip();
                userTrip.setName(cursorUsers.getString(cursorUsers.getColumnIndex(USER_NAME)));
                userTrip.setUserID(cursorUsers.getString(cursorUsers.getColumnIndex(USER_ID)));
                userTrips.add(userTrip);
            }
            while (cursorUsers.moveToNext());
        }
        return userTrips;
    }
}
