package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by sahil on 15/3/15.
 */
@DatabaseTable(tableName = "tt_week_trip")
public class Week {

    public Week() {
    }

    /**
     * @PK
     */
    @DatabaseField(canBeNull = false, generatedId = true)
    private Integer weekId;

    @DatabaseField
    private Boolean monday;

    @DatabaseField
    private Boolean tuesday;

    @DatabaseField
    private Boolean wednesday;

    @DatabaseField
    private Boolean thursday;

    @DatabaseField
    private Boolean friday;

    @DatabaseField
    private Boolean saturday;

    @DatabaseField
    private Boolean sunday;

    /**
     * To map a trip with one-one relationship
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Trip trip;

    public Boolean getSunday() {
        return sunday;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Integer getWeekId() {
        return weekId;
    }

    public void setWeekId(Integer weekId) {
        this.weekId = weekId;
    }

    @Override
    public String toString() {
        return "Week{" +
                "weekId=" + weekId +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                ", trip=" + trip +
                '}';
    }
}
