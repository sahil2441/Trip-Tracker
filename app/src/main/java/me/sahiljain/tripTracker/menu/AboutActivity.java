package me.sahiljain.tripTracker.menu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 15/6/15.
 */
public class AboutActivity extends Activity {

    private TextView textViewAbout;
    private TextView disclaimerText;
    private Button okayButton;
    private Button sendFeedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        textViewAbout = (TextView) findViewById(R.id.text_view_about);
        disclaimerText = (TextView) findViewById(R.id.disclaimer_text_about);

        textViewAbout.setText("Version 1.0 \n" +
                "Copyright \u00a9 2015 MusafirApps \n All Rights Reserved \n Email: " +
                "musafir.trip.tracker@gmail.com");
        disclaimerText.setText("Disclaimer: Please note that this app may show incorrect location coordinates" +
                " if the app is force closed by user " +
                "or an unexpected system shutdown occurs. The developers are not responsible " +
                "for any incorrect " +
                "location reports or any damages or loss in connection with the usage of this " +
                "application.");

        okayButton = (Button) findViewById(R.id.ok_about_activity);
        sendFeedbackButton = (Button) findViewById(R.id.send_feedback_about_activity);

        //onclick listeners
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okayButtonActionListener();
            }
        });

        sendFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedbackButtonActionListener();
            }
        });
    }

    private void sendFeedbackButtonActionListener() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "musafir.trip.tracker@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Musafir- Trip Tracker Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, getDeviceInfo());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private String getDeviceInfo() {
        String s = "";
        try {
            s = "Debug-infos:";
            s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
            s += "\n Device: " + android.os.Build.DEVICE;
            s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")\n\n";
        } catch (Exception e) {
            Log.e(Constants.TAG, e.toString());
        }
        return s;
    }

    private void okayButtonActionListener() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
