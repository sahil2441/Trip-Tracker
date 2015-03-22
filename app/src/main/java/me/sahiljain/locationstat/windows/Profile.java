package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.main.Constants;

/**
 * Created by sahil on 21/2/15.
 */
public class Profile extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getSharedPreferences
                (Constants.LOCATION_STAT_SHARED_PREFERENCES, MODE_PRIVATE);

        final SharedPreferences.Editor editor = preferences.edit();

        setContentView(R.layout.profile);
        final TextView textView = (TextView) findViewById(R.id.name_profile_input);
        textView.setText(preferences.getString(Constants.FIRST_NAME, ""));
        textView.setEnabled(true);

        Button button = (Button) findViewById(R.id.button_save_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setEnabled(false);
                Toast.makeText(getBaseContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                editor.putString(Constants.FIRST_NAME, textView.getText().toString());
                editor.apply();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();
        finish();
    }
}
