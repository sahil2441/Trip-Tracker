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
public class DataBaseFriends extends SQLiteOpenHelper {

    public static final String DB_NAME = "friendDB";
    public static final String TABLE_LIST_OF_FRIENDS = "TABLE_LIST_OF_FRIENDS";
    public static final String FRIEND_NAME = "FRIEND_NAME";
    public static final String USER_NAME = "USER_NAME";

    public DataBaseFriends(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LIST_OF_FRIENDS + "(" + FRIEND_NAME + " TEXT ," + USER_NAME +
                " TEXT PRIMARY KEY)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_OF_FRIENDS);
        onCreate(db);
    }

    public boolean insert(String userName, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FRIEND_NAME, name);
        contentValues.put(USER_NAME, userName);
        db.insert(TABLE_LIST_OF_FRIENDS, null, contentValues);
        db.close();

        return true;
    }

    public List<String> fetchData() {
        List<String> list = new ArrayList<String>();
        String query = "Select " + FRIEND_NAME + " from " + TABLE_LIST_OF_FRIENDS;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(FRIEND_NAME)));
            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public List<String> fetchChannels() {
        List<String> list = new ArrayList<String>();
        String query = "Select " + USER_NAME + " from " + TABLE_LIST_OF_FRIENDS;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(cursor.getColumnIndex(USER_NAME)));
            } while (cursor.moveToNext());
        }
        database.close();
        return list;
    }

    public boolean deleteData() {
        return true;
    }
}
