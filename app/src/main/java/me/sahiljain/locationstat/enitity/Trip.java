package me.sahiljain.locationstat.enitity;

import android.location.Location;

import java.util.List;

import me.sahiljain.locationstat.objects.Week;

/**
 * Created by sahil on 15/3/15.
 */
public class Trip {

    public Trip() {
    }

    /**
     * Holds the name of the trip
     */
    String tripName;

    /**
     * Location of the source
     */
    Location source;

    /**
     * Location of the destination
     */
    Location destination;


    /**
     * List of friends for this trip who must be informed
     * through push notifications
     */
    List<User> friendList;

    /**
     * Whether the notification should be sent for reverse trip also
     */
    Boolean toAndFro;

    /**
     * Contains seven boolean flags--one for each day
     * Determines whether the notification should be sent on that particular day
     */
    Week weekDetails;

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public Location getSource() {
        return source;
    }

    public void setSource(Location source) {
        this.source = source;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public List<User> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<User> friendList) {
        this.friendList = friendList;
    }

    public Boolean getToAndFro() {
        return toAndFro;
    }

    public void setToAndFro(Boolean toAndFro) {
        this.toAndFro = toAndFro;
    }

    public Week getWeekDetails() {
        return weekDetails;
    }

    public void setWeekDetails(Week weekDetails) {
        this.weekDetails = weekDetails;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripName='" + tripName + '\'' +
                ", source=" + source +
                ", destination=" + destination +
                ", friendList=" + friendList +
                ", toAndFro=" + toAndFro +
                ", weekDetails=" + weekDetails +
                '}';
    }
}
