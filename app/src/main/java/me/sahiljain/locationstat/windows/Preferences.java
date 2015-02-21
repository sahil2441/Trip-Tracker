package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.adapter.PreferencesAdapter;
import me.sahiljain.locationstat.main.Constants;
import me.sahiljain.locationstat.main.MapsActivity;

/**
 * Created by sahil on 21/2/15.
 */
public class Preferences extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        /**
         * Set up List View
         */
        List<String> list = new ArrayList<String>();
        list.add(Constants.ADD_A_FRIEND);
        list.add(Constants.PROFILE);
        list.add(Constants.NOTIFICATION_SETTINGS);

        ListView listView = (ListView) findViewById(R.id.list_view_preferences);
        listView.setAdapter(new PreferencesAdapter(this, list));

        //intent to call up addFriend activity
        final Intent intentAddFriend = new Intent(this, AddFriend.class);

        //intent for notification settings
        final Intent intentNotificationSettings = new Intent(this, NotificationSettings.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) ((LinearLayout) view).findViewById(R.id.text_view_preferences_list_item);
                String s = textView.getText().toString();

                if (s.equals(Constants.ADD_A_FRIEND)) {
                    startActivity(intentAddFriend);
                } else if (s.equals(Constants.NOTIFICATION_SETTINGS)) {
                    startActivity(intentNotificationSettings);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_action_bar, menu);
        getSupportActionBar().setTitle("Preferences");
        getSupportActionBar().setIcon(R.drawable.homeiconsmall);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        onNavigateUp();
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

}
