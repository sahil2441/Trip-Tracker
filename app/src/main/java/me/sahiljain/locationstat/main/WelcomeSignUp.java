package me.sahiljain.locationstat.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.matesnetwork.callverification.Cognalys;
import com.matesnetwork.interfaces.VerificationListner;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.notificationService.NotificationService;

/**
 * Created by sahil on 16/2/15.
 */
public class WelcomeSignUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        Cognalys.enableAnalytics(getApplicationContext(), true, true);
        final String countryCode = getCountryCode();
        setContentView(R.layout.sign_up_mobile);
        final TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        final Button createAccountButton = (Button) findViewById(R.id.create_Account_button);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Disable UI Components
                findViewById(R.id.create_Account_button).setEnabled(false);
                tv_mobile_no.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String userName = countryCode + tv_mobile_no.getText().toString();

                //Save user name in shared preferences
                editor.putString(Constants.USER_NAME, userName);
                editor.apply();

                String mobileNumber = tv_mobile_no.getText().toString();
                //Save mobile no in shared preferences
                editor.putString(Constants.MOBILE_NO, mobileNumber);
                editor.apply();

                verifyMobileNumber();
            }
        });
    }

    private String getCountryCode() {
        String countryCode = Cognalys.getCountryCode(this);
        countryCode = countryCode.replaceAll("[+]", "");
        return countryCode;

    }

    private void verifyMobileNumber() {
        final SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        final String mobileNumber = preferences.getString(Constants.MOBILE_NO, "");

        /**
         * Verify Mobile number using Cognalys
         */
        Cognalys.verifyMobileNumber(WelcomeSignUp.this, Constants.COGNALYS_ACCESS_TOKEN,
                Constants.COGNALYS_APP_ID, mobileNumber, new VerificationListner() {
                    @Override
                    public void onVerificationStarted() {
                        Log.d(Constants.TAG, "Cognalys verification Started");
                    }

                    @Override
                    public void onVerificationSuccess() {
                        Log.d(Constants.TAG, "Cognalys verification successful; Now starting " +
                                "parse process");
                        startParseProcess();
                    }

                    @Override
                    public void onVerificationFailed(ArrayList<String> strings) {
                        Log.d(Constants.TAG, "Cognalys verification Failed");

                        for (String error : strings) {
                            Log.d(Constants.TAG, "Verification Failed: " + " " + error + '\n');
                        }
                        Toast.makeText(getBaseContext(), "Verification Failed, Please try later.", Toast.LENGTH_LONG).show();
                        setDisabledFalse();
                    }
                });
    }

    private void setDisabledFalse() {
        TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        Button createAccountButton = (Button) findViewById(R.id.create_Account_button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        progressBar.setVisibility(View.GONE);
        tv_mobile_no.setEnabled(true);
        createAccountButton.setEnabled(true);
    }

    private void startParseProcess() {
        final SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
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

                        ParsePush.subscribeInBackground("c" + userName, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(Constants.TAG, "User Subscribed Successfully");
                                    updateLoginDetails(userName);
                                } else {
                                    Log.d(Constants.TAG, "Error: " + e.toString() + "\n User didn't subscribe Successfully");
                                    Toast.makeText(getBaseContext(), "Verification Failed, Please try later.", Toast.LENGTH_LONG).show();
                                    setDisabledFalse();
                                }
                            }
                        });
                    } else {
                        //Shit!
                        Log.d(Constants.TAG, "New user couldn't get signed up");
                        Toast.makeText(getBaseContext(), "Verification Failed, Please try later.", Toast.LENGTH_LONG).show();
                    }
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                }
            });
        }
    }

    /**
     * Method called only once- when the user is successfully subscribed for the first time.
     *
     * @param userName
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateLoginDetails(String userName) {

        Toast.makeText(getBaseContext(), "Successfully Verified! Please Wait...", Toast.LENGTH_LONG).show();

        SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGIN_STATUS, true);
        editor.putString(Constants.USER_NAME, userName);
        editor.apply();

        //Start the notification service
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(intent);

        //Finish this Activity
        this.finish();

        //Start Main Activity
        Intent intentMainActivity = new Intent(this, MapsActivity.class);
        this.startActivity(intentMainActivity);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
