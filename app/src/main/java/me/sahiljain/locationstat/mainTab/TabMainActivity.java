package me.sahiljain.locationstat.mainTab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseUser;
import com.shamanland.fab.FloatingActionButton;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.adapter.TabMainActivityAdapter;
import me.sahiljain.locationstat.addTrip.AddATripFirstWindow;
import me.sahiljain.locationstat.main.Constants;
import me.sahiljain.locationstat.main.WelcomeSignUp;
import me.sahiljain.locationstat.notificationService.NotificationService;
import me.sahiljain.locationstat.windows.Preferences;

/**
 * Created by sahil on 21/3/15.
 */
public class TabMainActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private PagerSlidingTabStrip tabs;
    private int currentColor;
    private Drawable oldBackground = null;
    private SharedPreferences preferences;
    private FloatingActionButton floatingActionButton;

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
