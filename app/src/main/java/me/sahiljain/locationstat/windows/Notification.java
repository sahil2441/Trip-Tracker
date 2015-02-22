package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.adapter.NotificationsAdapter;
import me.sahiljain.locationstat.main.Constants;

/**
 * This class represents the Notification window
 * that has the listView
 * Created by sahil on 15/2/15.
 */
public class Notification extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.notification_list_view);

        //Extract list from Shared Preferences
        List<String> list = new ArrayList<String>();
        list = getListFromSharedPreferences();

        NotificationsAdapter adapter = new NotificationsAdapter(this, list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    private List<String> getListFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(Constants.NOTIFICATIONS_SHARED_PREFERENCES, MODE_PRIVATE);
        int size = preferences.getInt(Constants.NOTIFICATIONS_SIZE, 0);
        List<String> newList = new ArrayList<String>();

        for (int i = 0; i < size; i++) {
            newList.add(preferences.getString("i" + i, ""));
        }
        return newList;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();
/*
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
*/

        this.finish();
    }
}
