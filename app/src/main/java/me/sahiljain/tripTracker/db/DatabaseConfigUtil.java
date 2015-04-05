package me.sahiljain.tripTracker.db;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import me.sahiljain.tripTracker.entity.Notification;
import me.sahiljain.tripTracker.entity.Trip;
import me.sahiljain.tripTracker.entity.UserDefault;
import me.sahiljain.tripTracker.entity.UserTrip;
import me.sahiljain.tripTracker.entity.Week;

/**
 * Created by sahil on 3/4/15.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class[] classes = new Class[]{Trip.class, Notification.class, UserDefault.class
            , UserTrip.class, Week.class};

    public static void main(String[] args) throws IOException, SQLException {

        writeConfigFile(new File("/home/sahil/AndroidStudioProjects/locationstat/app/src/main/res/raw/ormlite_config.txt"), classes);
    }
}
