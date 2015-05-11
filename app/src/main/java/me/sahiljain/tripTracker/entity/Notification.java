package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

/**
 * Created by sahil on 15/3/15.
 */
@DatabaseTable(tableName = "tt_notifications_all")
public class Notification {
    @DatabaseField(generatedId = true)
    private long notificationId;

    @DatabaseField
    private String message;

    /**
     * This is a regular string that is shown in the notifications list view
     */
    @DatabaseField
    private String time;

    /**
     * This is the actual date that is used to sort messages
     */
    @DatabaseField
    private DateTime dateTime;


    public Notification() {
    }

    public Notification(String message, String time, DateTime dateTime) {
        this.message = message;
        this.time = time;
        this.dateTime = dateTime;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }
}
