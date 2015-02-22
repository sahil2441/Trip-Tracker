package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.main.Constants;

/**
 * Created by sahil on 16/2/15.
 */
public class AddFriend extends ActionBarActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.add_friend);
        final TextView tv_country_code = (TextView) findViewById(R.id.add_friend_country_code_input);
        final TextView tv_mobile_no = (TextView) findViewById(R.id.add_friend_mobile_no_input);
        progressBar = (ProgressBar) findViewById(R.id.add_friend_progress_bar);

        final Button saveAddFriendButton = (Button) findViewById(R.id.save_add_friend_button);
        saveAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set read mode

                saveAddFriendButton.setEnabled(false);
                tv_country_code.setEnabled(false);
                tv_mobile_no.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                final String friendName = tv_country_code.getText().toString() + tv_mobile_no.getText().toString();
                SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("friendName", friendName);
                editor.commit();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();
        this.finish();
    }
}
