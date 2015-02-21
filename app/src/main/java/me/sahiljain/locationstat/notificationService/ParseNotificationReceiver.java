package me.sahiljain.locationstat.notificationService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.parse.ParsePushBroadcastReceiver;

import me.sahiljain.locationstat.windows.Notification;

/**
 * This class is created because we wanted to override the method
 * getActivity()--so that we could open NotificationWindow class on
 * click of Notification
 * Created by sahil on 19/2/15.
 */
public class ParseNotificationReceiver extends ParsePushBroadcastReceiver {

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return Notification.class;
    }

    public ParseNotificationReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return super.getSmallIconId(context, intent);
    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        return super.getLargeIcon(context, intent);
    }

    @Override
    protected android.app.Notification getNotification(Context context, Intent intent) {
        return super.getNotification(context, intent);
    }
}
