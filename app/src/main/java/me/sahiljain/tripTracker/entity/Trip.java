package me.sahiljain.tripTracker.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.Date;

import me.sahiljain.tripTracker.enumeration.LocationStatus;

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
     *
     * @PK
     */
    @DatabaseField(canBeNull = false, id = true)
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
     * Name of checkPoint 1
     */
    @DatabaseField
    private String checkPoint1Name;

    /**
     * Name of checkPoint 2
     */
    @DatabaseField
    private String checkPoint2Name;

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

    //Added checkpoint 1 and 2 in version 5
    /**
     * Latitude of CheckPoint1
     */
    @DatabaseField
    private Float latCheckPoint1;

    /**
     * Latitude of CheckPoint2
     */
    @DatabaseField
    private Float latCheckPoint2;
    /**
     * Longitude of CheckPoint1
     */
    @DatabaseField
    private Float longCheckPoint1;
    /**
     * Longitude of CheckPoint2
     */
    @DatabaseField
    private Float longCheckPoint2;

    //For checkpoints Time Stamp logic doesn't make much sense
    //So we use boolean flags
    /**
     * Indicates whether user is at checkpoint 1
     */
    @DatabaseField
    private boolean checkPoint1Flag = false;

    /**
     * Indicates whether user is at checkpoint 2
     */
    @DatabaseField
    private boolean checkPoint2Flag = false;

    /**
     * List of friends for this trip who must be informed
     * through push notifications.
     * One to many here is not exactly working as per ORM--it's kind of hard coded
     */
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

    /**
     * Indicates the current status of Trip.
     * This will be updated in the class NotificationService every time a notification is sent
     */
    @DatabaseField
    private LocationStatus locationStatus = LocationStatus.SOURCE;

    /**
     * Indicates the time when a notification was sent while leaving/entering source.
     * Notifications are sent only is time gap is >15 min
     */
    @DatabaseField
    private Date sourceTimeStamp = null;

    /**
     * Indicates the time when a notification was sent while leaving/entering destination.
     * Notifications are sent only is time gap is >15 min
     */
    @DatabaseField
    private Date destinationTimeStamp = null;

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

    public LocationStatus getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(LocationStatus locationStatus) {
        this.locationStatus = locationStatus;
    }

    public Date getSourceTimeStamp() {
        return sourceTimeStamp;
    }

    public void setSourceTimeStamp(Date sourceTimeStamp) {
        this.sourceTimeStamp = sourceTimeStamp;
    }

    public Date getDestinationTimeStamp() {
        return destinationTimeStamp;
    }

    public void setDestinationTimeStamp(Date destinationTimeStamp) {
        this.destinationTimeStamp = destinationTimeStamp;
    }

    public Float getLatCheckPoint1() {
        return latCheckPoint1;
    }

    public void setLatCheckPoint1(Float latCheckPoint1) {
        this.latCheckPoint1 = latCheckPoint1;
    }

    public Float getLatCheckPoint2() {
        return latCheckPoint2;
    }

    public void setLatCheckPoint2(Float latCheckPoint2) {
        this.latCheckPoint2 = latCheckPoint2;
    }

    public Float getLongCheckPoint1() {
        return longCheckPoint1;
    }

    public void setLongCheckPoint1(Float longCheckPoint1) {
        this.longCheckPoint1 = longCheckPoint1;
    }

    public Float getLongCheckPoint2() {
        return longCheckPoint2;
    }

    public void setLongCheckPoint2(Float longCheckPoint2) {
        this.longCheckPoint2 = longCheckPoint2;
    }

    public boolean isCheckPoint1Flag() {
        return checkPoint1Flag;
    }

    public void setCheckPoint1Flag(boolean checkPoint1Flag) {
        this.checkPoint1Flag = checkPoint1Flag;
    }

    public boolean isCheckPoint2Flag() {
        return checkPoint2Flag;
    }

    public void setCheckPoint2Flag(boolean checkPoint2Flag) {
        this.checkPoint2Flag = checkPoint2Flag;
    }

    public String getCheckPoint1Name() {
        return checkPoint1Name;
    }

    public void setCheckPoint1Name(String checkPoint1Name) {
        this.checkPoint1Name = checkPoint1Name;
    }

    public String getCheckPoint2Name() {
        return checkPoint2Name;
    }

    public void setCheckPoint2Name(String checkPoint2Name) {
        this.checkPoint2Name = checkPoint2Name;
    }
}
