package me.sahiljain.tripTracker.db;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import me.sahiljain.tripTracker.entity.Notification;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserBlocked;
import me.sahiljain.tripTracker.entity.UserDefault;
import me.sahiljain.tripTracker.entity.UserTrip;
import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 3/4/15.
 * This class takes care of persisting data into DB
 */
public class Persistence extends Activity {

    private DataBaseHelper dataBaseHelper;

    public void persistUserDefault(List<UserDefault> userDefaults) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
            RuntimeExceptionDao<UserDefault, Integer> userDefaultIntegerRuntimeExceptionDao =
                    dataBaseHelper.getUserDefaultRuntimeExceptionDao();

            //For each user --make entry  in Dao
            for (UserDefault userDefault : userDefaults) {
                //persist into DB
                userDefaultIntegerRuntimeExceptionDao.create(userDefault);
                Log.d(Constants.TAG, userDefault.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public List<UserDefault> fetchUserDefault(Context context) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            if (dataBaseHelper != null) {
                RuntimeExceptionDao<UserDefault, Integer> userDefaultIntegerRuntimeExceptionDao =
                        dataBaseHelper.getUserDefaultRuntimeExceptionDao();

                return userDefaultIntegerRuntimeExceptionDao.queryForAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
        return null;
    }

    public void saveTripInDataBase(Context context, Trip trip) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                    dataBaseHelper.getTripRuntimeExceptionDao();

            //persist Trip into DB
            tripRuntimeExceptionDao.create(trip);
            Log.d(Constants.TAG, trip.toString());

            //persist Users in DB
            saveUserInDB(context, trip.getFriendList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    private void saveUserInDB(Context context, Collection<UserTrip> friendList) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            RuntimeExceptionDao<UserTrip, Integer> tripRuntimeExceptionDao =
                    dataBaseHelper.getUserTripRuntimeExceptionDao();
            for (UserTrip userTrip : friendList) {
                tripRuntimeExceptionDao.create(userTrip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public List<Trip> fetchTrips(Context context) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            List<Trip> trips;
            if (dataBaseHelper != null) {
                RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                        dataBaseHelper.getTripRuntimeExceptionDao();
                trips = tripRuntimeExceptionDao.queryForAll();

                // Put Set of Trip Users in individual trip

                RuntimeExceptionDao<UserTrip, Integer> userTripDAO =
                        dataBaseHelper.getUserTripRuntimeExceptionDao();

                for (Trip trip : trips) {
                    trip.setFriendList(userTripDAO.queryForEq("tripId", trip.getTripId()));
                }
                return trips;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveNotificationInDatabase(Context context, Notification notification) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            RuntimeExceptionDao<Notification, Integer> notificationRuntimeExceptionDao =
                    dataBaseHelper.getNotificationRuntimeExceptionDao();

            //persist into DB
            notificationRuntimeExceptionDao.create(notification);
            Log.d(Constants.TAG, notification.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public List<Notification> fetchNotifications(Context context) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            PreparedQuery<Notification> preparedQuery = null;
            if (dataBaseHelper != null) {
                RuntimeExceptionDao<Notification, Integer> notificationRuntimeExceptionDao =
                        dataBaseHelper.getNotificationRuntimeExceptionDao();
                QueryBuilder<Notification, Integer> queryBuilder = notificationRuntimeExceptionDao.queryBuilder();
                queryBuilder.orderBy(Constants.DATE, false);
                try {
                    preparedQuery = queryBuilder.prepare();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return notificationRuntimeExceptionDao.query(preparedQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public void deleteTrip(Context context, Integer tripId) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            if (dataBaseHelper != null) {
                RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                        dataBaseHelper.getTripRuntimeExceptionDao();
                tripRuntimeExceptionDao.deleteById(tripId);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        List<Trip> trips = null;
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            if (dataBaseHelper != null) {
                RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                        dataBaseHelper.getTripRuntimeExceptionDao();
                trips = tripRuntimeExceptionDao.queryForEq("active", true);
                /**
                 * Put Set of Trip Users in individual trip
                 */
                RuntimeExceptionDao<UserTrip, Integer> userTripDAO =
                        dataBaseHelper.getUserTripRuntimeExceptionDao();

                for (Trip trip : trips) {
                    trip.setFriendList(userTripDAO.queryForEq("tripId", trip.getTripId()));
                }
                return trips;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
        return trips;

    }

    public void updateTrip(Context context, Trip trip) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao = null;
            if (dataBaseHelper != null) {
                tripRuntimeExceptionDao =
                        dataBaseHelper.getTripRuntimeExceptionDao();
            }
            if (tripRuntimeExceptionDao != null) {
                tripRuntimeExceptionDao.update(trip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();
    }

    public List<UserBlocked> fetchListOfBlockedUsers(Context context) {

        List<UserBlocked> list = null;
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            RuntimeExceptionDao<UserBlocked, Integer> userBlockedRuntimeExceptionDao =
                    dataBaseHelper.getUserBlockedRuntimeExceptionDao();
            list = userBlockedRuntimeExceptionDao.queryForAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void blockUser(Context context, UserBlocked userBlocked) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        try {
            RuntimeExceptionDao<UserBlocked, Integer> userBlockedRuntimeExceptionDao =
                    dataBaseHelper.getUserBlockedRuntimeExceptionDao();
            userBlockedRuntimeExceptionDao.create(userBlocked);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
    }

    public void unblockUser(Context context, UserBlocked userBlocked) {
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        try {
            RuntimeExceptionDao<UserBlocked, Integer> userBlockedRuntimeExceptionDao =
                    dataBaseHelper.getUserBlockedRuntimeExceptionDao();
            userBlockedRuntimeExceptionDao.delete(userBlocked);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
    }

    public Trip fetchTripById(Context context, int tripId) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            Trip trip;
            if (dataBaseHelper != null) {
                RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao =
                        dataBaseHelper.getTripRuntimeExceptionDao();
                trip = tripRuntimeExceptionDao.queryForId(tripId);

                // Put Set of Trip Users in individual trip

                RuntimeExceptionDao<UserTrip, Integer> userTripDAO =
                        dataBaseHelper.getUserTripRuntimeExceptionDao();

                trip.setFriendList(userTripDAO.queryForEq("tripId", trip.getTripId()));
                return trip;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearAllNotifications(Context context) {
        try {
            dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
            if (dataBaseHelper != null) {
                RuntimeExceptionDao<Notification, Integer> notificationRuntimeExceptionDao =
                        dataBaseHelper.getNotificationRuntimeExceptionDao();
                List<Notification> allNotifications = notificationRuntimeExceptionDao.queryForAll();
                notificationRuntimeExceptionDao.delete(allNotifications);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Release helper after using
        OpenHelperManager.releaseHelper();

    }
}
