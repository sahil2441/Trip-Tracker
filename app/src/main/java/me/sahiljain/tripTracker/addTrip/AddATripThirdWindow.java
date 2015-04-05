package me.sahiljain.tripTracker.addTrip;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.windows.SearchResults;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.service.GPSTracker;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripThirdWindow extends ActionBarActivity implements GoogleMap.OnMapClickListener {
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Location searchLocation;
    private int currentColor;

    //Global instance of Trip
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();

        trip = ((App) getApplication()).getTrip();
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
        View view = findViewById(R.id.map_add_a_trip_third);
        if (view != null) {
            flag = false;
        }

        //Restore map state
        if (flag) {
            try {
                setContentView(R.layout.add_a_trip_third);
                setUpMapIfNeeded();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error: " + e.toString() +
                        "Exception caught OnResume() at setContentView()");

            }
        }
        /**
         * Put a flag for destination location saved earlier--if available in shared preferences
         * and zoom map on it
         */
        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }
        if (trip.getLatDestination() != null && trip.getLongDestination() != null) {
            Float lat = trip.getLatDestination();
            Float lon = trip.getLongDestination();
            if (lat != 0 && lon != 0) {
                LatLng latLng = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(latLng).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon_small)));
                Location location = new Location("dummy");
                location.setLatitude(lat);
                location.setLongitude(lon);
                centerMapOnMYLocation(location);
            }
        }
        /**
         * Code for Dynamic Search in edit text
         * TODO
         */

        final EditText editText = (EditText) findViewById(R.id.edit_text_maps_add_a_trip_third);
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

        final Button previousButton = (Button) findViewById(R.id.previous_button_add_a_trip_third);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateUpToSecondWindow();
                finish();
            }
        });

        final Intent intentAddATripFourthWindow = new Intent(this, AddATripFourthWindow.class);
        Button nextButton = (Button) findViewById(R.id.next_button_add_a_trip_third);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentAddATripFourthWindow);

            }
        });

        Button setDestinationTripButton = (Button) findViewById(R.id.set_destination_location_button);
        setDestinationTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this).setTitle(Constants.SET_DESTINATION_LOCATION)
                .setMessage(Constants.DESTINATION_DIALOG_MESSAGE)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activateSetOnMapClickListener();
                    }
                })
                .show();
    }

    private void activateSetOnMapClickListener() {
        mMap.setOnMapClickListener(this);
    }

    @TargetApi(16)
    private void onNavigateUpToSecondWindow() {
        onNavigateUp();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().
                    findFragmentById(R.id.map_add_a_trip_third))
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
        //TODO: Button not visible
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

    @Override
    public void onMapClick(LatLng latLng) {

        saveDestinationCoordinates(latLng);
        Toast toast = Toast.makeText(this, Constants.DESTINATION_LOCATION_SAVED, Toast.LENGTH_SHORT);
        toast.show();
        mMap.addMarker(new MarkerOptions().position(latLng).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon_small)));
    }

    private void saveDestinationCoordinates(LatLng latLng) {
        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }
        trip.setLatDestination((float) latLng.latitude);
        trip.setLongDestination((float) latLng.longitude);
        ((App) getApplication()).setTrip(trip);
    }
}
