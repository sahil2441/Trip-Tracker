package me.sahiljain.locationstat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by sahil on 8/2/15.
 */

/**
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
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working......" + i + "/5 " + SystemClock.elapsedRealtime());
                }
                String message = getMessageFromBundle(extras.toString());
                sendNotification(message);
                Log.i(TAG, "Received : " + extras.toString());
            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            NotificationReceiver.completeWakefulIntent(intent);
        }
    }

    private String getMessageFromBundle(String string) {

        String message = "";
        int indexOfMessage = string.indexOf("message");
        int indexOfAndroidSupport = string.indexOf("android.support.content.wakelockid");

        indexOfMessage += 8;
        indexOfAndroidSupport -= 2;
        //int size=indexOfAndroidSupport-indexOfMessage+1;
        for (int i = indexOfMessage; i < indexOfAndroidSupport; i++) {
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
                getActivity(this, 0, new Intent(this, MapsActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).
                setSmallIcon(R.drawable.homeiconsmall)
                .setContentTitle("Location Stat Notification")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
