package me.sahiljain.tripTracker.addTrip;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
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
import com.google.android.gms.maps.model.PolylineOptions;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.menu.HelpActivity;
import me.sahiljain.tripTracker.service.GPSTracker;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripSourceWindow extends AppCompatActivity implements GoogleMap.OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Location searchLocation;
    private int currentColor;
    private Button previousButton;
    private Button nextButton;
    private Button setSourceTripButton;
    private Button helpButton;

    //Global instance of Trip from Application class
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
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
                setContentView(R.layout.add_a_trip_source);
                setUpMapIfNeeded();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error: " + e.toString() +
                        "Exception caught OnResume() at setContentView()");

            }
        }
        //Draw default balloons
        drawDefaultBalloonsOnMap();

        previousButton = (Button) findViewById(R.id.previous_button_add_a_trip_second_cp);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateUpToFirstWindow();
                finish();
            }
        });

        nextButton = (Button) findViewById(R.id.next_button_add_a_trip_second);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogueOnClickOfNext();
            }
        });

        setSourceTripButton = (Button) findViewById(R.id.set_source_location_button);
        setSourceTripButton.startAnimation(getAnimation());
        setSourceTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
                v.clearAnimation();
            }
        });

        final Intent helpActivity = new Intent(this, HelpActivity.class);
        helpButton = (Button) findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(helpActivity);
            }
        });

        //disable animation if source location has been set
        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }
        if (trip.getLatSource() != null && trip.getLongSource() != null) {
            Animation animation = new AlphaAnimation(1, 1);
            setSourceTripButton.startAnimation(animation);
        }
    }

    /**
     * Animation for Location Button
     *
     * @return
     */
    private Animation getAnimation() {
        Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back i
        return animation;
    }

    private void drawDefaultBalloonsOnMap() {
        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }
        LatLng latLngSource = null;
        LatLng latLngCheckPoint1 = null;
        LatLng latLngCheckPoint2 = null;
        LatLng latLngDestination = null;
        if (trip.getLatDestination() != null && trip.getLongDestination() != null) {
            Float lat = trip.getLatDestination();
            Float lon = trip.getLongDestination();
            if (lat != 0 && lon != 0) {
                latLngDestination = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(latLngDestination).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.source_icon_small)));
                Location location = new Location("dummy");
                location.setLatitude(lat);
                location.setLongitude(lon);
            }
        }
        if (trip.getLatCheckPoint1() != null && trip.getLongCheckPoint1() != null) {
            Float lat = trip.getLatCheckPoint1();
            Float lon = trip.getLongCheckPoint1();
            //Assuming source was not on (0,0)
            if (lat != 0 && lon != 0) {
                latLngCheckPoint1 = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(latLngCheckPoint1).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.source_icon_small)));
                Location location = new Location("dummy");
                location.setLatitude(lat);
                location.setLongitude(lon);
            }
        }
        if (trip.getLatCheckPoint2() != null && trip.getLongCheckPoint2() != null) {
            Float lat = trip.getLatCheckPoint2();
            Float lon = trip.getLongCheckPoint2();
            //Assuming source was not on (0,0)
            if (lat != 0 && lon != 0) {
                latLngCheckPoint2 = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(latLngCheckPoint2).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.source_icon_small)));
                Location location = new Location("dummy");
                location.setLatitude(lat);
                location.setLongitude(lon);
            }
        }
        if (trip.getLatSource() != null && trip.getLongSource() != null) {
            Float lat = trip.getLatSource();
            Float lon = trip.getLongSource();
            //Assuming source was not on (0,0)
            if (lat != 0 && lon != 0) {
                latLngSource = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(latLngSource).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.source_icon_small)));
                Location location = new Location("dummy");
                location.setLatitude(lat);
                location.setLongitude(lon);
                centerMapOnMYLocation(location);
            }
        }
        drawLineOnMap(latLngSource, latLngCheckPoint1, latLngCheckPoint2, latLngDestination);
    }

    private void drawLineOnMap(LatLng latLngSource, LatLng latLngCheckPoint1, LatLng latLngCheckPoint2, LatLng latLngDestination) {
        try {
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            if (latLngSource != null) {
                options.add(latLngSource);
            }
            if (latLngCheckPoint1 != null) {
                options.add(latLngCheckPoint1);
            }
            if (latLngCheckPoint2 != null) {
                options.add(latLngCheckPoint2);
            }
            if (latLngDestination != null) {
                options.add(latLngDestination);
            }
            mMap.addPolyline(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialogueOnClickOfNext() {
        final EditText input = new EditText(this);
        final Intent intentAddATripFirstCPWindow = new Intent(this, AddATripFirstCheckPointWindow.class);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("e.g. Home");
        new AlertDialog.Builder(this).setTitle(Constants.ENTER_SOURCE_NAME_TITLE)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((App) getApplicationContext()).getTrip().setSourceName(input.getText().
                                toString());
                        startActivity(intentAddATripFirstCPWindow);

                    }
                })
                .show();
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

    /**
     * This method activates the on map click listener,
     * clears the map, and draws all default balloons.
     */
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
        if (gpsTracker.canGetLocation()) {
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
        float zoom = getZoomLevel();
        mMap.setMyLocationEnabled(true);
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
    }

    /**
     * TODO
     * If first check point location set then return zoom level based on logic,
     * else return 12.
     *
     * @return zoom
     */
    private float getZoomLevel() {
        float zoom = 12;
        return zoom;
    }

    private float getDistance(LatLng latLngSource, LatLng latLngCheckPoint1) {
        return 0;
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
        Toast toast = Toast.makeText(this, Constants.SOURCE_LOCATION_SAVED, Toast.LENGTH_LONG);
        toast.show();
        mMap.clear();
        drawDefaultBalloonsOnMap();
        deActivateSetOnMapClickListener();
    }

    private void deActivateSetOnMapClickListener() {
        //To disable a listener simple set it to null since it will only be called if it's not null.
        mMap.setOnMapClickListener(null);
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
