package me.sahiljain.tripTracker.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by sahil on 31/1/15.
 */
public final class GPSTracker implements LocationListener {

    Context mContext;

    boolean isGPSEnabled;

    boolean isNetworkEnabled;

    boolean canGetLocation = false;

    Location location;

    double latitude;

    double longitude;

    LocationManager locationManager;

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * Method to stop using GPS
     *
     * @param
     */

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public GPSTracker(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    /**
     * Function to get user's current Location
     *
     * @return
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            /**
             * Get GPS status
             */
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            /**
             * Get Network Status
             */
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isNetworkEnabled == false && isGPSEnabled == false) {
                //Nothing can be done
            } else {
                this.canGetLocation = true;
                location = null;
                if (isGPSEnabled) {
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (isNetworkEnabled && location == null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            /**
                             * Code jumps out of try block if location is null here
                             * therefore network location is called right here.
                             */
                            if (location != null) {
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                            }
                        }
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                }
                if (isNetworkEnabled) {
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return location;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

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
}
