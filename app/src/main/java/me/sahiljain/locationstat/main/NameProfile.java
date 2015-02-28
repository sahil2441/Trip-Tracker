package me.sahiljain.locationstat.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 28/2/15.
 */
public class NameProfile extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        String name = preferences.getString(Constants.FIRST_NAME, "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_sign_up);

        if (!name.equalsIgnoreCase("")) {
            TextView textView = (TextView) findViewById(R.id.name_sign_up);
            textView.setText(name);
        }

        Button button = (Button) findViewById(R.id.save_name_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.name_sign_up);
                final String name = textView.getText().toString();

                if (name == null || name.equalsIgnoreCase("")) {
                    showErrorDialog();
                } else {
                    editor.putString(Constants.FIRST_NAME, name);
                    editor.apply();

                    startMainActivity();
                }
            }
        });
    }

    private void startMainActivity() {

        //Disable components
        TextView textView = (TextView) findViewById(R.id.name_sign_up);
        Button button = (Button) findViewById(R.id.save_name_button);

        textView.setEnabled(false);
        button.setEnabled(false);

        //Start Main Activity
        Intent intentMainActivity = new Intent(this, MapsActivity.class);
        this.startActivity(intentMainActivity);

        //Finish this Activity
        this.finish();
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("oops...")
                .setMessage("Please enter a valid name")
                .setPositiveButton(Constants.OKAY, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
