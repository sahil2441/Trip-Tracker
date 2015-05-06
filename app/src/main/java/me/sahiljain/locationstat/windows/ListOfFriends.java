package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.tripTracker.R;
import me.sahiljain.locationstat.db.DataBaseFriends;

/**
 * Created by sahil on 28/2/15.
 */
public class ListOfFriends extends ActionBarActivity {

    private DataBaseFriends dataBaseFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.list_of_friends);
        ListView listView = (ListView) findViewById(R.id.list_view_list_of_friends);

        List<String> listOfFriends = new ArrayList<String>();
/*
        dataBaseFriends = new DataBaseFriends(this);
        listOfFriends = dataBaseFriends.fetchData();
        ListOfFriendsAdapter adapter = new ListOfFriendsAdapter(this, listOfFriends);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
*/
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();
        finish();
    }
}
