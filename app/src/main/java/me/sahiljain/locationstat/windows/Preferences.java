package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.sahiljain.tripTracker.R;
import me.sahiljain.locationstat.adapter.PreferencesAdapter;
import me.sahiljain.locationstat.db.DataBaseFriends;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 21/2/15.
 */
public class Preferences extends ActionBarActivity {
    public boolean isMatchFound() {
        return matchFound;
    }

    public void setMatchFound(boolean matchFound) {
        this.matchFound = matchFound;
    }

    private boolean matchFound = false;

    private DataBaseFriends dataBaseFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /**
         * Set up List View
         */
        List<String> list = new ArrayList<String>();
        list.add(Constants.ADD_A_FRIEND);
        list.add(Constants.PROFILE);
        list.add(Constants.NOTIFICATION_SETTINGS);
        list.add(Constants.LIST_OF_FRIENDS);

        ListView listView = (ListView) findViewById(R.id.list_view_preferences);
        listView.setAdapter(new PreferencesAdapter(this, list));

        //intent to call up addFriend activity
        final Intent intentAddFriend = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //intent for notification settings
        final Intent intentNotificationSettings = new Intent(this, NotificationSettings.class);

        //intent for profile
        final Intent intentProfile = new Intent(this, Profile.class);

        //intent for profile
        final Intent listOfFriends = new Intent(this, ListOfFriends.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) ((LinearLayout) view).findViewById
                        (R.id.text_view_preferences_list_item);
                String s = textView.getText().toString();

                if (s.equals(Constants.ADD_A_FRIEND)) {
                    startActivityForResult(intentAddFriend, 1);
                } else if (s.equals(Constants.NOTIFICATION_SETTINGS)) {
                    startActivity(intentNotificationSettings);
                } else if (s.equals(Constants.PROFILE)) {
                    startActivity(intentProfile);
                } else if (s.equals(Constants.LIST_OF_FRIENDS)) {
                    startActivity(listOfFriends);
                }
            }
        });
    }

    /**
     * The method is called when a contact is chosen from the Contacts Intent
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = null;

        if (data != null) {
            uri = data.getData();
        }

        if (resultCode == Activity.RESULT_OK && uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String hasPhone = cursor.getString(cursor.getColumnIndex
                        (ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equals("1")) {
                    analysePhoneNumbers(name, id);
                } else {
                    showDialog(Constants.NO_PHONE_NUMBERS_FOUND);
                }
            }
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("oops..")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();


    }

    private void analysePhoneNumbers(String name, String id) {

        // You know it has a number so now query it like this
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

        List<String> allPhoneNumbers = new ArrayList<String>();

        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex
                    (ContactsContract.CommonDataKinds.Phone.NUMBER));
            allPhoneNumbers.add(phoneNumber);
        }
        if (allPhoneNumbers.size() > 0) {
            for (int i = 0; i < allPhoneNumbers.size(); i++) {
                if (!isMatchFound()) {
                    String userName = allPhoneNumbers.get(i).replaceAll("[+\\s]", "");
                    findAMatch(userName, name, (i == allPhoneNumbers.size() - 1) ? true : false);
                }
            }
        }
    }

    private void findAMatch(final String userName, final String name, final boolean last) {
        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.d(Constants.TAG, "Received " + parseObjects.size() + " objects");
                    if (parseObjects.size() > 0) {
                        setMatchFound(true);
                        updateFriendListWindow(userName, name);
                    } else if (!isMatchFound() && last == true) {
                        showDialog(Constants.FRIEND_APP_NOT_INSTALLED);
                    }
                } else {
                    Log.d(Constants.TAG, "Error in Parse Query: " + e.getMessage());
                }
            }
        });
    }

    private void updateFriendListWindow(String userName, String name) {

        dataBaseFriends = new DataBaseFriends(this);
        //we save the channel name as it is required to be--preceded by a 'c'
//        dataBaseFriends.insert("c" + userName, name);

        Intent intentListOfFriends = new Intent(this, ListOfFriends.class);
        startActivity(intentListOfFriends);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_action_bar, menu);
        getSupportActionBar().setTitle("Preferences");
        getSupportActionBar().setIcon(R.drawable.source_icon_small);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onNavigateUp();
        finish();
    }
}
