package me.sahiljain.locationstat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMapClickListener {

    boolean set_up_home_location = false;
    boolean set_up_work_location = false;
    private Location location_home;

    public Location getLocation_work() {
        return location_work;
    }

    public void setLocation_work(Location location_work) {
        this.location_work = location_work;
    }

    private Location location_work;

    Dialog dialog;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    /**
     * Tag used on log messages.
     */
    static final String TAG = "Location Stat";


    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public Location getLocation_home() {
        return location_home;
    }

    public void setLocation_home(Location location_home) {
        this.location_home = location_home;
    }

    private final String LOCATION_STAT_SHARED_PREFERNCES = "locationStatSharedPreferences";

    private final String PASSWORD = "mypass";

    private final String LOGIN_STATUS = "loginStatus";

    private final String NO_OF_INSTANCES_OF_MAIN_ACTIVITY = "no_of_instances_of_main_activity";


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);

        if (preferences.getBoolean(LOGIN_STATUS, false) == false) {
            Intent welcomeSignUpWindowIntent = new Intent(this, WelcomeSignUpWindow.class);
            startActivity(welcomeSignUpWindowIntent);

        } else {
            String userID = preferences.getString("userID", "");
            ParseUser.logInInBackground(userID, PASSWORD);
        }

        Location location = getLocationFromSharedPreferences("location_home", 0);
        setLocation_home(location);

        location = getLocationFromSharedPreferences("location_work", 1);
        setLocation_work(location);
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
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            } else {
                Log.i(TAG, "The device is not supported");
            }
            return false;
        }
        return true;
    }

    private Location getLocationFromSharedPreferences(String key, int i) {

        SharedPreferences sharedPreferences = getSharedPreferences(key, i);
        double latitude = (double) sharedPreferences.getFloat("location_home_latitude", 0);
        double longitude = (double) sharedPreferences.getFloat("location_home_longitude", 0);

        Location location = new Location("dummy");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        int instances = preferences.getInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, 0);
        // Check device for Play Services APK.
        checkPlayServices();
//        Restore map state
        if (preferences.getBoolean(LOGIN_STATUS, false) == true) {
            try {
                setContentView(R.layout.activity_maps);
                setUpMapIfNeeded();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, instances + 1);
                editor.commit();
            } catch (Exception e) {
                Log.d(TAG, "Exception caught OnResume() at setcontentView()");

            }

            Intent intent = new Intent(getApplicationContext(), NotificationService.class);
            getApplicationContext().startService(intent);
        }
    }

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);

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
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences location_home = getSharedPreferences("location_home", 0);
        SharedPreferences.Editor editor = location_home.edit();
        editor.putFloat("location_home_latitude", (float) getLocation_home().getLatitude());
        editor.putFloat("location_home_longitude", (float) getLocation_home().getLongitude());
        editor.commit();

        SharedPreferences location_work = getSharedPreferences("location_work", 1);
        SharedPreferences.Editor editor1 = location_work.edit();
        editor1.putFloat("location_work_latitude", (float) getLocation_work().getLatitude());
        editor1.putFloat("location_work_longitude", (float) getLocation_work().getLongitude());
        editor1.commit();
    }

    private void setUpMap() {
        GPSTracker gpsTracker = new GPSTracker(this);
        Location location = null;
        if (gpsTracker.canGetLocation() == true) {
            location = gpsTracker.getLocation();
        }

/*
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("GPS Location"));
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }
*/
        if (location != null) {
            centerMapOnMYLocation(location);

        }
    }

    public void centerMapOnMYLocation(Location location) {
        float zoom = 10;
        mMap.setMyLocationEnabled(true);
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
        setupDialog();
    }

    private void setupDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.popupview);
        TextView textView = (TextView) dialog.findViewById(R.id.popup_text);
        textView.setText("Tap screen to set your home location");
//        dialog.show();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (set_up_home_location == true) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Home").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.homeiconsmall)));
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            Location location = new Location("dummyProvider");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            this.setLocation_home(location);
            set_up_home_location = false;
        }
        if (set_up_work_location == true) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Work").
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.workiconsmall)));
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            Location location = new Location("dummyProvider");
            location.setLatitude(latitude);
            location.setLongitude(longitude);


            this.setLocation_work(location);
            set_up_work_location = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.maps_activity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.set_up_home_loc) {
            openSetUpHomeLoc();
            return true;
        } else if (item.getItemId() == R.id.set_up_work_loc) {
            openSetUpWorkLoc();
            return true;
        } else if (item.getItemId() == R.id.action_notifications) {
            openNotificationsWindow();
        } else if (item.getItemId() == R.id.add_friend) {
            openAddFriendWindow();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openAddFriendWindow() {
        Intent addFriendIntent = new Intent(this, AddFriendWindow.class);
        this.startActivity(addFriendIntent);
        SharedPreferences preferences = getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        int instances = preferences.getInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, 0);
        if (instances > 1) {
            this.finish();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, instances--);
        }
    }

    private void openNotificationsWindow() {
        Intent notificationsIntent = new Intent(this, NotificationWindow.class);
        this.startActivity(notificationsIntent);
        SharedPreferences preferences = getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        int instances = preferences.getInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, 0);
        if (instances > 1) {
            this.finish();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, instances--);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences preferences = getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        int instances = preferences.getInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, 0);
        if (instances > 1) {
            //Pop-up that says: Tap again to exit
        }
    }

    private void openSetUpWorkLoc() {
        set_up_work_location = true;
        mMap.setOnMapClickListener(this);

    }

    private void openSetUpHomeLoc() {
        set_up_home_location = true;
        mMap.setOnMapClickListener(this);
    }


}
