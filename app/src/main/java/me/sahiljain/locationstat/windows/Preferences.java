package me.sahiljain.locationstat.windows;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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

/**
 * Created by sahil on 21/2/15.
 */
public class Preferences extends ActionBarActivity {

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

        ListView listView = (ListView) findViewById(R.id.list_view_preferences);
        listView.setAdapter(new PreferencesAdapter(this, list));

        //intent to call up addFriend activity
        final Intent intentAddFriend = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //intent for notification settings
        final Intent intentNotificationSettings = new Intent(this, NotificationSettings.class);
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
                }
            }
        });
    }

    /**
     * The method is called when a contact is chosen from the Contacts Intent
     *
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
                    displayAllPhoneNumbers(name, id);
                }
            }
        }
    }

    private void displayAllPhoneNumbers(String name, String id) {

        SharedPreferences preferences = getSharedPreferences(Constants.LOCATION_STAT_SHARED_PREFERNCES,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int sizeInitial = preferences.getInt(Constants.NO_OF_FRIENDS, 0);

        // You know it has a number so now query it like this
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);

        List<String> allPhoneNumbers = new ArrayList<String>();

        while (phones.moveToNext()) {
            String phoneNumber = phones.getString(phones.getColumnIndex
                    (ContactsContract.CommonDataKinds.Phone.NUMBER));
            allPhoneNumbers.add(phoneNumber);
        }
        if (allPhoneNumbers.size() > 1) {
            //TODO: Save all phone numbers in a list and display in form of radio buttons to user

        } else if (allPhoneNumbers.size() == 1) {
            //TODO: Save in
        }

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
        onNavigateUp();
        finish();
    }
}
