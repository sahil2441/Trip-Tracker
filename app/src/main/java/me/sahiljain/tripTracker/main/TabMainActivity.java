package me.sahiljain.tripTracker.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.notificationService.NotificationService;
import me.sahiljain.locationstat.windows.Preferences;
import me.sahiljain.tripTracker.adapter.TabMainActivityAdapter;
import me.sahiljain.tripTracker.addTrip.AddATripFirstWindow;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.UserDefault;

/**
 * Created by sahil on 21/3/15.
 */
public class TabMainActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private int currentColor;
    private Drawable oldBackground = null;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FloatingActionButton floatingActionButton;
    private Persistence persistence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);

        if (preferences.getBoolean(Constants.LOGIN_STATUS, false) == false) {
            Intent welcomeSignUpWindowIntent = new Intent(this, WelcomeSignUp.class);
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

    @Override
    protected void onResume() {
        super.onResume();

        //Start Service
        Intent notificationServiceIntent = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(notificationServiceIntent);

        //Initialize screen
        initializeMainScreen();

        //Create list of default users
//        prepareListViewUserDefault();
    }

    /**
     * This method prepares a list of default users i.e. a list of all users who are in
     * phone's contact list and are registered with Parse Database.
     * This work is done on an Async task which runs on a separate thread than the UI thread
     * --this is executed only if at present there's no user present
     */
    private void prepareListViewUserDefault() {
        persistence = new Persistence();
        if (persistence.fetchUserDefault(this) == null || persistence.fetchUserDefault(this).size() == 0) {
            new PrepareListViewUsers().execute();
        }
    }

    public class PrepareListViewUsers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            /**
             * The method first saves all the contacts in the DB
             * and then in on post execute fetches those contacts to populate them in listView
             */
            prepareListInDatabase();
            return null;
        }
    }

    private void prepareListInDatabase() {
        List<UserDefault> userDefaults = getUserDefault();
        if (persistence == null) {
            persistence = new Persistence();
        }
        //Store these users in DB
        persistence.persistUserDefault(userDefaults);
    }

    private List<UserDefault> getUserDefault() {
        List<UserDefault> userDefaultList = null;
        Cursor cursor = getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                                ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            userDefaultList = new ArrayList<>();
            do {
                UserDefault userDefault = new UserDefault();

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));

                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                /**
                 *This method should return true if phone number is found in parse DB.
                 */
                if (analysePhoneNumber(number)) {
                    userDefault.setUserID(number);
                    userDefault.setName(name);
                    userDefaultList.add(userDefault);
                }
            } while (cursor.moveToNext());
        }
        return userDefaultList;
    }

    private boolean analysePhoneNumber(String number) {

        number = number.replaceAll("[+\\s]", "");

        //If first char of number is zero
        if (String.valueOf(number.charAt(0)).equals("0")) {
            number = number.replaceFirst("0", "");
        }
        //method returns true if phone number is found
        if (number.equals("917506471520")) {
            System.out.println("917506471520");
        }
        return findAMatch(number);
    }

    /**
     * This method searches a given user name into parse DB and returns true if that
     * phone number is found, else return false.
     * Username is the mobile number which is the unique identifier
     */

    private boolean findAMatch(final String userName) {

        preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(Constants.FIND_A_MATCH_FLAG, false);
        editor.apply();

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.d(Constants.TAG, "Received " + parseObjects.size() + " objects");
                    if (parseObjects.size() > 0) {
                        editor.putBoolean(Constants.FIND_A_MATCH_FLAG, true);
                        editor.apply();
                    }
                } else {
                    Log.d(Constants.TAG, "Error in Parse Query: " + e.getMessage());
                }
            }
        });
        return preferences.getBoolean(Constants.FIND_A_MATCH_FLAG, false);
    }

    private void initializeMainScreen() {

        currentColor = preferences.getInt(Constants.CURRENT_COLOR, 0xFF666666);

        setContentView(R.layout.tab_main_activity);

        // Initialize the ViewPager and set an adapter
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabMainActivityAdapter(getSupportFragmentManager()));
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);

        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setViewPager(viewPager);
        tabs.setIndicatorColor(currentColor);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //Disable the visibility of floating button of Notification tab is selected
                if (position != 0) {
                    floatingActionButton.setVisibility(View.INVISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.VISIBLE);
                }

                //Reload the list view through Adapter
/*
                viewPager.setAdapter(new TabMainActivityAdapter(getSupportFragmentManager()));
                tabs.setViewPager(viewPager);
*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Intent to be launched on click of fab button
        final Intent intent = new Intent(this, AddATripFirstWindow.class);

        //Fab Button
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setColor(currentColor);
        floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
        floatingActionButton.initBackground();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        changeColor(currentColor);
    }

    private void changeColor(int newColor) {
        tabs.setIndicatorColor(newColor);
        floatingActionButton.setColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Drawable colorDrawable = new ColorDrawable(newColor);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable});

            if (oldBackground == null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    layerDrawable.setCallback(drawableCallback);
                } else {
                    getSupportActionBar().setBackgroundDrawable(layerDrawable);
                }
            } else {
                TransitionDrawable transitionDrawable =
                        new TransitionDrawable(new Drawable[]{oldBackground, layerDrawable});

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    transitionDrawable.setCallback(drawableCallback);
                } else {
                    getSupportActionBar().setBackgroundDrawable(transitionDrawable);
                }
                transitionDrawable.startTransition(200);
            }
            oldBackground = layerDrawable;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        currentColor = newColor;

        /**
         * save current color in shared preferences
         */

        preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.CURRENT_COLOR, currentColor);
        editor.apply();
    }

    /**
     * Method called on tapping of colors in bottom
     *
     * @param v
     */
    public void onColorClicked(View v) {

        int color = Color.parseColor(v.getTag().toString());
        changeColor(color);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {

        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {

        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps_activity_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.preferences) {
            openPreferencesWindow();
        }
        return true;
    }

    private void openPreferencesWindow() {
        Intent preferencesIntent = new Intent(this, Preferences.class);
        this.startActivity(preferencesIntent);
    }


}
