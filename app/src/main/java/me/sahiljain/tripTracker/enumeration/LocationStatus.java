package me.sahiljain.tripTracker.enumeration;

/**
 * Created by sahil on 8/5/15.
 * This Enum takes care of the Location status of the trip which is used in
 * me.sahiljain.tripTracker.notificationService.NotificationService
 * while sending notifications.
 */
public enum LocationStatus {

    SOURCE(), DESTINATION(), BETWEEN_SOURCE_AND_DESTINATION();

    /**
     * Returns a string containing a concise, human-readable description of this
     * object. In this case, the enum constant's name is returned.
     *
     * @return a printable representation of this object.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
