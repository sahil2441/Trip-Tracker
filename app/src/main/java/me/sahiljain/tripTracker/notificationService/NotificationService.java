package me.sahiljain.tripTracker.notificationService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.parse.ParsePush;

import java.util.Collection;

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
public class NotificationService extends Service {

    private Trip activeTrip;

    private Persistence persistence;

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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        return Service.START_STICKY;
    }

    private void analyzeLocation(Trip activeTrip, Location location) {

        //Left Source
        if (activeTrip.getLocationStatus().equals(LocationStatus.SOURCE) &&
                location.distanceTo(getLocation(activeTrip.getLatSource(),
                        activeTrip.getLongSource())) > 500) {
            sendNotification(((App) getApplicationContext()).getUserName() + " has left " +
                    activeTrip.getSourceName());
            activeTrip.setLocationStatus(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION);
            updateLocationStatusOfTrip(activeTrip);

        }
        //Left Destination
        else if (activeTrip.getLocationStatus().equals(LocationStatus.DESTINATION) &&
                location.distanceTo(getLocation(activeTrip.getLatDestination(),
                        activeTrip.getLongDestination())) > 500) {
            sendNotification(((App) getApplicationContext()).getUserName() + " has left " +
                    activeTrip.getDestinationName());
            activeTrip.setLocationStatus(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION);
            updateLocationStatusOfTrip(activeTrip);

        } else if (activeTrip.getLocationStatus().equals(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION)) {

            //Reached  Source
            if (location.distanceTo(getLocation(activeTrip.getLatSource(),
                    activeTrip.getLongSource())) < 500) {
                sendNotification(((App) getApplicationContext()).getUserName() + " has reached " +
                        activeTrip.getSourceName());
                activeTrip.setLocationStatus(LocationStatus.SOURCE);
                updateLocationStatusOfTrip(activeTrip);

            }
            //Reached  destination
            else if (location.distanceTo(getLocation(activeTrip.getLatDestination(),
                    activeTrip.getLongDestination())) < 500) {
                sendNotification(((App) getApplicationContext()).getUserName() + " has reached " +
                        activeTrip.getDestinationName());
                activeTrip.setLocationStatus(LocationStatus.DESTINATION);
                updateLocationStatusOfTrip(activeTrip);
            }
        }
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

        /**
         * Send Push Message
         */
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
