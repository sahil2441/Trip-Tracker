package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by sahil on 3/4/15.
 * This class is different from UserTrip in the sense that it doesn't has tipId field
 * and hence these users are not mapped to any trip.
 * They just denote the total number of users in the contact list who also have an account with
 * trip Tracker
 */
@DatabaseTable(tableName = "tt_user_default_all")
public class UserDefault implements IUser {
    /**
     * The first and last name of the user
     */
    @DatabaseField
    private String name;

    /**
     * This is the mobile number with country code
     * A unique identifier for each user
     *
     * @PK
     */
    @DatabaseField(canBeNull = false)
    private String userID;

    public UserDefault() {
    }

    public UserDefault(String name, String userID) {
        this.name = name;
        this.userID = userID;
    }

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

    @Override
    public String toString() {
        return "UserDefault{" +
                "name='" + name + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }

}
