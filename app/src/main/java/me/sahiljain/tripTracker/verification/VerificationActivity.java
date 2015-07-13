package me.sahiljain.tripTracker.verification;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.tripTracker.main.TabMainActivity;
import me.sahiljain.tripTracker.notificationService.NotificationSendingService;

/**
 * Created by sahil on 11/6/15.
 */
public class VerificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES,
                MODE_PRIVATE);
        String origNumber = getIntent().getStringExtra(Constants.SMS_NUMBER);
        if (origNumber != null && origNumber != "" && !preferences.getBoolean(Constants.LOGIN_STATUS,
                false)) {
            if ((origNumber.equalsIgnoreCase(preferences.getString(Constants.USER_NAME, "")))
                    || (origNumber.equalsIgnoreCase(preferences.getString(Constants.MOBILE_NO, "")))) {
                Log.d(Constants.TAG, "SMS verification successful; Now starting " +
                        "parse process");
                startParseProcess();
            }
        }
    }

    private void startParseProcess() {
        final SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final String userName = preferences.getString(Constants.USER_NAME, "");

        if (userName != null && !userName.equalsIgnoreCase("")) {
            /**
             * Parse subscription starts here
             */
            ParseUser user = new ParseUser();
            user.setUsername(userName);
            user.setPassword(Constants.PASSWORD);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //Congrats!
                        Log.d(Constants.TAG, "New user signed up");

                        startParseSubscription();
                    } else {
                        /**
                         * This is in cases when user uninstalls app
                         * and re-installs it
                         */
                        String error = e.toString();
                        if (error.contains("already taken")) {
                            Log.d(Constants.TAG, "User already exists in Database; Starting subscription process");
                            startParseSubscription();
                        } else {
                            //Shit!
                            Log.d(Constants.TAG, "Error: " + e.toString() + " \nNew user couldn't get signed up");
                        }
                    }
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                }
            });
        }
    }

    private void startParseSubscription() {

        final SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final String userName = preferences.getString(Constants.USER_NAME, "");

        ParsePush.subscribeInBackground("c" + userName, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(Constants.TAG, "User Subscribed Successfully");
                    updateLoginDetails(userName);
                } else {
                    Log.d(Constants.TAG, "Error: " + e.toString() + "\n User didn't subscribe Successfully");
                    Toast.makeText(getBaseContext(), "Verification Failed, Please try later.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Method called only once- when the user is successfully subscribed for the first time.
     *
     * @param userName
     */
    private void updateLoginDetails(String userName) {

        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGIN_STATUS, true);
        editor.putString(Constants.USER_NAME, userName);
        editor.apply();

        //Start the notification service
        Intent intent = new Intent(getApplicationContext(), NotificationSendingService.class);
        getApplicationContext().startService(intent);

        boolean status = preferences.getBoolean(Constants.FIRST_LOGIN, true);
        if (status) {
            showVerificationSuccessfulToast();
            status = false;
            editor.putBoolean(Constants.FIRST_LOGIN, status);
            editor.apply();
        }
    }

    private void showVerificationSuccessfulToast() {
        Toast.makeText(getBaseContext(),
                "Verification Successful!", Toast.LENGTH_LONG).show();
        openMainTabActivityOnTopOfStack();
    }

    private void openMainTabActivityOnTopOfStack() {
        //Start Main Activity
        final Intent intentMainActivity = new Intent(this, TabMainActivity.class);
        intentMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentMainActivity);
    }
}

