package me.sahiljain.tripTracker.notificationService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.parse.ParsePush;

import java.util.List;

import me.sahiljain.locationstat.db.DataBaseFriends;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 14/2/15.
 * This class sends the notification to 'Parse' which in turn sends it to GCM.
 */
public class NotificationService extends Service {

    private DataBaseFriends dataBaseFriends;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        final Location homeLoc = new Location("dummy");
        homeLoc.setLatitude(preferences.getFloat(Constants.HOME_LATITUDE, 0));
        homeLoc.setLongitude(preferences.getFloat(Constants.HOME_LONGITUDE, 0));

        final Location workLoc = new Location("dummy");
        workLoc.setLatitude(preferences.getFloat(Constants.WORK_LATITUDE, 0));
        workLoc.setLongitude(preferences.getFloat(Constants.WORK_LONGITUDE, 0));

        final String firstName = preferences.getString(Constants.FIRST_NAME, "");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /**
         * Inner Class
         */
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            SharedPreferences preferences = getSharedPreferences
                    (Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                /**
                 * Based on Current location and location stored (i.e. previous location)
                 * in shared preferences
                 * we determine the change in 'status' of location and denote it using a flag
                 */

                if (isAtHome(location) != preferences.getBoolean(Constants.AT_HOME, false)) {
                    editor.putBoolean(Constants.FLAG_HOME, true);
                } else {
                    editor.putBoolean(Constants.FLAG_HOME, false);
                }
                if (isAtWork(location) != preferences.getBoolean(Constants.AT_WORK, false)) {
                    editor.putBoolean(Constants.FLAG_WORK, true);
                } else {
                    editor.putBoolean(Constants.FLAG_WORK, false);
                }

                /**
                 * Now we check the current location and update in the shared preferences
                 */

                if (isAtHome(location)) {
                    editor.putBoolean(Constants.AT_HOME, true);
                } else {
                    editor.putBoolean(Constants.AT_HOME, false);
                }
                if (isAtWork(location)) {
                    editor.putBoolean(Constants.AT_WORK, true);
                } else {
                    editor.putBoolean(Constants.AT_WORK, false);
                }
                //commit the changes
                editor.apply();

                /**
                 * Send Notification based on flags
                 * Only one of them should be true
                 */
                if (preferences.getBoolean(Constants.FLAG_HOME, false)) {
                    if (preferences.getBoolean(Constants.AT_HOME, false) &&
                            preferences.getBoolean(Constants.NOTIFY_ON_REACH_HOME, true)) {
                        //reached home
                        sendNotification(firstName + " has reached home.");
                    } //left home
                    else if (!preferences.getBoolean(Constants.AT_HOME, false) &&
                            preferences.getBoolean(Constants.NOTIFY_ON_LEAVING_HOME, true)) {

                        sendNotification(firstName + " has left home.");
                    }

                }
                if (preferences.getBoolean(Constants.FLAG_WORK, false)) {
                    if (preferences.getBoolean(Constants.AT_WORK, false) &&
                            preferences.getBoolean(Constants.NOTIFY_ON_REACH_WORKPLACE, true)) {

                        //reached workplace
                        sendNotification(firstName + " has reached workplace.");
                    }
                    //left workplace
                    else if (preferences.getBoolean(Constants.AT_WORK, false) &&
                            preferences.getBoolean(Constants.NOTIFY_ON_LEAVING_WORKPLACE, true)) {

                        sendNotification(firstName + " has left workplace.");
                    }
                }
            }

            private boolean isAtWork(Location location) {
                if (location.distanceTo(workLoc) < 400) {
                    return true;
                }
                return false;
            }

            private boolean isAtHome(Location location) {
                if (location.distanceTo(homeLoc) < 400) {
                    return true;
                }
                return false;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, locationListener);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification(String message) {

        dataBaseFriends = new DataBaseFriends(this);

        /**
         * Send Push Message
         */
        List<String> listOfChannels = dataBaseFriends.fetchChannels();

        ParsePush push = new ParsePush();
        push.setChannels(listOfChannels);
        push.setMessage(message);
        push.sendInBackground();
    }

}
