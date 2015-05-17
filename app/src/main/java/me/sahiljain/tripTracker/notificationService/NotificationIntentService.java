package me.sahiljain.tripTracker.notificationService;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import me.sahiljain.tripTracker.db.Persistence;
import me.sahiljain.tripTracker.entity.Notification;
import me.sahiljain.tripTracker.entity.UserBlocked;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 8/2/15.
 */

/**
 * This class receives notification from GCM and persists it into the DB
 * <p/>
 * This {@code NotificationIntentService } does the actual handling of the GCM message.
 * {@code NotificationReceiver} holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */

public class NotificationIntentService extends IntentService {

    private Persistence persistence;

    public NotificationIntentService() {
        super("NotificationIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isUserBlocked;
        Bundle extras = intent.getExtras();
        String senderID = null;
        if (extras != null) {
            senderID = (getSenderID(extras.toString()));
        }
        isUserBlocked = checkIfUserBlocked(senderID);

        if (!isUserBlocked) {
            GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
            // The getMessageType() intent parameter must be the intent you received
            // in your BroadcastReceiver.

            String messageType = googleCloudMessaging.getMessageType(intent);
            if (!extras.isEmpty()) { // has effect of unparcelling Bundle

            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    //Do nothing
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                    //Do nothing
                }
                // If it's a regular GCM message, do some work.
                else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    String message = getMessageFromBundle(extras.toString());

                    Log.i(Constants.NOTIFICATION_SERVICE_TAG, "Received : " + extras.toString());
                    String time = getTime();
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    String timeInString = time + " " + sdf.format(date).toString();
                    saveMessageToDataBase(message, timeInString, new Date(),
                            getSenderID(extras.toString()));
                }
                // Release the wake lock provided by the WakefulBroadcastReceiver.
//                WakefulReceiver.completeWakefulIntent(intent);
            }
        }

    }

    /**
     * This method checks if the user who has sent the notification belongs to the blocklist.
     * returns true if it does.
     *
     * @param senderID
     * @return
     */
    private boolean checkIfUserBlocked(String senderID) {
        persistence = new Persistence();
        List<UserBlocked> userBlockedList =
                persistence.fetchListOfBlockedUsers(this);
        if (senderID != null && userBlockedList != null && userBlockedList.size() > 0) {
            Iterator<UserBlocked> userBlockedIterator = userBlockedList.iterator();
            while (userBlockedIterator.hasNext()) {
                if (userBlockedIterator.next().getUserID().equalsIgnoreCase(senderID)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getSenderID(String string) {
        if (string != null && string.contains("#")) {

            String senderID = "";
            int indexOfHash = string.indexOf("#");
            int indexOfPushHash = string.indexOf("push_hash");

            indexOfHash += 1;
            indexOfPushHash -= 3;
            //int size=indexOfAndroidSupport-indexOfMessage+1;
            for (int i = indexOfHash; i < indexOfPushHash; i++) {
                senderID += string.charAt(i);
            }
            return senderID;
        }
        return null;
    }

    private String getTime() {
        Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
        String s;
        if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
            s = "AM";
        } else {
            s = "PM";
        }

        String curTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE))
                + " " + s;
        return curTime;
    }

    private void saveMessageToDataBase(String message, String time, Date date, String userID) {

        if (persistence == null) {
            persistence = new Persistence();
        }
        Notification notification = new Notification(message, time, date, userID);
        persistence.saveNotificationInDatabase(this, notification);
    }

    private String getMessageFromBundle(String string) {

        String message = "";
        int indexOfAlert = string.indexOf("alert");
        int indexOfPushHash = string.indexOf("push_hash");

        indexOfAlert += 8;
        indexOfPushHash -= 3;
        //int size=indexOfAndroidSupport-indexOfMessage+1;
        for (int i = indexOfAlert; i < indexOfPushHash; i++) {
            if (string.charAt(i) == '#') {
                break;
            }
            message += string.charAt(i);
        }
        return message;
    }
}

