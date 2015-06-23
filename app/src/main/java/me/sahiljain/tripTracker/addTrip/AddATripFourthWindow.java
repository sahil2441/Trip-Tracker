package me.sahiljain.tripTracker.addTrip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserTrip;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.main.TabMainActivity;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripFourthWindow extends AppCompatActivity {

    private SharedPreferences preferences;
    private int currentColor;

    //Instance of Trip from the application class
    private Trip trip;

    //UI elements
    private Button previousButton;
    private Button saveButton;
    private Button searchButton;
    private LinearLayout linearLayout;

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
        searchButton = (Button) findViewById(R.id.search_friends_add_trip_fourth);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Contacts Intent
                openContactsIntent();
            }
        });

        //Instance of Trip from the application class
        trip = ((App) getApplication()).getTrip();

    }

    private void openContactsIntent() {
        Intent intentAddFriend = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intentAddFriend, 1);
    }

    /**
     * The method is called when a contact is chosen from the Contacts Intent
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;

        if (data != null) {
            uri = data.getData();
        }

        if (resultCode == Activity.RESULT_OK && uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String hasPhone = cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equals("1")) {
                    analysePhoneNumbers(name, id);
                } else {
                    showDialog(Constants.NO_PHONE_NUMBERS_FOUND);
                }
            }
        }
    }

    private void analysePhoneNumbers(String name, String id) {

        // You know it has a number so now query it like this
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

        List<String> allPhoneNumbers = new ArrayList<String>();

        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex
                    (ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber.replaceAll("[-+\\s]", "");
            allPhoneNumbers.add(phoneNumber);
        }

        /**
         * Populate these in group of Radio Buttons
         */
        if (allPhoneNumbers.size() > 0) {
            populatePhoneNumbersInRadioButtons(name, allPhoneNumbers);
        }
    }

    private void populatePhoneNumbersInRadioButtons(String name, List<String> allPhoneNumbers) {

        linearLayout = (LinearLayout) findViewById(R.id.ll_inside_scroll);
        if (name != null && name != "") {
            TextView textView = new TextView(this);
            textView.setText("Choose contacts of: " + name + ".");
            linearLayout.addView(textView);
        }

        Random rand = new Random();
        for (int i = 0; i < allPhoneNumbers.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(rand.nextInt() % 10000);
            checkBox.setText(allPhoneNumbers.get(i));
            linearLayout.addView(checkBox);
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("oops..")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);

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

        persistence.saveTripInDataBase(this, trip);

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
        /**
         * Generate Random trip ID
         */
        Random rand = new Random();
        Integer tripId = Math.abs(rand.nextInt() % 1000000000);
        trip.setTripId(tripId);
        List<UserTrip> userTrips = new ArrayList<>();

        //Add user details
        linearLayout = ((LinearLayout) findViewById(R.id.ll_inside_scroll));

        if (linearLayout != null) {

            int noOfViews = linearLayout.getChildCount();
            for (int i = 0; i < noOfViews; i++) {
                View view = linearLayout.getChildAt(i);
                if ((view.getClass() == CheckBox.class)) {
                    CheckBox checkBox = (CheckBox) view;
                    if (checkBox.isChecked()) {
                        userTrips.add(new UserTrip(checkBox.getText().toString().replaceAll("[-+\\s]", ""), tripId));
                    }
                }
            }
        }

        trip.setFriendList(userTrips);
        trip.setActive(false);

        //Set Trip name as combination of Source name+to+DestinationName
        trip.setTripName(trip.getSourceName() + " to " + trip.getDestinationName());

        //set time stamp to null
        trip.setSourceTimeStamp(null);
        trip.setDestinationTimeStamp(null);
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
}

