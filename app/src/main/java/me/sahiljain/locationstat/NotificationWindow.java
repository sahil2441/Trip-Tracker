package me.sahiljain.locationstat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by sahil on 15/2/15.
 */
public class NotificationWindow extends ActionBarActivity {
    private final String LOCATION_STAT_SHARED_PREFERNCES = "locationStatSharedPreferences";

    private final String NO_OF_INSTANCES_OF_MAIN_ACTIVITY = "no_of_instances_of_main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_list_view);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        Intent mapsActivityIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsActivityIntent);

/*
        SharedPreferences preferences = getSharedPreferences(LOCATION_STAT_SHARED_PREFERNCES, MODE_PRIVATE);
        int instances = preferences.getInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(NO_OF_INSTANCES_OF_MAIN_ACTIVITY, instances + 1);
        editor.commit();
*/
    }
}
