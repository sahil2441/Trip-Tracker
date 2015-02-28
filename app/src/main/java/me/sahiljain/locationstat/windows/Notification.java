package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.adapter.NotificationsAdapter;
import me.sahiljain.locationstat.db.DataBaseNotifications;

/**
 * This class represents the Notification window
 * that has the listView
 * Created by sahil on 15/2/15.
 */
public class Notification extends ActionBarActivity {

    private DataBaseNotifications dataBaseNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.notification_list_view);

        List<String> list = new ArrayList<String>();
        dataBaseNotifications = new DataBaseNotifications(this);
        list = dataBaseNotifications.fetchListNotifications();

        List<String> listTimeStamp = new ArrayList<String>();
        listTimeStamp = dataBaseNotifications.fetchListTime();

        NotificationsAdapter adapter = new NotificationsAdapter(this, list, listTimeStamp);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();

        this.finish();
    }
}
