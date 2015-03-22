package me.sahiljain.locationstat.addTrip;

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

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.main.Constants;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripFirstWindow extends ActionBarActivity {

    private int currentColor;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private EditText tripName;
    private CheckBox toFro;
    private RadioButton buttonOneTimeTrip;
    private RadioButton buttonRecurringTrip;
    CheckBox monday;
    CheckBox tuesday;
    CheckBox wednesday;
    CheckBox thursday;
    CheckBox friday;
    CheckBox saturday;
    CheckBox sunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.add_a_trip_first);

        //Initialize variables
        initializeVariables();

        //Set color of Action Bar --to have a uniformity
        sharedPreferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);
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

        final Intent secondWindow = new Intent(this, AddATripSecondWindow.class);

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

    private void initializeVariables() {
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
    }

    private void setDefaultValues() {
        tripName.setText(sharedPreferences.getString(Constants.TRIP_NAME, ""));
        toFro.setChecked(sharedPreferences.getBoolean(Constants.TO_FRO, true));
        buttonOneTimeTrip.setChecked(sharedPreferences.getBoolean(Constants.ONE_TIME_TRIP, false));
        buttonRecurringTrip.setChecked(sharedPreferences.getBoolean(Constants.RECURRING_TRIP, true));
        monday.setChecked(sharedPreferences.getBoolean(Constants.MONDAY_FLAG, true));
        tuesday.setChecked(sharedPreferences.getBoolean(Constants.TUESDAY_FLAG, true));
        wednesday.setChecked(sharedPreferences.getBoolean(Constants.WEDNESDAY_FLAG, true));
        thursday.setChecked(sharedPreferences.getBoolean(Constants.THURSDAY_FLAG, true));
        friday.setChecked(sharedPreferences.getBoolean(Constants.FRIDAY_FLAG, true));
        saturday.setChecked(sharedPreferences.getBoolean(Constants.SATURDAY_FLAG, false));
        sunday.setChecked(sharedPreferences.getBoolean(Constants.SUNDAY_FLAG, false));
    }

    private void saveData() {
        editor.putString(Constants.TRIP_NAME, tripName.getText().toString());
        editor.putBoolean(Constants.TO_FRO, toFro.isChecked());
        editor.putBoolean(Constants.ONE_TIME_TRIP, buttonOneTimeTrip.isChecked());
        editor.putBoolean(Constants.RECURRING_TRIP, buttonRecurringTrip.isChecked());
        editor.putBoolean(Constants.MONDAY_FLAG, monday.isChecked());
        editor.putBoolean(Constants.TUESDAY_FLAG, tuesday.isChecked());
        editor.putBoolean(Constants.WEDNESDAY_FLAG, wednesday.isChecked());
        editor.putBoolean(Constants.THURSDAY_FLAG, thursday.isChecked());
        editor.putBoolean(Constants.FRIDAY_FLAG, friday.isChecked());
        editor.putBoolean(Constants.SATURDAY_FLAG, saturday.isChecked());
        editor.putBoolean(Constants.SUNDAY_FLAG, sunday.isChecked());
        editor.apply();
    }
}
