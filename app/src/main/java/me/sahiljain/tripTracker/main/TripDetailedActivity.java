package me.sahiljain.tripTracker.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.db.Persistence;

/**
 * Created by sahil on 6/5/15.
 */
public class TripDetailedActivity extends AppCompatActivity {

    private Button setDefault;
    private Button deleteTrip;
    private TextView tripNameTV;

    private int tripId;
    private String tripName;
    private Persistence persistence;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_detail_view);
        setDefault = (Button) findViewById(R.id.set_default_button);
        deleteTrip = (Button) findViewById(R.id.delete_trip_button);
        tripNameTV = (TextView) findViewById(R.id.textView_trip_name);

        Intent intent = getIntent();
        tripName = intent.getStringExtra(Constants.TRIP_NAME);
        tripId = intent.getIntExtra(Constants.TRIP_ID, 0);
        tripNameTV.setText(intent.getStringExtra(Constants.TRIP_NAME));

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

        getSupportActionBar().setTitle("Trip Details");
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

        Toast toast = Toast.makeText(this, tripName + " " + "has been set as Default Trip",
                Toast.LENGTH_SHORT);
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
