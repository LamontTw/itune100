package com.lamont.demo.itunetop100.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lamont.demo.itunetop100.database.model.ITuneItem;

import java.util.ArrayList;
import java.util.List;


public class Top100Table {
    private static final String TAG = "Top100Table";

    //Table Name
    private static final String TABLE_NAME = "Top100";

    //Table Columns
    private static final String COLUMN_KEY_ID = "_id"; //Table Serial Number. It must be _id

    private static final String COLUMN_RANK = "rank"; //String
    private static final String COLUMN_ART_WORK_URL = "artworkUrl100"; //String
    private static final String COLUMN_SONG_NAME = "songName"; //int
    private static final String COLUMN_ARTIST_NAME = "artistName"; //String

    //Table Column Index
    private static final int COLUMN_INDEX_KEY_ID = 0;
    private static final int COLUMN_INDEX_RANK = 1;
    private static final int COLUMN_INDEX_ART_WORK_URL  = 2;
    private static final int COLUMN_INDEX_SONG_NAME = 3;
    private static final int COLUMN_INDEX_ARTIST_NAME = 4;

    private SQLiteDatabase mDB;

    public synchronized static boolean createTable(SQLiteDatabase db) {
        Log.d(TAG, "createTable()");
        if (db == null || !db.isOpen()) {
            Log.e(TAG, "db is null or closed");
            return false;
        }

        boolean ret = false;
        try {
            //INTEGER – 整數，對應Java 的byte、short、int 和long。
            //REAL – 小數，對應Java 的float 和double。
            //TEXT – 字串，對應Java 的String。
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_RANK + " INTEGER, " +
                    COLUMN_ART_WORK_URL + " TEXT NOT NULL, " +
                    COLUMN_SONG_NAME + " TEXT NOT NULL, " +
                    COLUMN_ARTIST_NAME + " TEXT NOT NULL)");
            ret = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "createTable() ret : " + ret);
        return ret;
    }

    public synchronized static boolean deleteTable(SQLiteDatabase db) {
        Log.d(TAG, "deleteTable()");
        if (db == null || !db.isOpen()) {
            Log.e(TAG, "db is null or closed");
            return false;
        }

        boolean ret = false;
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            ret = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Top100Table(Context context) {
        Log.i(TAG, "Top100Table: getDatabase");
        mDB = iTuneDBHelper.getDatabase(context);
        Log.d(TAG, "mDB : " + mDB);
    }

    public synchronized ITuneItem insert(ITuneItem item) {
        Log.d(TAG, "insert(ITuneItem)");
        if (mDB == null || !mDB.isOpen()) {
            Log.e(TAG, "db is null or closed");
            return null;
        } else if (item == null) {
            Log.e(TAG, "item should not be null");
            return null;
        }

        try {
            long id = mDB.insert(TABLE_NAME, null, getContentValues(item));
            item.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public synchronized List<ITuneItem> insert(List<ITuneItem> itemList) {
        Log.d(TAG, "insert(List<ITuneItem>)");
        if (mDB == null || !mDB.isOpen()) {
            Log.e(TAG, "db is null or closed");
            return null;
        } else if (itemList == null) {
            Log.e(TAG, "itemList should not be null");
            return null;
        }

        if (itemList.size() > 0) {
            Log.d(TAG, "insert(): count = " + itemList.size());
            try {
                mDB.beginTransaction();

                for (int i = 0, count = itemList.size(); i < count; ++i) {
                    ITuneItem item = itemList.get(i);
                    try {
                        long id = mDB.insert(TABLE_NAME, null, getContentValues(item));
                        item.setId(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    itemList.set(i, item);
                }

                mDB.setTransactionSuccessful();
                mDB.endTransaction();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "itemList is empty");
        }

        return itemList;
    }

    public synchronized List<ITuneItem> getAll() {
        Log.d(TAG, "getAll()");
        if (mDB == null || !mDB.isOpen()) {
            Log.e(TAG, "db is null or closed");
            return null;
        }

        List<ITuneItem> itemList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = mDB.query(TABLE_NAME, null, null, null, null, null, COLUMN_RANK + " COLLATE NOCASE ASC", null);
            while (cursor.moveToNext()) {
                itemList.add(getRecord(cursor));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return itemList;
    }

    public void saveItuneItems(List<ITuneItem> itemList) {
        deleteAll();
        insert(itemList);
    }

    private ContentValues getContentValues(ITuneItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RANK, item.getRank());
        cv.put(COLUMN_ART_WORK_URL, item.getArtworkUrl100());
        cv.put(COLUMN_SONG_NAME, item.getSongName());
        cv.put(COLUMN_ARTIST_NAME, item.getArtistName());
        return cv;
    }


    private ITuneItem getRecord(Cursor cursor) {
        ITuneItem item = new ITuneItem();
        item.setId(cursor.getLong(COLUMN_INDEX_KEY_ID));
        item.setRank(cursor.getInt(COLUMN_INDEX_RANK));
        item.setArtworkUrl100(cursor.getString(COLUMN_INDEX_ART_WORK_URL));
        item.setSongName(cursor.getString(COLUMN_INDEX_SONG_NAME));
        item.setArtistName(cursor.getString(COLUMN_INDEX_ARTIST_NAME));
        return item;
    }

    public synchronized void deleteAll() {
        Log.w(TAG, "deleteAll()");
        if (mDB == null || !mDB.isOpen()) {
            Log.e(TAG, "db is null or closed");
            return;
        }

        try {
            mDB.delete(TABLE_NAME, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
