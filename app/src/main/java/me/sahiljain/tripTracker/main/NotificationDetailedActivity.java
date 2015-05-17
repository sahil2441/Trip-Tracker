package me.sahiljain.tripTracker.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.UserBlocked;

/**
 * Created by sahil on 6/5/15.
 */
public class NotificationDetailedActivity extends Activity {

    private Button unblockUser;
    private Button blockUser;
    private TextView notification;
    private TextView senderID;

    private String senderIDFromIntent;
    private String notificationFromIntent;
    private Persistence persistence;
    private SharedPreferences preferences;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notification_detail_view);
        unblockUser = (Button) findViewById(R.id.unblock_user);
        blockUser = (Button) findViewById(R.id.block_user);
        notification = (TextView) findViewById(R.id.notification_text);
        senderID = (TextView) findViewById(R.id.sender_id);

        Intent intent = getIntent();
        notificationFromIntent = intent.getStringExtra(Constants.NOTIFICATION);
        senderIDFromIntent = intent.getStringExtra(Constants.SENDER_ID);
        notification.setText(notificationFromIntent);
        senderID.setText("From: " + senderIDFromIntent);

        preferences = this.getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, 0);
        currentColor = preferences.getInt(Constants.CURRENT_COLOR, 0xFF666666);
        notification.setBackgroundColor(currentColor);
        senderID.setBackgroundColor(currentColor);

        blockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockUser(senderIDFromIntent);
            }
        });

        unblockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unblockUser(senderIDFromIntent);
            }
        });

        /**
         * Dim Background
         */
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void unblockUser(String senderIDFromIntent) {
        persistence = new Persistence();
        UserBlocked userBlocked = new UserBlocked(senderIDFromIntent);
        persistence.unblockUser(this, userBlocked);
        //Show Toast
        Toast toast = Toast.makeText(this, "User " + senderIDFromIntent + " Removed from Block List", Toast.LENGTH_LONG);
        toast.show();
        launchTabMainActivity();
    }

    private void blockUser(String senderIDFromIntent) {
        persistence = new Persistence();
        UserBlocked userBlocked = new UserBlocked(senderIDFromIntent);
        persistence.blockUser(this, userBlocked);
        //Show Toast
        Toast toast = Toast.makeText(this, "User " + senderIDFromIntent + " Added to Block List", Toast.LENGTH_LONG);
        toast.show();
        launchTabMainActivity();
    }


    private void launchTabMainActivity() {
        Intent intent = new Intent(this, TabMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
