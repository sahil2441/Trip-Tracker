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
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import me.sahiljain.locationstat.R;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.service.GPSTracker;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripSecondWindow extends ActionBarActivity implements GoogleMap.OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Location searchLocation;
    private int currentColor;

    //Global instance of Trip from Application class
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

        //TODO:Search feature
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
        }
        /**
         * Put a flag for source location saved earlier--if available in shared preferences
         * and zoom map on it
         */
        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }
        if (trip.getLatSource() != null && trip.getLongSource() != null) {
            Float lat = trip.getLatSource();
            Float lon = trip.getLongSource();
            //Assuming source was not on (0,0)
            if (lat != 0 && lon != 0) {
                LatLng latLng = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(latLng).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.source_icon_small)));
                Location location = new Location("dummy");
                location.setLatitude(lat);
                location.setLongitude(lon);
                centerMapOnMYLocation(location);
            }

        }

        //TODO: Search Functionality
/*
        final EditText editText = (EditText) findViewById(R.id.edit_text_maps_add_a_trip_second);
        final Intent intent = new Intent(this, SearchResults.class);
        if (editText != null) {
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    editText.setCursorVisible(true);

                    startActivity(intent);
                    return true;
                }
            });
        }
*/

        final Button previousButton = (Button) findViewById(R.id.previous_button_add_a_trip_second);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateUpToFirstWindow();
                finish();
            }
        });

        final Intent intentAddATripThirdWindow = new Intent(this, AddATripThirdWindow.class);
        Button nextButton = (Button) findViewById(R.id.next_button_add_a_trip_second);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentAddATripThirdWindow);

            }
        });

        Button setSourceTripButton = (Button) findViewById(R.id.set_source_location_button);
        setSourceTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(this).setTitle(Constants.SET_SOURCE_LOCATION)
                .setMessage(Constants.SOURCE_DIALOG_MESSAGE)
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
    private void onNavigateUpToFirstWindow() {
        onNavigateUp();
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
//                setButtonPosition();
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

    @Override
    public void onMapClick(LatLng latLng) {

        //Save Source coordinates
        saveSourceCoordinates(latLng);
        Toast toast = Toast.makeText(this, Constants.SOURCE_LOCATION_SAVED, Toast.LENGTH_SHORT);
        toast.show();
        mMap.addMarker(new MarkerOptions().position(latLng).
                icon(BitmapDescriptorFactory.fromResource(R.drawable.source_icon_small)));
    }

    private void saveSourceCoordinates(LatLng latLng) {
        trip = ((App) getApplication()).getTrip();
        if (trip != null) {
            trip.setLatSource((float) latLng.latitude);
            trip.setLongSource((float) latLng.longitude);
        }
        ((App) getApplication()).setTrip(trip);
    }
}
