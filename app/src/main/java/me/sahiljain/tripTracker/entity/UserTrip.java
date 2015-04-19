package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by sahil on 15/3/15.
 */
@DatabaseTable(tableName = "tt_user_trip_all")
public class UserTrip implements IUser {

    public UserTrip() {
    }

    public UserTrip(String name, String userID) {
        this.userID = userID;
        this.name = name;
    }

    /**
     * The first and last name of the user
     */
    @DatabaseField
    private String name;

    /**
     * This is the mobile number with country code
     * A unique identifier for each user
     */
    @DatabaseField(canBeNull = false)
    private String userID;

    /**
     * To map a trip with one to many relationship
     * "Parent in Child class is defined as parent_id in Child Table. That is point.
     * To access using DAO, you should use parent_id"
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Trip trip;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
