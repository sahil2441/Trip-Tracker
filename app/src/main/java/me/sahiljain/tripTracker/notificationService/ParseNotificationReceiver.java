package me.sahiljain.tripTracker.notificationService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import java.util.Iterator;
import java.util.List;

import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.UserBlocked;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 8/2/15.
 */

/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 *
 * The objective of overriding ParsePushBroadcastReceiver is to block the users that are present in
 * the user's block list. However, this still needs a lot of work since the information is not
 * available, except while we debug. Weird!
 */


public class ParseNotificationReceiver extends ParsePushBroadcastReceiver {
    private Persistence persistence;

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isUserBlocked;
        Bundle extras = intent.getExtras();
        Log.i(Constants.NOTIFICATION_SERVICE_TAG, "ParseNotificationReceiver, Received : " + extras.toString());

        String senderID = null;
        //It needs some time process the variable extras--found while debugging
        if (extras != null) {
            senderID = (getSenderID(extras));
        }
        isUserBlocked = checkIfUserBlocked(context, senderID);

        String intentAction = intent.getAction();
        byte var5 = -1;
        switch (intentAction.hashCode()) {
            case -824874927:
                if (intentAction.equals("com.parse.push.intent.DELETE")) {
                    var5 = 1;
                }
                break;
            case -269490979:
                if (intentAction.equals("com.parse.push.intent.RECEIVE")) {
                    var5 = 0;
                }
                break;
            case 374898288:
                if (intentAction.equals("com.parse.push.intent.OPEN")) {
                    var5 = 2;
                }
        }

        switch (var5) {
            case 0:
                //Call parent's onPushReceive only if user is not blocked
                if (!isUserBlocked) {
                    this.onPushReceive(context, intent);
                }
                break;
            case 1:
                this.onPushDismiss(context, intent);
                break;
            case 2:
                this.onPushOpen(context, intent);
        }

        /**
         * For Notification Service
         * This ensures that service resumes on restarting the phone
         */
        Intent startNotificationServiceIntent = new Intent(context, NotificationSendingService.class);
        context.startService(startNotificationServiceIntent);
    }


    private String getSenderID(Bundle extras) {
        String senderID = null;
        if (extras != null) {
            senderID = extras.getParcelable(Constants.SENDER_ID);
        }
        return senderID;
    }

    /**
     * This method checks if the user who has sent the notification belongs to the blocklist.
     * returns true if it does.web
     *
     * @param context
     * @param userID
     * @return
     */
    private boolean checkIfUserBlocked(Context context, String userID) {
        persistence = new Persistence();
        List<UserBlocked> userBlockedList =
                persistence.fetchListOfBlockedUsers(context);
        if (userID != null && userBlockedList != null && userBlockedList.size() > 0) {
            Iterator<UserBlocked> userBlockedIterator = userBlockedList.iterator();
            while (userBlockedIterator.hasNext()) {
                if (userBlockedIterator.next().getUserID().equalsIgnoreCase(userID)) {
                    return true;
                }
            }
        }
        return false;
    }

}