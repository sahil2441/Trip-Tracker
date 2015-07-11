package me.sahiljain.tripTracker.addTrip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParsePush;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserTrip;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.main.TabMainActivity;
import me.sahiljain.tripTracker.menu.HelpActivity;

/**
 * Created by sahil on 22/3/15.
 */
public class AddATripFourthWindow extends AppCompatActivity {

    private SharedPreferences preferences;
    private int currentColor;
    private String firstName;

    //Instance of Trip from the application class
    private Trip trip;

    //UI elements
    private Button previousButton;
    private Button saveButton;
    private Button searchButton;
    private LinearLayout linearLayout;
    private Button helpButton;

    private Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_a_trip_final);

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
        searchButton.startAnimation(getAnimation());
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Contacts Intent
                openContactsIntent();
                v.clearAnimation();
            }
        });

        //Instance of Trip from the application class
        trip = ((App) getApplication()).getTrip();

        final Intent helpActivity = new Intent(this, HelpActivity.class);
        helpButton = (Button) findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(helpActivity);
            }
        });

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
            textView.setTextSize(20);
            textView.setTypeface(Typeface.SANS_SERIF);
            textView.setTextColor(Color.BLACK);
            linearLayout.addView(textView);
        }

        Random rand = new Random();
        for (int i = 0; i < allPhoneNumbers.size(); i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(rand.nextInt() % 10000);
            checkBox.setText(allPhoneNumbers.get(i));
            checkBox.setTextSize(15);
            checkBox.setTextColor(Color.BLACK);
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

        activateThisNewTrip(trip);
        informAllRecipients(trip);

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

    /**
     * This method informs all the recipients regarding the creation of the trip.
     *
     * @param trip
     */
    private void informAllRecipients(Trip trip) {
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        firstName = preferences.getString(Constants.FIRST_NAME, "");

        sendNotification(firstName + " has added you as a recipient for the trip - " + trip.getTripName(), trip);
    }

    private void sendNotification(String message, Trip trip) {

        Collection<String> listOfChannels = getListOfChannels(trip);
        ParsePush push;
        String userID;
        String timeToShow = getTimeToShow();

        //Send Push Message
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        userID = preferences.getString(Constants.USER_NAME, "");
        message += "#" + userID;
        message += "$" + timeToShow;
        try {
            push = new ParsePush();
            push.setChannels(listOfChannels);
            push.setMessage(message);
            push.sendInBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<String> getListOfChannels(Trip trip) {
        Collection<String> collectionOfChannels = new ArrayList<>();
        if (trip != null && trip.getFriendList() != null &&
                trip.getFriendList().size() > 0) {
            for (UserTrip userTrip : trip.getFriendList()) {
                collectionOfChannels.add("c" + userTrip.getUserID());
            }
        }
        return collectionOfChannels;
    }


    private String getTimeToShow() {
        String time = getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String timeInString = time + " " + sdf.format(new Date());
        return timeInString;
    }

    private String getTime() {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        String s;
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            s = "AM";
        } else {
            s = "PM";
        }
        String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
                + " " + s;
        return curTime;
    }


    /**
     * This method will activate the the newly created trip and will deactivate the other trip
     *
     * @param trip
     */
    private void activateThisNewTrip(Trip trip) {
        Integer tripId = trip.getTripId();
        persistence = new Persistence();
        persistence.activateTrip(this, tripId);

        //save default active trip is in shared preferences
        preferences = this.getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.ACTIVE_TRIP, tripId);
        editor.apply();
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

    /**
     * Animation for Location Button
     *
     * @return
     */
    private Animation getAnimation() {
        Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back i
        return animation;
    }
}

