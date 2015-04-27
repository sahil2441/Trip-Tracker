package me.sahiljain.tripTracker.main;

import android.annotation.TargetApi;
import android.app.Application;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import com.parse.Parse;

import me.sahiljain.tripTracker.entity.Trip;

/**
 * Created by sahil on 15/2/15.
 */
public class App extends Application {

    /**
     * Create a trip variable here.
     * this variable is carried through all the activities when a new trip is being created.
     */
    private Trip trip = null;

    /**
     * Holds the user name
     */
    private String userName = null;

    /**
     * flag to indicate the whether user is at Destination
     */
    private boolean atDestination = false;

    /**
     * flag to indicate the whether user is at source
     */
    private boolean atSource = true;

    /**
     * Used in me.sahiljain.tripTracker.notificationService.NotificationService
     * in the case when the active trip is changed by the user
     */
    private Integer activeTripId;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
        trip = new Trip();
        userName = getFirstName();
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isAtDestination() {
        return atDestination;
    }

    public void setAtDestination(boolean atDestination) {
        this.atDestination = atDestination;
    }

    public boolean isAtSource() {
        return atSource;
    }

    public void setAtSource(boolean atSource) {
        this.atSource = atSource;
    }

    public Integer getActiveTripId() {
        return activeTripId;
    }

    public void setActiveTripId(Integer activeTripId) {
        this.activeTripId = activeTripId;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private String getFirstName() {
        Cursor c = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null,
                null, null, null);
        int count = c.getCount();
        String[] columnNames = c.getColumnNames();
        boolean b = c.moveToFirst();
        int position = c.getPosition();
        if (count == 1 && position == 0) {
            for (int i = 0; i < columnNames.length; i++) {
                String colName = columnNames[i];
                if (colName.equalsIgnoreCase("display_name")) {
                    return c.getString(c.getColumnIndex(colName));
                }
            }
        }
        c.close();
        return "";
    }

}
