package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 21/2/15.
 */
public class Profile extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();
        finish();
    }
}
