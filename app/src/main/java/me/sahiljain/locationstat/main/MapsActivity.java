package me.sahiljain.locationstat.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import me.sahiljain.tripTracker.R;
import me.sahiljain.locationstat.windows.Preferences;
import me.sahiljain.locationstat.windows.SearchResults;
import me.sahiljain.locationstat.windows.StartJourney;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.main.IntroActivity;
import me.sahiljain.tripTracker.notificationService.NotificationSendingService;
import me.sahiljain.tripTracker.service.GPSTracker;

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMapClickListener {

    boolean set_up_home_location = false;
    boolean set_up_work_location = false;
    private Location searchLocation;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences
                (Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);

        if (preferences.getBoolean(Constants.LOGIN_STATUS, false) == false) {
            Intent welcomeSignUpWindowIntent = new Intent(this, IntroActivity.class);
            startActivity(welcomeSignUpWindowIntent);
            finish();

        } else {
            String userID = preferences.getString(Constants.USER_NAME, "");
            try {
                ParseUser.logInInBackground(userID, Constants.PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i(Constants.TAG, "The device is not supported");
            }
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        /**
         * Get first name of user
         */
        if (!preferences.getBoolean(Constants.FIRST_NAME_FLAG, false)) {
            String name = getFirstName();
            if (name != null && name != "") {
                editor.putString(Constants.FIRST_NAME, name);
                editor.putBoolean(Constants.FIRST_NAME_FLAG, true);
            } else {
                editor.putBoolean(Constants.FIRST_NAME_FLAG, false);
            }
            editor.apply();
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
        // Check device for Play Services APK.
        checkPlayServices();

        //Check if view already exists
        boolean flag = true;
        View view = findViewById(R.id.map);
        if (view != null) {
            flag = false;
        }

        //Restore map state
        if (preferences.getBoolean(Constants.LOGIN_STATUS, false) && flag) {
            try {
                setContentView(R.layout.activity_maps);
                setUpMapIfNeeded();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error: " + e.toString() +
                        "Exception caught OnResume() at setContentView()");

            }

            final EditText editText = (EditText) findViewById(R.id.edit_text_maps);
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
            //Start Service
            Intent notificationServiceIntent = new Intent(getApplicationContext(), NotificationSendingService.class);
            getApplicationContext().startService(notificationServiceIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private String getFirstName() {
        Cursor c = getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null,
                null, null, null);
        int count = c.getCount();
        String[] columnNames = c.getColumnNames();
        boolean b = c.moveToFirst();
        int position = c.getPosition();
        if (count == 1 && position == 0) {
            for (int i = 0; i < columnNames.length; i++) {
                String colName = columnNames[i];
                if (colName.equalsIgnoreCase("display_name")) {
                    return c.getString(c.getColumnIndex(colName));
                }
            }
        }
        c.close();
        return "";
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                //Move the button to bottom
                setButtonPosition();
            }
        }
    }

    private void setButtonPosition() {
        /**
         * Move the button
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
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
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    private void setUpMap() {
        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
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

    @Override
    public void onMapClick(LatLng latLng) {
        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (set_up_home_location == true) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Home").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.source_icon_small)));
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            editor.putFloat(Constants.HOME_LATITUDE, (float) latitude);
            editor.putFloat(Constants.HOME_LONGITUDE, (float) longitude);
            set_up_home_location = false;

            //Show Toast
            Toast toast = Toast.makeText(this, "Home Location Saved", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (set_up_work_location == true) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Work").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_icon_small)));
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            editor.putFloat(Constants.WORK_LATITUDE, (float) latitude);
            editor.putFloat(Constants.WORK_LONGITUDE, (float) longitude);
            set_up_work_location = false;

            //Show Toast
            Toast toast = Toast.makeText(this, "Work Location Saved", Toast.LENGTH_SHORT);
            toast.show();
        }
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.maps_activity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Total 4 options in the action Bar
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        if (item.getItemId() == R.id.action_notifications) {
            openNotificationsWindow();
        } else if (item.getItemId() == R.id.preferences) {
            openPreferencesWindow();
        } else if (item.getItemId() == R.id.start_a_journey) {
            openStartJourneyWindow();
        } else if (item.getItemId() == R.id.set_up_home_loc) {
            openSetUpHomeLoc();
        } else if (item.getItemId() == R.id.set_up_work_loc) {
            openSetUpWorkLoc();
        }
*/
        return super.onOptionsItemSelected(item);
    }

    private void openStartJourneyWindow() {
        Intent startJourneyIntent = new Intent(this, StartJourney.class);
        this.startActivity(startJourneyIntent);
    }

    private void openPreferencesWindow() {
        Intent preferencesIntent = new Intent(this, Preferences.class);
        this.startActivity(preferencesIntent);
    }

/*
    private void openNotificationsWindow() {
        Intent notificationsIntent = new Intent(this, Notification.class);
        this.startActivity(notificationsIntent);
    }
*/

    private void openSetUpWorkLoc() {
        set_up_work_location = true;
        set_up_home_location = false;
        mMap.clear();

        //edit
        Toast toast = Toast.makeText(this, Constants.TAP_ANYWHERE_NOTIFICATION, Toast.LENGTH_LONG);
        toast.show();
        mMap.setOnMapClickListener(this);
/*
        // Create an instance of the dialog and show it
        DialogFragment dialog = new UseCurrentLocationDialog();
        dialog.show(getSupportFragmentManager(), String.valueOf(UseCurrentLocationDialog.class));
*/
    }

    private void openSetUpHomeLoc() {
        set_up_home_location = true;
        set_up_work_location = false;
        mMap.clear();
        //edit
        Toast toast = Toast.makeText(this, Constants.TAP_ANYWHERE_NOTIFICATION, Toast.LENGTH_LONG);
        toast.show();
        mMap.setOnMapClickListener(this);


/*
        // Create an instance of the dialog and show it
        DialogFragment dialog = new UseCurrentLocationDialog();
        dialog.show(getSupportFragmentManager(), String.valueOf(UseCurrentLocationDialog.class));
*/
    }

    /**
     * Two methods implemented from the
     * 'UseCurrentLocationDialogue' class
     *
     * @param dialogFragment
     */

/*
    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Location location = null;
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation() == true) {
            location = gpsTracker.getLocation();
        }
        if ((this.set_up_home_location) && (location != null)) {
            editor.putFloat(Constants.HOME_LATITUDE, (float) location.getLatitude());
            editor.putFloat(Constants.HOME_LONGITUDE, (float) location.getLongitude());
            Toast toast = Toast.makeText(this, "Current Location Saved as Home Location", Toast.LENGTH_SHORT);
            toast.show();
        } else if ((this.set_up_work_location) && (location != null)) {
            editor.putFloat(Constants.WORK_LATITUDE, (float) location.getLatitude());
            editor.putFloat(Constants.WORK_LONGITUDE, (float) location.getLongitude());
            Toast toast = Toast.makeText(this, "Current Location Saved as Work Location", Toast.LENGTH_SHORT);
            toast.show();
        }
        editor.commit();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        Toast toast = Toast.makeText(this, Constants.TAP_ANYWHERE_NOTIFICATION, Toast.LENGTH_LONG);
        toast.show();
        mMap.setOnMapClickListener(this);
    }
*/
}
