package me.sahiljain.locationstat.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
        setContentView(R.layout.welcome_to_location_stat);
        Button button = (Button) findViewById(R.id.continue_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVerifyScreen();
            }
        });
    }

    private void openVerifyScreen() {

        Cognalys.enableAnalytics(getApplicationContext(), true, true);
        //TODO: Remove this and create a drop down for country code-- that automatically populates
//        final String countryCode = getCountryCode();
        setContentView(R.layout.sign_up_mobile);
        final Button continueButton = (Button) findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final TextView tv_country_code = (TextView) findViewById(R.id.country_code_input);
                final TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);

                String mobileNumber = tv_mobile_no.getText().toString();
                String countryCode = tv_country_code.getText().toString();

                String userName = countryCode + tv_mobile_no.getText().toString();

                if (mobileNumber == null || mobileNumber == "") {
                    showErrorDialog("Please enter a valid mobile number");
                } else {
                    showConfirmationDialog(userName, mobileNumber);
                }
            }
        });
    }

    public void showConfirmationDialog(final String userName, final String mobileNumber) {
        final SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        new AlertDialog.Builder(this)
                .setMessage("Is " + userName + " your correct number ?" +
                        "\n Location Stat will send a missed call to verify your phone number.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        disableUIComponents();

                        //Save user name in shared preferences
                        editor.putString(Constants.USER_NAME, userName);
                        editor.apply();

                        //Save mobile no in shared preferences
                        editor.putString(Constants.MOBILE_NO, mobileNumber);
                        editor.apply();

                        verifyMobileNumber();
                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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
                        findErrorMessage(strings);
                        enableUIComponents();
                    }
                });
    }

    private void findErrorMessage(ArrayList<String> strings) {

        for (String error : strings) {
            if (error.equals("551")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_551);
                break;
            } else if (error.equals("601") || error.equals("600")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_601);
                break;
            } else if (error.equals("504")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_504);
                break;
            } else if (error.equals("500")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_500);
                break;
            }
            Log.d(Constants.TAG, "Verification Failed: " + " " + error + '\n');
        }
    }

    private void showErrorDialog(final String error) {
        new AlertDialog.Builder(this)
                .setTitle("oops...")
                .setMessage(error)
                .setPositiveButton(Constants.OKAY, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (error.equalsIgnoreCase(Constants.ERROR_551)) {
                            Log.d(Constants.TAG, "Killing Viber now");
                            //TODO: kill viber here
                            killViber();
                        }

                    }
                })
                .show();
    }

    private void killViber() {
    }

    private void disableUIComponents() {
        TextView tv_countryCode = (TextView) findViewById(R.id.country_code_input);
        TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        tv_mobile_no.setEnabled(false);
        tv_countryCode.setEnabled(false);
        continueButton.setEnabled(false);
    }

    private void enableUIComponents() {
        TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        Button createAccountButton = (Button) findViewById(R.id.continue_button);
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
                            Toast.makeText(getBaseContext(), "Verification Failed, Please try later.", Toast.LENGTH_LONG).show();
                            enableUIComponents();
                        }
                    }
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                }
            });
        }
    }

    private void startParseSubscription() {

        final SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
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
                    enableUIComponents();
                }
            }
        });
    }

    /**
     * Method called only once- when the user is successfully subscribed for the first time.
     *
     * @param userName
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateLoginDetails(String userName) {

//        Toast.makeText(getBaseContext(), "Successfully Verified! Please Wait...", Toast.LENGTH_LONG).show();

        SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGIN_STATUS, true);
        editor.putString(Constants.USER_NAME, userName);
        editor.apply();

        //Start the notification service
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(intent);

        //Start Main Activity
        Intent intentMainActivity = new Intent(this, NameProfile.class);
        this.startActivity(intentMainActivity);

        //Finish this Activity
        this.finish();

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
