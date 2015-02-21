package me.sahiljain.locationstat.main;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.dialog.UseCurrentLocationDialog;
import me.sahiljain.locationstat.notificationService.NotificationService;
import me.sahiljain.locationstat.service.GPSTracker;
import me.sahiljain.locationstat.windows.Notification;
import me.sahiljain.locationstat.windows.Preferences;
import me.sahiljain.locationstat.windows.StartJourney;
import me.sahiljain.locationstat.windows.WelcomeSignUp;

public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMapClickListener,
        UseCurrentLocationDialog.UseCurrentLocationDialogListener {

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

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public Location getLocation_home() {
        return location_home;
    }

    public void setLocation_home(Location location_home) {
        this.location_home = location_home;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences
                (Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);

        if (preferences.getBoolean(Constants.LOGIN_STATUS, false) == false) {
            Intent welcomeSignUpWindowIntent = new Intent(this, WelcomeSignUp.class);
            startActivity(welcomeSignUpWindowIntent);

        } else {
            String userID = preferences.getString("userID", "");
            ParseUser.logInInBackground(userID, Constants.PASSWORD);
        }

        Location locationHome = getHomeLocationFromSharedPreferences();
        setLocation_home(locationHome);

        Location locationWork = getWorkLocationFromSharedPreferences();
        setLocation_work(locationWork);
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

    private Location getHomeLocationFromSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        double latitude = (double) sharedPreferences.getFloat("location_home_latitude", 0);
        double longitude = (double) sharedPreferences.getFloat("location_home_longitude", 0);

        Location location = new Location("dummy");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    private Location getWorkLocationFromSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        double latitude = (double) sharedPreferences.getFloat("location_work_latitude", 0);
        double longitude = (double) sharedPreferences.getFloat("location_work_longitude", 0);

        Location location = new Location("dummy");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        // Check device for Play Services APK.
        checkPlayServices();

//        Restore map state
        if (preferences.getBoolean(Constants.LOGIN_STATUS, false) == true) {
            try {
                setContentView(R.layout.activity_maps);
                setUpMapIfNeeded();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Exception caught OnResume() at setContentView()");

            }
/*
            SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            //Getting a reference to the map
            GoogleMap map=mapFragment.getMap();

            Button findButton = (Button) findViewById(R.id.search_button_in_maps);
            findButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText= (EditText) findViewById(R.id.edit_text_maps);
                    String searchLocation=editText.getText().toString();
                    if(searchLocation!=null&&!searchLocation.equals("")){
                        new GeocoderTask().execute(searchLocation);
                    }
                }
            });
*/
            Intent notificationServiceIntent = new Intent(getApplicationContext(), NotificationService.class);
            getApplicationContext().startService(notificationServiceIntent);
        }
    }
/*
    private class GeocoderTask extends AsyncTask<String,Void,List<Address>>{
        @Override
        protected List<Address> doInBackground(String...searchLocation) {
            //Create an instance of Geocoder class
            Geocoder geocoder=new Geocoder(getBaseContext());
            List<Address> addresses=null;
            try{
                //Try to get a max of three results for the search
                addresses=geocoder.getFromLocationName(searchLocation[0],1);
            }catch (Exception e){
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if(addresses==null||addresses.size()==0){
                Toast.makeText(getBaseContext(),"No LocationFound",Toast.LENGTH_SHORT).show();
            }
            else{
                Address address=addresses.get(0);
                Location location=new Location("dummyProvider");
                location.setLongitude(address.getLongitude());
                location.setLatitude(address.getLatitude());
                centerMapOnMYLocation(location);
            }
        }
    }
*/

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

        SharedPreferences location_home = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = location_home.edit();
        editor.putFloat("location_home_latitude", (float) getLocation_home().getLatitude());
        editor.putFloat("location_home_longitude", (float) getLocation_home().getLongitude());
        editor.commit();

        SharedPreferences location_work = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        SharedPreferences.Editor editor1 = location_work.edit();
        editor1.putFloat("location_work_latitude", (float) getLocation_work().getLatitude());
        editor1.putFloat("location_work_longitude", (float) getLocation_work().getLongitude());
        editor1.commit();
    }

    private void setUpMap() {
        Location location = null;
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation() == true) {
            location = gpsTracker.getLocation();
        }
        if (location != null) {
            centerMapOnMYLocation(location);

        }
    }

    public void centerMapOnMYLocation(Location location) {
        float zoom = 10;
        mMap.setMyLocationEnabled(true);
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zoom));
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

            //Show Toast
            Toast toast = Toast.makeText(this, "Home Location Saved", Toast.LENGTH_SHORT);
            toast.show();

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

            //Show Toast
            Toast toast = Toast.makeText(this, "Work Location Saved", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.maps_activity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Total 5 options in the action Bar
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            openNotificationsWindow();
        } else if (item.getItemId() == R.id.start_a_journey) {
            openStartJourney();
        } else if (item.getItemId() == R.id.preferences) {
            openPreferencesWindow();
        } else if (item.getItemId() == R.id.set_up_home_loc) {
            openSetUpHomeLoc();
        } else if (item.getItemId() == R.id.set_up_work_loc) {
            openSetUpWorkLoc();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openStartJourney() {
        //TODO Change this
        Intent intent = new Intent(this, StartJourney.class);
        this.startActivity(intent);
    }

    private void openPreferencesWindow() {
        Intent preferencesIntent = new Intent(this, Preferences.class);
        this.startActivity(preferencesIntent);
    }

    private void openNotificationsWindow() {
        Intent notificationsIntent = new Intent(this, Notification.class);
        this.startActivity(notificationsIntent);
    }

    private void openSetUpWorkLoc() {
        set_up_work_location = true;
        set_up_home_location = false;
        mMap.clear();

        // Create an instance of the dialog and show it
        DialogFragment dialog = new UseCurrentLocationDialog();
        dialog.show(getSupportFragmentManager(), String.valueOf(UseCurrentLocationDialog.class));
    }

    private void openSetUpHomeLoc() {
        set_up_home_location = true;
        set_up_work_location = false;
        mMap.clear();

        // Create an instance of the dialog and show it
        DialogFragment dialog = new UseCurrentLocationDialog();
        dialog.show(getSupportFragmentManager(), String.valueOf(UseCurrentLocationDialog.class));
    }

    /**
     * Two methods implemented from the
     * 'UseCurrentLocationDialogue' class
     *
     * @param dialogFragment
     */

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        Location location = null;
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation() == true) {
            location = gpsTracker.getLocation();
        }
        if ((this.set_up_home_location) && (location != null)) {
            this.setLocation_home(location);
            Toast toast = Toast.makeText(this, "Current Location Saved as Home Location", Toast.LENGTH_SHORT);
            toast.show();
        } else if ((this.set_up_work_location) && (location != null)) {
            this.setLocation_work(location);
            Toast toast = Toast.makeText(this, "Current Location Saved as Work Location", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        Toast toast = Toast.makeText(this, Constants.TAP_ANYWHERE_NOTIFICATION, Toast.LENGTH_LONG);
        toast.show();
        mMap.setOnMapClickListener(this);
    }

    @Override
    protected void onUserLeaveHint() {
        //TODO: Bug here--on press home
//        finish();
    }

}
