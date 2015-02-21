package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.locationstat.notificationService.NotificationListAdapter;
import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 15/2/15.
 */
public class Notification extends ActionBarActivity {
    private static final String NOTIFICATIONS_SHARED_PREFERENCES = "Notifications_SP";
    private static final String NOTIFICATIONS_SIZE = "Notifications_Size";

    private List<String> list;

    private NotificationListAdapter adapter;

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.notification_list_view);
        list = new ArrayList<String>();
        //Extract list from Shared Preferences
        list = getListFromSharedPreferences();
        adapter = new NotificationListAdapter(this, list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    private List<String> getListFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences(NOTIFICATIONS_SHARED_PREFERENCES, MODE_PRIVATE);
        int size = preferences.getInt(NOTIFICATIONS_SIZE, 0);
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
        this.finish();

    }
}
