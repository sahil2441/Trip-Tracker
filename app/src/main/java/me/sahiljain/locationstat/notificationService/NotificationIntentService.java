package me.sahiljain.locationstat.notificationService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import me.sahiljain.locationstat.R;
import me.sahiljain.locationstat.db.DataBaseNotifications;
import me.sahiljain.tripTracker.main.Constants;
import me.sahiljain.locationstat.windows.Notification;

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

    private DataBaseNotifications dataBaseNotifications;

    public NotificationIntentService() {
        super("NotificationIntentService");

    }

    private NotificationManager notificationManager;

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
                Log.i(Constants.NOTIFICATION_SERVICE_TAG, "Received : " + extras.toString());
                String time = getTime();
                saveMessageToDataBase(message, time);
            }
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            NotificationReceiver.completeWakefulIntent(intent);
        }
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

    private void saveMessageToDataBase(String message, String time) {
        dataBaseNotifications = new DataBaseNotifications(this);
        dataBaseNotifications.insert(message, time);
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
                getActivity(this, 0, new Intent(this, Notification.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).
                setSmallIcon(R.drawable.source_icon_small)
                .setContentTitle("Location Stat")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }
}
