package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by sahil on 15/3/15.
 */
@DatabaseTable(tableName = "tt_notifications_all")
public class Notification {
    @DatabaseField(generatedId = true)
    private long notificationId;

    @DatabaseField
    private String message;

    @DatabaseField
    private String time;

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public Notification() {
    }

    public Notification(String message, String time) {

        this.message = message;
        this.time = time;
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

}
