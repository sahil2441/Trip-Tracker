package me.sahiljain.tripTracker.menu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 20/6/15.
 */
public class HelpActivity extends Activity {

    private TextView textViewHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        textViewHelp = (TextView) findViewById(R.id.disclaimer_text_help);

        textViewHelp.setText(Constants.HELP_TEXT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
