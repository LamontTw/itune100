package com.lamont.demo.itunetop100.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class iTuneDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "iTuneDBHelper";

    private static final String DATABASE_NAME = "itune100.db";
    private static final int VERSION_INIT = 1;
    private static final int VERSION = VERSION_INIT;
    private static SQLiteDatabase mSQLiteDB;

    public iTuneDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static SQLiteDatabase getDatabase(Context context) {
        if (mSQLiteDB == null || !mSQLiteDB.isOpen()) {
            Log.i(TAG, "getDatabase(): open database");
            mSQLiteDB = new iTuneDBHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return mSQLiteDB;
    }

    public synchronized static boolean closeDatabase() {
        Log.i(TAG, "closeDatabase()");
        if (mSQLiteDB == null || !mSQLiteDB.isOpen()) {
            Log.w(TAG, "closeDatabase: database is null or closed");
            return false;
        }
        mSQLiteDB.close();
        return true;
    }

    public synchronized static boolean clearDatabase() {
        Log.i(TAG, "clearDatabase()");
        if (mSQLiteDB == null || !mSQLiteDB.isOpen()) {
            Log.w(TAG, "clearDatabase: database is null or closed");
            return false;
        }
        Top100Table.deleteTable(mSQLiteDB);
        Top100Table.createTable(mSQLiteDB);

        return true;
    }

    @Override  // only run when the database file did not exist
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate(): curVersion = " + VERSION);
        Top100Table.createTable(db);
    }

    @Override  // only called when the database file exists
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade(): oldVersion = " + oldVersion);
        switch (oldVersion) {
            case VERSION_INIT :
            default:
                reCreateDBTable(db);
                break;
        }
    }

    private void reCreateDBTable(SQLiteDatabase db) {
        Top100Table.deleteTable(db);
        onCreate(db);
    }
}