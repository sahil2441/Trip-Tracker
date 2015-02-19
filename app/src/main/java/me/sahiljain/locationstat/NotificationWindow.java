package me.sahiljain.locationstat;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil on 15/2/15.
 */
public class NotificationWindow extends ActionBarActivity {
    private static final String NOTIFICATIONS_SHARED_PREFERENCES = "Notifications_SP";
    private static final String NOTIFICATIONS_SIZE = "Notifications_Size";

    private static final String NOTIFICATIONS_FLAG = "Notifications_Flag";

    private List<String> list;

    private NotificationListAdapter adapter;

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_list_view);
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
/*
        int flag=preferences.getInt(NOTIFICATIONS_FLAG,0);
        for(int i=flag;i<size;i++){
            newList.add(preferences.getString("i"+flag,""));
        }

        flag=size;
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(NOTIFICATIONS_FLAG,flag);
        editor.commit();
*/
        return newList;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        onNavigateUp();
    }
}
