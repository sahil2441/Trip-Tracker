package me.sahiljain.locationstat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by sahil on 16/2/15.
 */
public class AddFriendWindow extends ActionBarActivity {
    private final String LOCATION_STAT_SHARED_PREFERNCES = "locationStatSharedPreferences";

    private ProgressBar progressBar;

    private final String NO_OF_INSTANCES_OF_MAIN_ACTIVITY = "no_of_instances_of_main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView tv_country_code = (TextView) findViewById(R.id.add_friend_country_code_input);
        final TextView tv_mobile_no = (TextView) findViewById(R.id.add_friend_mobile_no_input);
        progressBar = (ProgressBar) findViewById(R.id.add_friend_progress_bar);

        final Button saveAddFriendButton = (Button) findViewById(R.id.save_add_friend_button);
        saveAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Set Read Only Mode
                 */
                saveAddFriendButton.setEnabled(false);
                tv_country_code.setEnabled(false);
                tv_mobile_no.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                final String friendName = tv_country_code.getText().toString() + tv_mobile_no.getText().toString();
                SharedPreferences preferences = getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("friendName", friendName);
                editor.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent mapsActivityIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsActivityIntent);

/*
        SharedPreferences preferences=getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES,MODE_PRIVATE);
        int instances=preferences.getInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY,0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY,instances+1);
        editor.commit();
*/
    }
}
