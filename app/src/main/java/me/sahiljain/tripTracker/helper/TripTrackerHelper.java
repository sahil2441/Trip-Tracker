package me.sahiljain.tripTracker.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.UserDefault;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 5/4/15.
 * This class is of no use temporarily
 */
public class TripTrackerHelper {
    private Persistence persistence;

    private SharedPreferences preferences;

    private SharedPreferences.Editor editor;

    private static Context mContext;

    public class PrepareListViewUsers extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            /**
             * The method first saves all the contacts in the DB
             * and then in on post execute fetches those contacts to populate them in listView
             */
            prepareListInDatabase();
            return null;
        }
    }


    private void prepareListInDatabase() {
        List<UserDefault> userDefaults = getUserDefault();
        if (persistence == null) {
            persistence = new Persistence();
        }
        //Store these users in DB
        persistence.persistUserDefault(userDefaults);
    }

    private List<UserDefault> getUserDefault() {
        List<UserDefault> userDefaultList = null;
/*
        Cursor cursor = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            userDefaultList = new ArrayList<>();
            do {
                UserDefault userDefault = new UserDefault();

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));

                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                */
/**
 *This method should return true if phone number is found in parse DB.
 *//*

                if (analysePhoneNumber(number)) {
                    userDefault.setUserID(number);
                    userDefault.setName(name);
                    userDefaultList.add(userDefault);
                }
            } while (cursor.moveToNext());
        }
*/
        return userDefaultList;
    }

    private boolean analysePhoneNumber(String number) {

        number = number.replaceAll("[+\\s]", "");

        //If first char of number is zero
        if (String.valueOf(number.charAt(0)).equals("0")) {
            number = number.replaceFirst("0", "");
        }
        //method returns true if phone number is found
        return findAMatch(number);
    }

    /**
     * This method searches a given user name into parse DB and returns true if that
     * phone number is found, else return false.
     * Username is the mobile number which is the unique identifier
     */

    private boolean findAMatch(final String userName) {

//        preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(Constants.FIND_A_MATCH_FLAG, false);
        editor.apply();

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.d(Constants.TAG, "Received " + parseObjects.size() + " objects");
                    if (parseObjects.size() > 0) {
                        editor.putBoolean(Constants.FIND_A_MATCH_FLAG, true);
                        editor.apply();
                    }
                } else {
                    Log.d(Constants.TAG, "Error in Parse Query: " + e.getMessage());
                }
            }
        });
        return preferences.getBoolean(Constants.FIND_A_MATCH_FLAG, false);
    }

}



