package me.sahiljain.locationstat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.AppEventsLogger;
import com.facebook.RequestAsyncTask;
import com.facebook.android.Facebook;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMapClickListener {

    boolean set_up_home_location = false;
    boolean set_up_work_location = false;
    private Location location_home;

    private Context context;

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

    GoogleCloudMessaging gcm;

    String regId;

    public static final String PROPERTY_REG_ID = "registration_id";

    private static final String PROPERTY_APP_VERSION = "appVersion";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "694932255843";

    private FacebookLogin facebookLogin;
    //Instance of facebook class
    private Facebook facebook;
    private RequestAsyncTask requestAsyncTask;
    String FILENAME = "AndroidSSO_Data";
    private SharedPreferences sharedPreferences;

    public String getFacebookAccessToken() {
        return facebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }

    String facebookAccessToken = "";

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


    public Location getLocation_home() {
        return location_home;
    }

    public void setLocation_home(Location location_home) {
        this.location_home = location_home;
    }

    private final String FACEBOOK_LOGIN_STATUS = "facebookLoginStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize Parse
        Parse.initialize(this, "g6RAVxcxermOczF7n8WEuN7nBTe7vTzADJTqMh6F", "v5zBzf0ZxefhdnLnRulZ8dSkUjsOn1sYuQAEb89Z");
        SharedPreferences preferences = getSharedPreferences(FACEBOOK_LOGIN_STATUS, 0);
        if (preferences.getBoolean("fbLoginStatus", false) == false) {
            setContentView(R.layout.main_login_screen);


            if (savedInstanceState == null) {
                // Add the fragment on initial activity setup
                facebookLogin = new FacebookLogin();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, facebookLogin)
                        .commit();
            } else {
                // Or set the fragment from restored state info
                facebookLogin = (FacebookLogin) getSupportFragmentManager()
                        .findFragmentById(android.R.id.content);
                //show the Main screen here:
//            setContentView(R.layout.activity_maps);
            }
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("fbLoginStatus", true);
            editor.commit();
        } else {
            setContentView(R.layout.activity_maps);
            setUpMapIfNeeded();
        }

        Location location = getLocationFromSharedPreferences("location_home", 0);
        setLocation_home(location);

        location = getLocationFromSharedPreferences("location_work", 1);
        setLocation_work(location);

        context = getApplicationContext();
        /**
         * Send Push Message
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.logIn(Arrays.asList("email"), this, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser == null) {
                            Log.d(TAG, "User cancelled the authentication");
                        } else if (parseUser.isNew()) {
                            Log.d(TAG, "New user signed up");
                            ParsePush.subscribeInBackground("", new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Log.d(TAG, "User Subscribed Successfully");
                                    } else {
                                        Log.d(TAG, "User didn't subscribe Successfully");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "USer signed in through Facebook");
                        }
                    }
                }
        );
//        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
        // Save the current Installation to Parse.
        ParseInstallation.getCurrentInstallation().saveInBackground();
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //should never happen
            throw new RuntimeException("Couldn't get the package name" + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MapsActivity.class.getSimpleName(), Context.MODE_PRIVATE);
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

        /**
         * To accurately track the time people spend in your app,
         * you should also log a deactivate event in the onPause() method of
         * each activity where you added the activateApp() method above:
         */

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
        //Restore map state
//        setContentView(R.layout.activity_maps);
//        setUpMapIfNeeded();


        /**
         * App Events let you measure installs on your mobile app ads,
         * create high value audiences for targeting, and view
         * analytics including user demographics.
         */

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
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

    //Send an upstream message
    public void onclick(View view) {

    }
}
