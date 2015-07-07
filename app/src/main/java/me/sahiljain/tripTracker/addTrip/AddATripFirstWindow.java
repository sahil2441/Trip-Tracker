package me.sahiljain.tripTracker.addTrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.Week;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 22/3/15.
 *
 * This class is not in use till version 5. May be used later.
 */
public class AddATripFirstWindow extends ActionBarActivity {

    private int currentColor;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText tripName;
    private CheckBox toFro;
    private RadioButton buttonOneTimeTrip;
    private RadioButton buttonRecurringTrip;
    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thursday;
    private CheckBox friday;
    private CheckBox saturday;
    private CheckBox sunday;

    /**
     * Instance of Trip from the Application class.This variable will store the trip details
     * through out all four activities
     */
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_a_trip_first);

        //Get trip instance
        trip = ((App) getApplication()).getTrip();

        //Set color of Action Bar --to have a uniformity
        sharedPreferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        currentColor = sharedPreferences.getInt(Constants.CURRENT_COLOR, 0xFF666666);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Drawable colorDrawable = new ColorDrawable(currentColor);
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{colorDrawable});
            getSupportActionBar().setBackgroundDrawable(layerDrawable);
        }

        setDefaultValues();

        final RadioButton buttonOneTimeTrip = (RadioButton) findViewById(R.id.one_time_trip_radio_button);
        final RadioButton buttonRecurringTrip = (RadioButton) findViewById(R.id.recurring_trip_radio_button);
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout_week_checkbox);

        buttonOneTimeTrip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonRecurringTrip.setChecked(false);
                    relativeLayout.setVisibility(View.INVISIBLE);
                } else {
                    buttonRecurringTrip.setChecked(true);
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonRecurringTrip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonOneTimeTrip.setChecked(false);
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    buttonOneTimeTrip.setChecked(true);
                    relativeLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        final Intent secondWindow = new Intent(this, AddATripSourceWindow.class);

        Button button = (Button) findViewById(R.id.next_button_add_a_trip_first);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Start second screen of add a trip--like a train
                 * Start new activity
                 * Save data into shared preferences before moving ahead
                 */
                saveData();
                startActivity(secondWindow);
            }
        });
    }

    private void setDefaultValues() {

        tripName = (EditText) findViewById(R.id.trip_name_add_a_trip_first);
        toFro = (CheckBox) findViewById(R.id.to_fro_check_box);
        buttonOneTimeTrip = (RadioButton) findViewById(R.id.one_time_trip_radio_button);
        buttonRecurringTrip = (RadioButton) findViewById(R.id.recurring_trip_radio_button);
        monday = (CheckBox) findViewById(R.id.Monday);
        tuesday = (CheckBox) findViewById(R.id.Tuesday);
        wednesday = (CheckBox) findViewById(R.id.Wednesday);
        thursday = (CheckBox) findViewById(R.id.Thursday);
        friday = (CheckBox) findViewById(R.id.Friday);
        saturday = (CheckBox) findViewById(R.id.Saturday);
        sunday = (CheckBox) findViewById(R.id.Sunday);

        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }

        //set default values from trip instance
        if (trip != null) {
            if (trip.getTripName() != null) {
                tripName.setText(trip.getTripName());
            }
            if (trip.getToAndFro() != null) {
                toFro.setChecked(trip.getToAndFro());
            }
            if (trip.getOneTimeTrip() != null) {
                buttonOneTimeTrip.setChecked(trip.getOneTimeTrip());
            }
            if (trip.getWeek() != null) {
                if (trip.getWeek().getMonday() != null) {
                    monday.setChecked(trip.getWeek().getMonday());
                }
                if (trip.getWeek().getTuesday() != null) {
                    tuesday.setChecked(trip.getWeek().getTuesday());
                }
                if (trip.getWeek().getWednesday() != null) {
                    wednesday.setChecked(trip.getWeek().getWednesday());
                }
                if (trip.getWeek().getThursday() != null) {
                    thursday.setChecked(trip.getWeek().getThursday());
                }
                if (trip.getWeek().getFriday() != null) {
                    friday.setChecked(trip.getWeek().getFriday());
                }
                if (trip.getWeek().getSaturday() != null) {
                    saturday.setChecked(trip.getWeek().getSaturday());
                }
                if (trip.getWeek().getSunday() != null) {
                    sunday.setChecked(trip.getWeek().getSunday());
                }
            }
        }
    }

    private void saveData() {

        if (trip == null) {
            trip = ((App) getApplication()).getTrip();
        }

        if (trip != null) {
            trip.setTripName(tripName.getText().toString());
            trip.setToAndFro(toFro.isChecked());
            trip.setOneTimeTrip(buttonOneTimeTrip.isChecked());
            trip.setWeek(new Week());
            trip.getWeek().setMonday(monday.isChecked());
            trip.getWeek().setTuesday(tuesday.isChecked());
            trip.getWeek().setWednesday(wednesday.isChecked());
            trip.getWeek().setThursday(thursday.isChecked());
            trip.getWeek().setFriday(friday.isChecked());
            trip.getWeek().setSaturday(saturday.isChecked());
            trip.getWeek().setSunday(sunday.isChecked());
        }
        //Save trip to application
        ((App) getApplication()).setTrip(trip);
    }
}
