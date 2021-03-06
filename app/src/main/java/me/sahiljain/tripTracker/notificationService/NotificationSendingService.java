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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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

    private String firstName;

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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);

        return Service.START_STICKY;
    }

    private void analyzeLocation(Trip activeTrip, Location location) {

        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        firstName = preferences.getString(Constants.FIRST_NAME, "");
        if (firstName != null && firstName.equals("")) {
            firstName = ((App) getApplicationContext()).getUserName();
        }
        /**
         * In every case we also add the check whether the last notification sent on the source/destination
         * was made <15 minutes . If yes, don't send the notification.
         * But for a checkpoint this time duration is just 5 minutes.
         * Also, distance radius for Source/Destination is 500 m. If user is within this radius,
         * he's considered to be at source/destination.
         * Whereas for a checkpoint this radius is 1000 m.
         */

        //Left Source
        if (getTimeDifference(activeTrip.getSourceTimeStamp()) &&
                activeTrip.getLocationStatus().equals(LocationStatus.SOURCE) &&
                location.distanceTo(getLocation(activeTrip.getLatSource(),
                        activeTrip.getLongSource())) > 500) {
            sendNotification(firstName + " has left " +
                    activeTrip.getSourceName());
            activeTrip.setLocationStatus(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION);
            activeTrip.setSourceTimeStamp(new Date());
            resetAllCheckpointsFlag(activeTrip);
            updateLocationStatusOfTrip(activeTrip);

        }
        //Left Destination
        else if (getTimeDifference(activeTrip.getDestinationTimeStamp()) &&
                activeTrip.getLocationStatus().equals(LocationStatus.DESTINATION) &&
                location.distanceTo(getLocation(activeTrip.getLatDestination(),
                        activeTrip.getLongDestination())) > 500) {
            sendNotification(firstName + " has left " +
                    activeTrip.getDestinationName());
            activeTrip.setLocationStatus(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION);
            activeTrip.setDestinationTimeStamp(new Date());
            resetAllCheckpointsFlag(activeTrip);
            updateLocationStatusOfTrip(activeTrip);

        }
        /**
         * If it's between source and destination, it can reach source, destination or
         * either of the checkpoints.
         */
        else if (activeTrip.getLocationStatus().equals(LocationStatus.BETWEEN_SOURCE_AND_DESTINATION)) {

            //Reached  Source
            if (getTimeDifference(activeTrip.getSourceTimeStamp()) &&
                    location.distanceTo(getLocation(activeTrip.getLatSource(),
                            activeTrip.getLongSource())) < 500) {
                sendNotification(firstName + " has reached " +
                        activeTrip.getSourceName());
                activeTrip.setLocationStatus(LocationStatus.SOURCE);
                activeTrip.setSourceTimeStamp(new Date());
                resetAllCheckpointsFlag(activeTrip);
                updateLocationStatusOfTrip(activeTrip);
            }
            //Reached  destination
            else if (getTimeDifference(activeTrip.getDestinationTimeStamp()) &&
                    location.distanceTo(getLocation(activeTrip.getLatDestination(),
                            activeTrip.getLongDestination())) < 500) {
                sendNotification(firstName + " has reached " +
                        activeTrip.getDestinationName());
                activeTrip.setLocationStatus(LocationStatus.DESTINATION);
                activeTrip.setDestinationTimeStamp(new Date());
                //Set all checkpoints flag ==false
                resetAllCheckpointsFlag(activeTrip);
                updateLocationStatusOfTrip(activeTrip);
            }
            /**
             * There shouldn't be anything like leaving the checkpoint. A checkpoint is only reached and
             * only reached once in the entire trip.
             * When the user reaches/leaves source/destination, the flag on all checkpoints
             * is set to false.
             */
            //Reached checkpoint1
            else if (!activeTrip.isCheckPoint1Flag() &&
                    checkLocationCoordinatesNotNull(activeTrip.getLatCheckPoint1(),
                            activeTrip.getLongCheckPoint1()) &&
                    location.distanceTo(getLocation(activeTrip.getLatCheckPoint1(),
                            activeTrip.getLongCheckPoint1())) < 1000) {
                sendNotification(firstName + " has reached " + activeTrip.getCheckPoint1Name());
                activeTrip.setCheckPoint1Flag(true);
                updateLocationStatusOfTrip(activeTrip);
            }

            //Reached checkpoint2
            else if (!activeTrip.isCheckPoint2Flag() &&
                    checkLocationCoordinatesNotNull(activeTrip.getLatCheckPoint2(), activeTrip.getLongCheckPoint2())
                    &&
                    location.distanceTo(getLocation(activeTrip.getLatCheckPoint2(),
                            activeTrip.getLongCheckPoint2())) < 1000) {
                sendNotification(firstName + " has reached " + activeTrip.getCheckPoint2Name());
                activeTrip.setCheckPoint2Flag(true);
                updateLocationStatusOfTrip(activeTrip);
            }
        }
    }

    private void resetAllCheckpointsFlag(Trip activeTrip) {
        activeTrip.setCheckPoint1Flag(false);
        activeTrip.setCheckPoint2Flag(false);
    }

    private boolean checkLocationCoordinatesNotNull(Float latitude, Float longitude) {

        if (latitude != null && longitude != null) {
            return true;
        }
        return false;
    }

    /**
     * We assume that the person will not return to the same location before 15 minutes
     *
     * @param timeStamp
     * @return
     */
    private boolean getTimeDifference(Date timeStamp) {
        if (timeStamp != null) {
            long diff = new Date().getTime() - timeStamp.getTime();
            double diffMinutes = diff / (60 * 1000);
            if (diffMinutes > 15) {
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

        Collection<String> listOfChannels = getListOfChannels();
        ParsePush push;
        String userID;
        String timeToShow = getTimeToShow();

        //Send Push Message
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        userID = preferences.getString(Constants.USER_NAME, "");
        message += "#" + userID;
        message += "$" + timeToShow;
        try {
            push = new ParsePush();
            push.setChannels(listOfChannels);
            push.setMessage(message);
            push.sendInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<String> getListOfChannels() {
        Collection<String> collectionOfChannels = new ArrayList<>();
        Trip activeTrip = getActiveTripFromDB();
        if (activeTrip != null && activeTrip.getFriendList() != null &&
                activeTrip.getFriendList().size() > 0) {
            for (UserTrip userTrip : activeTrip.getFriendList()) {
                collectionOfChannels.add("c" + userTrip.getUserID());
            }
        }
        return collectionOfChannels;
    }

    private String getTimeToShow() {
        String time = getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String timeInString = time + " " + sdf.format(new Date());
        return timeInString;
    }

    private String getTime() {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        String s;
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            s = "AM";
        } else {
            s = "PM";
        }
        String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
                + " " + s;
        return curTime;
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
