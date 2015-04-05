package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.adapter.NotificationSettingsAdapter;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 21/2/15.
 */
public class NotificationSettings extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);

        List<String> list = new ArrayList<String>();
        list.add(Constants.NOTIFY_ON_REACH_HOME);
        list.add(Constants.NOTIFY_ON_LEAVING_HOME);
        list.add(Constants.NOTIFY_ON_REACH_WORKPLACE);
        list.add(Constants.NOTIFY_ON_LEAVING_WORKPLACE);

        ListView listView = (ListView) findViewById(R.id.list_view_notification_settings);
        listView.setAdapter(new NotificationSettingsAdapter(this, list));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notification_settings_action_bar, menu);
        getSupportActionBar().setTitle("Notification Settings");
        getSupportActionBar().setIcon(R.drawable.source_icon_small);
        return super.onCreateOptionsMenu(menu);
    }
}
