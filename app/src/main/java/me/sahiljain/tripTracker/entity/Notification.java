package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by sahil on 15/3/15.
 */
@DatabaseTable(tableName = "tt_notifications_all")
public class Notification {
    @DatabaseField(generatedId = true)
    private long notificationId;

    /**
     * Message contained in a notification
     */
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
    private Date date;

    /**
     * Denotes the senderID of the user who sent this notification
     * this is useful for blocking a user
     */
    @DatabaseField
    private String senderID;


    public Notification() {
    }

    public Notification(String message, String time, Date date, String senderID) {
        this.message = message;
        this.time = time;
        this.date = date;
        this.senderID = senderID;
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

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
}
