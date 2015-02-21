package me.sahiljain.locationstat.windows;

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

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import me.sahiljain.locationstat.main.Constants;
import me.sahiljain.locationstat.main.MapsActivity;
import me.sahiljain.locationstat.notificationService.NotificationService;
import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 16/2/15.
 */
public class WelcomeSignUp extends Activity {

    private String globalUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_mobile);
        final TextView tv_country_code = (TextView) findViewById(R.id.country_code_input);
        final TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        Button createAccountbutton = (Button) findViewById(R.id.create_Account_button);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        createAccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.create_Account_button).setEnabled(false);
                tv_country_code.setEnabled(false);
                tv_mobile_no.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                ParseUser user = new ParseUser();
                final String userName = tv_country_code.getText().toString() + tv_mobile_no.getText().toString();
                globalUserName = userName;

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
                                    } else {
                                        Log.d(Constants.TAG, "User didn't subscribe Successfully");
                                    }
                                }
                            });
                            updateLoginDetails();

                        } else {
                            //Shit!
                            Log.d(Constants.TAG, "New user couldn't get signed up");
                        }
                        ParseInstallation.getCurrentInstallation().saveInBackground();
                    }
                });
            }
        });


    }

    /**
     * Method called only once- when the user is successfully subscribed for the first time.
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateLoginDetails() {
        SharedPreferences preferences = getSharedPreferences
                (Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGIN_STATUS, true);
        editor.putString("userID", globalUserName);
        editor.putString("password", Constants.PASSWORD);
        editor.commit();

        //Start the notification service
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(intent);

        //Start Main Activity
        Intent intentMainActivity = new Intent(this, MapsActivity.class);
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
