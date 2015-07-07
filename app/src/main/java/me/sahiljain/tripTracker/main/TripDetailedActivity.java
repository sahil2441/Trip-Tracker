package me.sahiljain.tripTracker.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserTrip;

/**
 * Created by sahil on 6/5/15.
 */
public class TripDetailedActivity extends Activity {

    private Button setDefault;
    private Button deleteTrip;
    private TextView tripNameTV;
    private TextView checkPoint1;
    private TextView checkPoint2;
    private ScrollView scrollView;

    private int tripId;
    private String tripName;
    private Persistence persistence;
    private SharedPreferences preferences;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.trip_detail_view);
        setDefault = (Button) findViewById(R.id.set_default_button);
        deleteTrip = (Button) findViewById(R.id.delete_trip_button);
        tripNameTV = (TextView) findViewById(R.id.textView_trip_name);
        checkPoint1 = (TextView) findViewById(R.id.textView_check_point1);
        checkPoint2 = (TextView) findViewById(R.id.textView_check_point2);

        Intent intent = getIntent();
        tripName = intent.getStringExtra(Constants.TRIP_NAME);
        tripId = intent.getIntExtra(Constants.TRIP_ID, 0);
        tripNameTV.setText(intent.getStringExtra(Constants.TRIP_NAME));
        checkPoint1.setText("Check Point 1: " + intent.getStringExtra(Constants.CHECK_POINT_1));
        checkPoint2.setText("Check Point 2: " + intent.getStringExtra(Constants.CHECK_POINT_2));

        preferences = this.getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, 0);
        currentColor = preferences.getInt(Constants.CURRENT_COLOR, 0xFF666666);
        tripNameTV.setBackgroundColor(currentColor);
        checkPoint1.setBackgroundColor(currentColor);
        checkPoint2.setBackgroundColor(currentColor);
        populateRecipientsInScrollView(tripId);

        deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTripFromDB(tripId, tripName);
            }
        });

        setDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateTrip(tripId, tripName);
            }
        });

        /**
         * Dim Background
         */
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void populateRecipientsInScrollView(int tripId) {
        scrollView = (ScrollView) findViewById(R.id.scrollView_recipients);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        persistence = new Persistence();
        TextView textViewHeading = new TextView(this);
        textViewHeading.setText(Constants.RECIPIENTS);
        textViewHeading.setTextColor(Color.BLACK);
        linearLayout.addView(textViewHeading);

        Trip trip = persistence.fetchTripById(this, tripId);
        if (trip != null) {
            if (trip.getFriendList() != null) {
                Iterator<UserTrip> userTripIterator = trip.getFriendList().iterator();
                while (userTripIterator.hasNext()) {
                    String userId = userTripIterator.next().getUserID().replace("c", "");
                    TextView textView = new TextView(this);
                    textView.setText(userId);
                    textView.setBackgroundColor(currentColor);
                    textView.setTextColor(Color.WHITE);
                    linearLayout.addView(textView);
                }
            }
        }
        scrollView.addView(linearLayout);
    }


    private void activateTrip(int tripId, String tripName) {
        persistence = new Persistence();
        persistence.activateTrip(this, tripId);
        /**
         * save default active trip is in shared preferences
         */
        preferences = this.getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.ACTIVE_TRIP, tripId);
        editor.apply();

        Toast toast = Toast.makeText(this, tripName + " " + "has been activated. Musafir will now track" +
                        " this trip and send updates.",
                Toast.LENGTH_LONG);
        toast.show();
        launchTabMainActivity();
    }

    private void deleteTripFromDB(int tripId, String tripName) {
        persistence = new Persistence();
        persistence.deleteTrip(this, tripId);
        Toast toast = Toast.makeText(this, tripName + " " + "has been deleted",
                Toast.LENGTH_SHORT);
        toast.show();
        launchTabMainActivity();
    }

    private void launchTabMainActivity() {
        Intent intent = new Intent(this, TabMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
