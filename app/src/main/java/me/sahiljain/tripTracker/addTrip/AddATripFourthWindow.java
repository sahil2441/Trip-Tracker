package me.sahiljain.tripTracker.addTrip;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.adapter.UserDefaultAdapter;
import me.sahiljain.locationstat.db.DataBaseFriends;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.IUser;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserDefault;
import me.sahiljain.tripTracker.entity.UserTrip;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.main.TabMainActivity;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripFourthWindow extends ActionBarActivity {

    private DataBaseFriends dataBaseFriends;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    private boolean matchFound = false;

    private int currentColor;

    //Instance of Trip from the application class
    private Trip trip;

    //UI elements
    private Button previousButton;
    private Button saveButton;
    private ListView listView;

    private Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_a_trip_fourth);

        //Method for previous and SAVE button
        previousButton = (Button) findViewById(R.id.previous_button_add_a_trip_fourth);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateUpToThirdWindow();
                finish();
            }
        });

        saveButton = (Button) findViewById(R.id.save_button_add_a_trip_fourth);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTripData();

            }
        });

        listView = (ListView) findViewById(R.id.listView_friends_add_a_trip_fourth);

        //Instance of Trip from the application class
        trip = ((App) getApplication()).getTrip();

        populateListView();
    }

    public void populateListView() {

        persistence = new Persistence();
        List<UserDefault> userDefaults = persistence.fetchUserDefault(this);

        if (userDefaults != null && userDefaults.size() > 0) {
            if (listView == null) {
                listView = (ListView) findViewById(R.id.listView_friends_add_a_trip_fourth);
            }
            UserDefaultAdapter adapter = new UserDefaultAdapter(this, userDefaults);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);

        //Set up color for Action bar
        currentColor = preferences.getInt(Constants.CURRENT_COLOR, 0xFF666666);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Drawable colorDrawable = new ColorDrawable(currentColor);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable});
            getSupportActionBar().setBackgroundDrawable(layerDrawable);
        }
    }

    private void saveTripData() {
        Trip trip = setUpTrip();

        if (persistence == null) {
            persistence = new Persistence();
        }

        //TODO: Persist in DB
        persistence.saveTripInDataBase(trip);

        //show dialogue for successful operation
        final Intent intent = new Intent(this, TabMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        new AlertDialog.Builder(this).setTitle("Success!")
                .setMessage("Trip Added Successfully")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                    }
                })
                .show();
    }

    private Trip setUpTrip() {
        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }
        SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
        List<UserTrip> userTrips = new ArrayList<>();

        //Iterate listView and get elements at this position
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            if (sparseBooleanArray.get(i)) {
                IUser userTrip = new UserTrip();
                //Add this user to the list TODO
                userTrip = (IUser) listView.getItemAtPosition(i);
                userTrips.add((UserTrip) userTrip);
            }
        }
        trip.setFriendList(userTrips);
        return trip;
    }

    @TargetApi(16)
    private void onNavigateUpToThirdWindow() {
        onNavigateUp();
    }

    private void showDialog(String title, String message, String positiveButton) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    private void updateFriendListWindow(String userName, String name) {

        /**
         * Store all the friends into a temporary DB. And later receive the list--and
         * also execute a delete query
         */
        dataBaseFriends = new DataBaseFriends(this);
        //we save the channel name as it is required to be--preceded by a 'c'
        userName = "c" + userName;
        UserTrip userTrip = new UserTrip(name, userName);
        dataBaseFriends.insert(userTrip);

        //Set users into the list view on this window

/*
        ListView listView = (ListView) findViewById(R.id.listView_friends_add_a_trip_fourth);
        List<UserTrip> userTrips = dataBaseFriends.fetchData();
        UserDefaultAdapter adapter = new UserDefaultAdapter(this, userTrips);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
*/
    }

}

