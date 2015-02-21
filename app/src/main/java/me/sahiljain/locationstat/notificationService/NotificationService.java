package me.sahiljain.locationstat.notificationService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import me.sahiljain.locationstat.main.Constants;

/**
 * Created by sahil on 14/2/15.
 * This class sends the notification to 'Parse' which in turn sends it to GCM.
 */
public class NotificationService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
//                sendNotification();

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
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification() {
        /**
         * Send Push Message
         */
        ParseQuery pushQuery = ParseInstallation.getQuery();
        SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        String username = preferences.getString("userID", "");

        pushQuery.whereEqualTo("channels", "c" + username);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.NOTIFICATIONS_SHARED_PREFERENCES, MODE_PRIVATE);
        int n = sharedPreferences.getInt(Constants.NOTIFICATIONS_SIZE, 0);
        String message = "Hi!" + n;

        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(message);
        push.sendInBackground();
    }

}
