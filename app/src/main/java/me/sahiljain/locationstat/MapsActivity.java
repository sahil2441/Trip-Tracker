package me.sahiljain.locationstat;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBSubscription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    TextView mDisplay;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        Location location = getLocationFromSharedPreferences("location_home", 0);
        setLocation_home(location);

        location = getLocationFromSharedPreferences("location_work", 1);
        setLocation_work(location);

        /**
         * Set up the device with QuickBlox
         */
//        intApplication();

        context = getApplicationContext();

        /**
         * Check device for Play Services APK. If check succeeds, proceed with GCM registration.
         */
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found");
        }
    }

    private void intApplication(final String regId) {
        // Set your QuickBlox application credentials here
        QBSettings.getInstance().fastConfigInit("19423", "ZjmEEQ8XMdaabrc", "s3YLhXZCvETThZS");

        // Create quickblox session with user
        QBAuth.createSession("abc", "12345678", new QBEntityCallbackImpl<QBSession>() {
                    @Override
                    public void onSuccess(QBSession result, Bundle params) {

                        /**
                         *If session is created succesfully then the user is subscribed.
                         *
                         *  Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
                         * messages to your app. Not needed for this demo since the device sends upstream messages
                         * to a server that echoes back the message using the 'from' address in the message.
                         */
                        Log.d(TAG, "Subscribing. . . .");

                        String deviceID;

                        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        if (telephonyManager.getDeviceId() != null) {
                            deviceID = telephonyManager.getDeviceId();//use for mobiles
                        } else {
                            deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        }
                        QBMessages.subscribeToPushNotificationsTask(regId, deviceID,
                                QBEnvironment.DEVELOPMENT, new QBEntityCallbackImpl<ArrayList<QBSubscription>>() {
                                    @Override
                                    public void onSuccess(ArrayList<QBSubscription> result, Bundle params) {
                                        Log.d(TAG, "Subscribed. . . . ");
                                    }

                                    @Override
                                    public void onError(List<String> errors) {
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(List<String> errors) {

                    }
                }
        );
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device Registered, Registration ID = " + regId;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    Handler handler = new Handler(context.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            intApplication(regId);
                        }
                    });

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException e) {
                    msg = "Error:" + e.getMessage();
                }
                return msg;

            }

            @Override
            protected void onPostExecute(String msg) {
//                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);

    }

    private void storeRegistrationId(Context context, String regId) {
        SharedPreferences sharedPreferences = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving Registration ID on app version : " + appVersion);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */

    private String getRegistrationId(Context context) {
        final SharedPreferences sharedPreferences = getGcmPreferences(context);
        String registrationId = sharedPreferences.getString("PROPERTY_REG_ID", "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not Found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.

        int registeredVersion = sharedPreferences.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version Changed");
            return "";
        }
        return registrationId;
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
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        // Check device for Play Services APK.
        checkPlayServices();
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

/*
        Uri uri=Uri.parse("smsto:+9557282071");
        Intent intent=new Intent(Intent.ACTION_SENDTO,uri);
        intent.setPackage("com.whatsapp");
        intent.putExtra("sms_body","Text here");
        intent.putExtra("chat",true);
        startActivity(intent);
*/

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
