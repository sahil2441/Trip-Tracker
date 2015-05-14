package me.sahiljain.tripTracker.notificationService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

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
 */


public class NotificationReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that NotificationIntentService will handle the intent.
        ComponentName componentName = new ComponentName(context.getPackageName(),
                NotificationIntentService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, intent.setComponent(componentName));
        setResultCode(Activity.RESULT_OK);

        /**
         * For Notification Service
         * This ensures that service resumes on restarting the phone
         */
        Intent startNotificationServiceIntent = new Intent(context, NotificationService.class);
        context.startService(startNotificationServiceIntent);
    }
}