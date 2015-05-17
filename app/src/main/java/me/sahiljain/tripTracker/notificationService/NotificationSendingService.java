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

import java.util.Collection;
import java.util.Date;

import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserTrip;
import me.sahiljain.tripTracker.enumeration.LocationStatus;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 14/2/15.
 * This class sends the notification to 'Parse' which in turn sends it to GCM.
 */
public class NotificationSendingService extends Service {

    private Trip activeTrip;

    private Persistence persistence;

    private SharedPreferences preferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /**
         * Acquire a reference to the system Location Manager
         */
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /**
         * Inner Class--Define a listener that responds to location updates
         */
        LocationListener locationListener = new LocationListener() {

            /**
             * Called when a new location is found by the network location provider.
             * @param location
             */

            @Override
            public void onLocationChanged(Location location) {
                activeTrip = getActiveTripFromDB();
                if (activeTrip != null) {
                    analyzeLocation(activeTrip, location);
                }
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 100, locationListener);

        return Service.START_STICKY;
    }

    private void analyzeLocation(Trip activeTrip, Location location) {
        /**
         * In every case we also add the check whether the last notification sent on the source/destination
         * was made <2 hours. If yes, don't send the notification.
         */

        //Left Source
        if (getTimeDifference(activeTrip.getSourceTimeStamp()) &&
                activeTrip.getLocationStatus().equals(LocationStatus.SOURCE) &&
                location.distanceTo(getLocation(activeTrip.getLatSource(),
                        activeTrip.getLongSource())) > 500) {
            sendNotification(((App) getApplicationContext()).getUserName() + " has left " +
                    activeTrip.getSourceName());
            activeTrip.setLocationStatus(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION);
            activeTrip.setSourceTimeStamp(new Date());
            updateLocationStatusOfTrip(activeTrip);

        }
        //Left Destination
        else if (getTimeDifference(activeTrip.getDestinationTimeStamp()) &&
                activeTrip.getLocationStatus().equals(LocationStatus.DESTINATION) &&
                location.distanceTo(getLocation(activeTrip.getLatDestination(),
                        activeTrip.getLongDestination())) > 500) {
            sendNotification(((App) getApplicationContext()).getUserName() + " has left " +
                    activeTrip.getDestinationName());
            activeTrip.setLocationStatus(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION);
            activeTrip.setDestinationTimeStamp(new Date());
            updateLocationStatusOfTrip(activeTrip);

        } else if (activeTrip.getLocationStatus().equals(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION)) {

            //Reached  Source
            if (getTimeDifference(activeTrip.getSourceTimeStamp()) &&
                    location.distanceTo(getLocation(activeTrip.getLatSource(),
                            activeTrip.getLongSource())) < 500) {
                sendNotification(((App) getApplicationContext()).getUserName() + " has reached " +
                        activeTrip.getSourceName());
                activeTrip.setLocationStatus(LocationStatus.SOURCE);
                activeTrip.setSourceTimeStamp(new Date());
                updateLocationStatusOfTrip(activeTrip);

            }
            //Reached  destination
            else if (getTimeDifference(activeTrip.getDestinationTimeStamp()) &&
                    location.distanceTo(getLocation(activeTrip.getLatDestination(),
                            activeTrip.getLongDestination())) < 500) {
                sendNotification(((App) getApplicationContext()).getUserName() + " has reached " +
                        activeTrip.getDestinationName());
                activeTrip.setLocationStatus(LocationStatus.DESTINATION);
                activeTrip.setDestinationTimeStamp(new Date());
                updateLocationStatusOfTrip(activeTrip);
            }
        }
    }

    private boolean getTimeDifference(Date sourceTimeStamp) {
        if (sourceTimeStamp != null) {
            long diff = new Date().getTime() - sourceTimeStamp.getTime();
            long diffHours = diff / (60 * 60 * 1000);
            if (diffHours > 2) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private void updateLocationStatusOfTrip(Trip activeTrip) {
        persistence = new Persistence();
        persistence.updateTrip(this, activeTrip);
    }

    private Location getLocation(Float latCoordinate, Float longCoordinate) {
        Location location = new Location(Constants.EMPTY_STRING);
        if (latCoordinate != null && longCoordinate != null) {
            location.setLatitude(Double.parseDouble(latCoordinate.toString()));
            location.setLongitude(Double.parseDouble(longCoordinate.toString()));
        }
        return location;
    }

    private Trip getActiveTripFromDB() {

        persistence = new Persistence();
        if (persistence.fetchActiveTrip(this) != null && persistence.fetchActiveTrip(this).size() > 0) {
            activeTrip = persistence.fetchActiveTrip(this).get(0); //This should ideally return a list of one size=1
        }
        return activeTrip;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendNotification(String message) {

        Collection<UserTrip> userTrips = this.getActiveTrip().getFriendList();
        ParsePush push;
        String userID;

        /**
         * Send Push Message
         */
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        userID = preferences.getString(Constants.USER_NAME, "");
        message += "#" + userID;
        if (userTrips != null && userTrips.size() > 0) {
            for (UserTrip userTrip : userTrips) {
                push = new ParsePush();
                push.setChannel("c" + userTrip.getUserID());
                push.setMessage(message);
                push.sendInBackground();
            }
        }
    }

    public Trip getActiveTrip() {
        return activeTrip;
    }

    public void setActiveTrip(Trip activeTrip) {
        this.activeTrip = activeTrip;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }
}
