package me.sahiljain.locationstat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;

/**
 * Created by sahil on 8/2/15.
 */

/**
 * This class receives notification from GCM and pops onto the device.
 * <p/>
 * This {@code NotificationIntentService } does the actual handling of the GCM message.
 * {@code NotificationReceiver} holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */

public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("NotificationIntentService");

    }

    public static final String TAG = "Notification Intent Service";

    private NotificationManager notificationManager;

    public static final int NOTIFICATION_ID = 1;

    private static final String NOTIFICATIONS_SHARED_PREFERENCES = "Notifications_SP";
    private static final String NOTIFICATIONS_SIZE = "Notifications_Size";


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
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
                sendNotification("Send Error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server " + extras.toString());
            }
            // If it's a regular GCM message, do some work.
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String message = getMessageFromBundle(extras.toString());
                /**
                 * Set contents in notification list view using adapter
                 */
                ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add(message);
//                sendNotification(message);
                Log.i(TAG, "Received : " + extras.toString());
                saveMessageToSharedPreferences(message);


            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            NotificationReceiver.completeWakefulIntent(intent);
        }

    }

    private void saveMessageToSharedPreferences(String message) {
        SharedPreferences preferences = getSharedPreferences(NOTIFICATIONS_SHARED_PREFERENCES, MODE_PRIVATE);
        int size = preferences.getInt(NOTIFICATIONS_SIZE, 0);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("i" + size, message);
        size++;
        editor.putInt(NOTIFICATIONS_SIZE, size);
        editor.commit();
    }

    private String getMessageFromBundle(String string) {

        String message = "";
        int indexOfAlert = string.indexOf("alert");
        int indexOfPushHash = string.indexOf("push_hash");

        indexOfAlert += 8;
        indexOfPushHash -= 3;
        //int size=indexOfAndroidSupport-indexOfMessage+1;
        for (int i = indexOfAlert; i < indexOfPushHash; i++) {
            message += string.charAt(i);
        }
        return message;
    }
    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.

    private void sendNotification(String message) {
        notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.
                getActivity(this, 0, new Intent(this, NotificationWindow.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).
                setSmallIcon(R.drawable.homeiconsmall)
                .setContentTitle("Location Stat")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
