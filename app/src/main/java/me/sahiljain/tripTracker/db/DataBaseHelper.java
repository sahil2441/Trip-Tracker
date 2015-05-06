package me.sahiljain.tripTracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import me.sahiljain.tripTracker.R;
import me.sahiljain.tripTracker.entity.Notification;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserDefault;
import me.sahiljain.tripTracker.entity.UserTrip;
import me.sahiljain.tripTracker.entity.Week;

/**
 * Created by sahil on 3/4/15.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tripTracker.db";

    private static final int DATABASE_VERSION = 1;

    private Dao<Trip, Integer> tripDao = null;
    private Dao<UserDefault, Integer> userDefaultDao = null;
    private Dao<UserTrip, Integer> userTripDao = null;
    private Dao<Notification, Integer> notificationDao = null;
    private Dao<Week, Integer> weekDao = null;

    private RuntimeExceptionDao<Trip, Integer> tripRuntimeExceptionDao = null;
    private RuntimeExceptionDao<UserDefault, Integer> userDefaultRuntimeExceptionDao = null;
    private RuntimeExceptionDao<UserTrip, Integer> userTripRuntimeExceptionDao = null;
    private RuntimeExceptionDao<Notification, Integer> notificationRuntimeExceptionDao = null;
    private RuntimeExceptionDao<Week, Integer> weekRuntimeExceptionDao = null;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Trip.class);
            TableUtils.createTableIfNotExists(connectionSource, Notification.class);
            TableUtils.createTableIfNotExists(connectionSource, UserDefault.class);
            TableUtils.createTableIfNotExists(connectionSource, UserTrip.class);
            TableUtils.createTableIfNotExists(connectionSource, Week.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, Trip.class, true);
            TableUtils.dropTable(connectionSource, Notification.class, true);
            TableUtils.dropTable(connectionSource, UserDefault.class, true);
            TableUtils.dropTable(connectionSource, UserTrip.class, true);
            TableUtils.dropTable(connectionSource, Week.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Trip, Integer> getTripDao() throws SQLException {
        if (tripDao == null) {
            tripDao = getDao(Trip.class);
        }
        return tripDao;
    }

    public RuntimeExceptionDao<Trip, Integer> getTripRuntimeExceptionDao() {
        if (tripRuntimeExceptionDao == null) {
            tripRuntimeExceptionDao = getRuntimeExceptionDao(Trip.class);
        }
        return tripRuntimeExceptionDao;
    }

    public Dao<UserDefault, Integer> getUserDefaultDao() throws SQLException {
        if (userDefaultDao == null) {
            userDefaultDao = getDao(UserDefault.class);
        }
        return userDefaultDao;
    }

    public Dao<UserTrip, Integer> getUserTripDao() throws SQLException {

        if (userTripDao == null) {
            userTripDao = getDao(UserTrip.class);
        }
        return userTripDao;
    }

    public Dao<Notification, Integer> getNotificationDao() throws SQLException {
        if (notificationDao == null) {
            notificationDao = getDao(Notification.class);
        }
        return notificationDao;
    }

    public Dao<Week, Integer> getWeekDao() throws SQLException {
        if (weekDao == null) {
            weekDao = getDao(Week.class);
        }
        return weekDao;
    }

    public RuntimeExceptionDao<UserDefault, Integer> getUserDefaultRuntimeExceptionDao() {
        if (userDefaultRuntimeExceptionDao == null) {
            userDefaultRuntimeExceptionDao = getRuntimeExceptionDao(UserDefault.class);
        }
        return userDefaultRuntimeExceptionDao;
    }

    public RuntimeExceptionDao<UserTrip, Integer> getUserTripRuntimeExceptionDao() {
        if (userTripRuntimeExceptionDao == null) {
            userTripRuntimeExceptionDao = getRuntimeExceptionDao(UserTrip.class);
        }
        return userTripRuntimeExceptionDao;
    }

    public RuntimeExceptionDao<Notification, Integer> getNotificationRuntimeExceptionDao() {
        if (notificationRuntimeExceptionDao == null) {
            notificationRuntimeExceptionDao = getRuntimeExceptionDao(Notification.class);
        }
        return notificationRuntimeExceptionDao;
    }

    public RuntimeExceptionDao<Week, Integer> getWeekRuntimeExceptionDao() {
        if (weekRuntimeExceptionDao == null) {
            weekRuntimeExceptionDao = getRuntimeExceptionDao(Week.class);
        }
        return weekRuntimeExceptionDao;
    }
}
