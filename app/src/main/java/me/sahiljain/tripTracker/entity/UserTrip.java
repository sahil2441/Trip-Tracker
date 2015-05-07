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

    public UserTrip(String userID, Integer tripId) {
        this.userID = userID;
        this.tripId = tripId;
    }

    /**
     * Primary Key of any User--a composite key which is a combination of
     * userID and TripID
     */
    @DatabaseField(id = true, useGetSet = true)
    private String id;

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
     * One to many here is not exactly working as per ORM--it's kind of hard coded
     */
    @DatabaseField(canBeNull = false)
    private Integer tripId;

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

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getId() {
        return this.userID + "-" + this.getTripId();
    }

    public void setId(String id) {
        this.id = id;
    }
}
