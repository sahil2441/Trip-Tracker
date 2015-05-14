package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by sahil on 15/5/15.
 * The corresponding table contains the list of blocked users maintained.
 * Notifications send by these users will not be persisted in DB.
 */

@DatabaseTable(tableName = "TT_USER_BLOCKED_ALL")
public class UserBlocked implements IUser {

    @DatabaseField(id = true, canBeNull = false)
    private String userID;

    @DatabaseField
    private String name;

    @Override
    public String getUserID() {
        return userID;
    }

    @Override
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
