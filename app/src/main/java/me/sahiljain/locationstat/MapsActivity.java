package me.sahiljain.locationstat;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

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

    private final String LOGIN_DETAILS = "Login";

    private final String PASSWORD = "mypass";

    private final String LOGIN_STATUS = "loginStatus";

    private String globalUserName = "";

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(LOGIN_DETAILS, MODE_PRIVATE);

        //Initialize Parse
        Parse.initialize(this, "g6RAVxcxermOczF7n8WEuN7nBTe7vTzADJTqMh6F", "v5zBzf0ZxefhdnLnRulZ8dSkUjsOn1sYuQAEb89Z");

        if (preferences.getBoolean(LOGIN_STATUS, false) == false) {
            setContentView(R.layout.sign_up_mobile);

            final TextView tv_country_code = (TextView) findViewById(R.id.country_code_input);
            final TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);

            Button createAccountbutton = (Button) findViewById(R.id.create_Account);
            createAccountbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser user = new ParseUser();
                    final String userName = tv_country_code.getText().toString() + tv_mobile_no.getText().toString();
                    globalUserName = userName;

                    user.setUsername(userName);
                    user.setPassword(PASSWORD);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //Congrats!
                                Log.d(TAG, "New user signed up");

/*                              ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                ArrayList<String> channels = new ArrayList<String>();
                                channels.add(userName);
                                installation.addAllUnique("channels", channels);
*/
                                ParsePush.subscribeInBackground("c" + userName, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.d(TAG, "User Subscribed Successfully");
                                        } else {
                                            Log.d(TAG, "User didn't subscribe Successfully");
                                        }
                                    }
                                });
                                updateLoginDetails();

                            } else {
                                //Shit!
                                Log.d(TAG, "New user couldn't get signed up");
                            }
                            ParseInstallation.getCurrentInstallation().saveInBackground();
                        }
                    });
                }
            });

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
     * Method called only once- when the user is successfully subscribed for the first time.
     */

    private void updateLoginDetails() {
        SharedPreferences preferences = getSharedPreferences(LOGIN_DETAILS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOGIN_STATUS, true);
        editor.putString("userID", globalUserName);
        editor.putString("password", PASSWORD);
        editor.commit();

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(intent);
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
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(LOGIN_DETAILS, MODE_PRIVATE);
        // Check device for Play Services APK.
        checkPlayServices();
//        Restore map state
        if (preferences.getBoolean(LOGIN_STATUS, false) == true) {
            setContentView(R.layout.activity_maps);
            setUpMapIfNeeded();
            Intent intent = new Intent(getApplicationContext(), NotificationService.class);
            getApplicationContext().startService(intent);
        }
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
        }

        if (item.getItemId() == R.id.set_up_work_loc) {
            openSetUpWorkLoc();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
