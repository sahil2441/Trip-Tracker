package me.sahiljain.locationstat.addTrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.main.Constants;
import me.sahiljain.locationstat.service.GPSTracker;
import me.sahiljain.locationstat.windows.SearchResults;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripSecondWindow extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Location searchLocation;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Set up color for Action bar
        currentColor = preferences.getInt(Constants.CURRENT_COLOR, 0xFF666666);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Drawable colorDrawable = new ColorDrawable(currentColor);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable});
            getSupportActionBar().setBackgroundDrawable(layerDrawable);
        }

        this.searchLocation = null;
        /**
         * Get Location from Search bar--if applicable
         * this will replace the above location
         */
        if (!preferences.getString(Constants.SEARCH_LAT, "").equalsIgnoreCase("") &&
                !preferences.getString(Constants.SEARCH_LONG, "").equalsIgnoreCase("")) {
            this.searchLocation = new Location("dummy");
            this.searchLocation.setLatitude(Double.parseDouble(preferences.getString(Constants.SEARCH_LAT, "")));
            this.searchLocation.setLongitude(Double.parseDouble(preferences.getString(Constants.SEARCH_LONG, "")));
        }

        //Check if map view already exists
        boolean flag = true;
        View view = findViewById(R.id.map_add_a_trip_second);
        if (view != null) {
            flag = false;
        }

        //Restore map state
        if (flag) {
            try {
                setContentView(R.layout.add_a_trip_second);
                setUpMapIfNeeded();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error: " + e.toString() +
                        "Exception caught OnResume() at setContentView()");

            }

            final EditText editText = (EditText) findViewById(R.id.edit_text_maps_add_a_trip_second);
            final Intent intent = new Intent(this, SearchResults.class);
            if (editText != null) {
                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        editText.setCursorVisible(true);
                        /**
                         *Flag for SearchResults.java to distinguish whether the request came from
                         * Second Window or third
                         */
/*
                        editor.putBoolean(Constants.ADD_A_TRIP_SECOND_WINDOW,true);
                        editor.putBoolean(Constants.ADD_A_TRIP_THIRD_WINDOW,false);
                        editor.apply();
*/
                        startActivity(intent);
                        return true;
                    }
                });
            }
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.map_add_a_trip_second))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                //Move the button to bottom
                setButtonPosition();
            }
        }
    }

    private void setUpMap() {
        SharedPreferences.Editor editor = preferences.edit();

        Location location = new Location("dummy");
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation() == true) {
            location = gpsTracker.getLocation();
        }
        /**
         * Check to see if search was triggered from Search Results class or not
         * if yes then search location wont be null
         */
        if (this.searchLocation != null) {
            location = searchLocation;
            editor.putString(Constants.SEARCH_LAT, "");
            editor.putString(Constants.SEARCH_LONG, "");
            editor.apply();
            //TODO: Put a balloon on map in this case
        }

        if (location != null) {
            centerMapOnMYLocation(location);
        }
    }

    public void centerMapOnMYLocation(Location location) {
        float zoom = 16;
        mMap.setMyLocationEnabled(true);
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
    }

    private void setButtonPosition() {
        /**
         * Move the button
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map_add_a_trip_second);
        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 100);
        }
    }
}
