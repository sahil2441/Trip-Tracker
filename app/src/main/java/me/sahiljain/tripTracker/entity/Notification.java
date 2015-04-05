package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by sahil on 15/3/15.
 */
@DatabaseTable(tableName = "tt_notifications_all")
public class Notification {

    public Notification() {
    }

    public Notification(String notificationId, String message, Date date, long time) {
        this.message = message;
        this.date = date;
        this.time = time;
    }

    @DatabaseField(canBeNull = false, generatedId = true)
    private Integer notificationId;

    @DatabaseField
    private String message;

    @DatabaseField
    private Date date;

    @DatabaseField
    private long time;

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
