package me.sahiljain.tripTracker.menu;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.main.App;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 16/6/15.
 */
public class ProfileActivity extends Activity {

    private TextView textView;
    private EditText editText;
    private Button buttonSave;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);

        textView = (TextView) findViewById(R.id.output_text_user_name);
        editText = (EditText) findViewById(R.id.input_text_user_name);
        buttonSave = (Button) findViewById(R.id.save_button_profile);

        textView.setText("Name");
        editText.setText(preferences.getString(Constants.FIRST_NAME, ""));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFirstName();
            }
        });
    }

    private void saveFirstName() {
        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(Constants.FIRST_NAME, editText.getText().toString());
        editor.apply();
        Toast toast = Toast.makeText(this, "User Name Updated!", Toast.LENGTH_LONG);
        toast.show();
        finish();
    }

    private String getUserName() {
        return ((App) getApplicationContext()).getUserName();
    }
}
