package me.sahiljain.tripTracker.db;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import me.sahiljain.tripTracker.entity.Notification;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserDefault;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 3/4/15.
 * This class takes care of persisting data into DB
 */
public class Persistence extends Activity {

    private DataBaseHelper dataBaseHelper;

    public void persistUserDefault(List<UserDefault> userDefaults) {
        dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        RuntimeExceptionDao<UserDefault, Integer> userDefaultIntegerRuntimeExceptionDao =
                dataBaseHelper.getUserDefaultRuntimeExceptionDao();

        //For each user --make entry  in Dao
        for (UserDefault userDefault : userDefaults) {
            //persist into DB
            userDefaultIntegerRuntimeExceptionDao.create(userDefault);
            Log.d(Constants.TAG, userDefault.toString());
        }

        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public List<UserDefault> fetchUserDefault(Context context) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        if (dataBaseHelper != null) {
            RuntimeExceptionDao<UserDefault, Integer> userDefaultIntegerRuntimeExceptionDao =
                    dataBaseHelper.getUserDefaultRuntimeExceptionDao();

            return userDefaultIntegerRuntimeExceptionDao.queryForAll();
        }
        return null;
    }

    public void saveTripInDataBase(Context context, Trip trip) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                dataBaseHelper.getTripRuntimeExceptionDao();

        //persist into DB
        tripRuntimeExceptionDao.create(trip);
        Log.d(Constants.TAG, trip.toString());

        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public List<Trip> fetchTrips(Context context) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        if (dataBaseHelper != null) {
            RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                    dataBaseHelper.getTripRuntimeExceptionDao();
            return tripRuntimeExceptionDao.queryForAll();
        }
        return null;
    }

    public void saveNotificationInDatabase(Context context, Notification notification) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        RuntimeExceptionDao<Notification, Integer> notificationRuntimeExceptionDao =
                dataBaseHelper.getNotificationRuntimeExceptionDao();

        //persist into DB
        notificationRuntimeExceptionDao.create(notification);
        Log.d(Constants.TAG, notification.toString());

        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public List<Notification> fetchNotifications(Context context) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        if (dataBaseHelper != null) {
            RuntimeExceptionDao<Notification, Integer> notificationRuntimeExceptionDao =
                    dataBaseHelper.getNotificationRuntimeExceptionDao();
            return notificationRuntimeExceptionDao.queryForAll();
        }
        return null;
    }

    /**
     * Deactivate all the trips--set isActive flag ==false on all tips except one trip .
     * and activate the trip that has the same trip ID as provided to this method as argument
     *
     * @param context
     * @param tripId
     */
    public void activateTrip(Context context, Integer tripId) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        if (dataBaseHelper != null) {
            RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                    dataBaseHelper.getTripRuntimeExceptionDao();
            List<Trip> trips = fetchTrips(context);
            for (Trip trip : trips) {
                trip.setActive(false);
                if (tripId.equals(trip.getTripId())) {
                    trip.setActive(true);
                }
                tripRuntimeExceptionDao.update(trip);
            }
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();

    }

    public void deleteTrip(Context context, Integer tripId) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        if (dataBaseHelper != null) {
            RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                    dataBaseHelper.getTripRuntimeExceptionDao();
            tripRuntimeExceptionDao.deleteById(tripId);
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    /**
     * This method return the active trip
     * there can be only one active trip against which location will be tracked in
     * me.sahiljain.tripTracker.notificationService.NotificationService
     * and notification will be send subsequently
     */
    public List<Trip> fetchActiveTrip(Context context) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        if (dataBaseHelper != null) {
            RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                    dataBaseHelper.getTripRuntimeExceptionDao();
            return tripRuntimeExceptionDao.queryForEq("active", true);
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
        return null;

    }
}