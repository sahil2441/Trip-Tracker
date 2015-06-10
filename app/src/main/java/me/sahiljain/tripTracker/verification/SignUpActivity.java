package me.sahiljain.tripTracker.verification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 27/4/15.
 */
public class SignUpActivity extends Activity {
    TextView timerTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Launch_Activity_Theme);
        getWindow().setBackgroundDrawableResource(R.drawable.transparent);
        openVerifyScreen();
    }

    private void openVerifyScreen() {

        setContentView(R.layout.sign_up_mobile);
        final Button continueButton = (Button) findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final EditText tv_country_code = (EditText) findViewById(R.id.country_code_input);
                final TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);

                String mobileNumber = tv_mobile_no.getText().toString();
                String countryCode = tv_country_code.getText().toString();
                countryCode = countryCode.replaceAll("[-+.^:,]", "");
                countryCode = countryCode.replaceAll("[^\\d.]", "");

                String userName = countryCode + mobileNumber;

                if (mobileNumber == "") {
                    showErrorDialog("Please enter a valid mobile number");
                } else {
                    showConfirmationDialog(userName, mobileNumber, countryCode);
                }
            }
        });
    }

    private void showErrorDialog(final String error) {
        new AlertDialog.Builder(this)
                .setTitle("oops...")
                .setMessage(error)
                .setPositiveButton(Constants.OKAY, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public void showConfirmationDialog(final String userName, final String mobileNumber, String countryCode) {
        final SharedPreferences preferences = getSharedPreferences
                (Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        new AlertDialog.Builder(this)
                .setMessage("Is this your correct number?\n" + countryCode + "-" + mobileNumber +
                        "\nA SMS will be send to verify this number. " +
                        "Standard carrier charges may apply.")
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
                        startCountDownTimer();
                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void startCountDownTimer() {
        timerTextField = (TextView) findViewById(R.id.timerTextField);
        new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTextField.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Toast.makeText(getBaseContext(), "Verification Failed, Please try later.",
                        Toast.LENGTH_LONG).show();
                enableUIComponents();
            }
        }.start();
    }

    private void verifyMobileNumber() {
        final SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final String mobileNumber = preferences.getString(Constants.MOBILE_NO, "");
        sendSMStoSelf(mobileNumber);
        Log.d(Constants.TAG, "SMS verification Started- SMS Sent");
    }

    private void sendSMStoSelf(String mobileNumber) {
        SmsManager manager = SmsManager.getDefault();
        Random rand = new Random();
        int randomInt = rand.nextInt() % 10000;
        randomInt = Math.abs(randomInt);
        manager.sendTextMessage(mobileNumber, null, "Your Verification Code is " + randomInt, null, null);
        Log.d(Constants.TAG, "Mobile verification Started: SMS sent");
    }


    private void disableUIComponents() {
        EditText tv_countryCode = (EditText) findViewById(R.id.country_code_input);
        TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        tv_mobile_no.setEnabled(false);
        tv_countryCode.setEnabled(false);
        continueButton.setEnabled(false);
    }

    /**
     * Method called when 300 sec timer is called finished to auto enable the UI components
     */
    private void enableUIComponents() {
        TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        TextView countryCode = (TextView) findViewById(R.id.country_code_input);
        Button createAccountButton = (Button) findViewById(R.id.continue_button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        progressBar.setVisibility(View.GONE);
        tv_mobile_no.setEnabled(true);
        countryCode.setEnabled(true);
        createAccountButton.setEnabled(true);
    }
}
