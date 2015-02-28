package me.sahiljain.locationstat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahil on 28/2/15.
 */
public class DataBaseNotifications extends SQLiteOpenHelper {

    public static final String DB_NAME = "notificationsDB";
    public static final String TABLE_LIST_OF_FRIENDS = "TABLE_NOTIFICATIONS";
    public static final String NOTIFICATIONS = "FRIEND_NAME";
    public static final String TIME_STAMP = "TIME_STAMP";

    public DataBaseNotifications(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_LIST_OF_FRIENDS + " (" + NOTIFICATIONS +
                " TEXT PRIMARY KEY, " + TIME_STAMP + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_OF_FRIENDS);
        onCreate(db);
    }

    public void insert(String notification, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATIONS, notification);
        contentValues.put(TIME_STAMP, time);
        db.insert(TABLE_LIST_OF_FRIENDS, null, contentValues);
        db.close();

    }

    public List<String> fetchListNotifications() {
        List<String> list = new ArrayList<String>();

        String query = "Select " + NOTIFICATIONS + " from " + TABLE_LIST_OF_FRIENDS;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(NOTIFICATIONS)));
            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public List<String> fetchListTime() {
        List<String> list = new ArrayList<String>();

        String query = "Select " + TIME_STAMP + " from " + TABLE_LIST_OF_FRIENDS;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(TIME_STAMP)));
            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }
}
