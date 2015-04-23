package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by sahil on 15/3/15.
 */
@DatabaseTable(tableName = "tt_trips_all")
public class Trip {

    public Trip() {
    }

    /**
     * Unique Id of every Trip
     * Primary Key
     * @PK
     */
    @DatabaseField(canBeNull = false, generatedId = true)
    private Integer tripId;

    /**
     * Holds the name of the trip
     */
    @DatabaseField
    private String tripName;

    /**
     * Name of source
     */
    @DatabaseField
    private String sourceName;

    /**
     * Name of destination
     */
    @DatabaseField
    private String destinationName;

    /**
     * Latitude of the source
     */
    @DatabaseField
    private Float latSource;

    /**
     * Longitude of the source
     */
    @DatabaseField
    private Float longSource;

    /**
     * Latitude of the destination
     */
    @DatabaseField
    private Float latDestination;

    /**
     * Longitude of the destination
     */
    @DatabaseField
    private Float longDestination;

    /**
     * List of friends for this trip who must be informed
     * through push notifications
     */
    @ForeignCollectionField(eager = true)
    private Collection<UserTrip> friendList;

    /**
     * Whether the notification should be sent for reverse trip also
     */
    @DatabaseField
    private Boolean toAndFro;

    /**
     * If true, it means that the trip is just a one time trip and not a recurring one.
     *
     * @return
     */

    @DatabaseField
    private Boolean oneTimeTrip;

    /**
     * One time trips must be deactivated after the destination notification has been
     * served.
     * The flag will be checked again and again before sending notifications
     */
    @DatabaseField
    private Boolean active;

    /**
     * Details of week
     */
    private Week week;

    public Week getWeek() {
        return week;
    }

    public void setWeek(Week week) {
        this.week = week;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public Boolean getToAndFro() {
        return toAndFro;
    }

    public void setToAndFro(Boolean toAndFro) {
        this.toAndFro = toAndFro;
    }

    public Boolean getOneTimeTrip() {
        return oneTimeTrip;
    }

    public void setOneTimeTrip(Boolean oneTimeTrip) {
        this.oneTimeTrip = oneTimeTrip;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Float getLatSource() {
        return latSource;
    }

    public void setLatSource(Float latSource) {
        this.latSource = latSource;
    }

    public Float getLongSource() {
        return longSource;
    }

    public void setLongSource(Float longSource) {
        this.longSource = longSource;
    }

    public Float getLatDestination() {
        return latDestination;
    }

    public void setLatDestination(Float latDestination) {
        this.latDestination = latDestination;
    }

    public Float getLongDestination() {
        return longDestination;
    }

    public void setLongDestination(Float longDestination) {
        this.longDestination = longDestination;
    }

    public Collection<UserTrip> getFriendList() {
        return friendList;
    }

    public void setFriendList(Collection<UserTrip> friendList) {
        this.friendList = friendList;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }
}
