package com.company.cc.floater;

import android.database.sqlite.SQLiteDatabase;


public class DBHandler {
    private SQLiteDatabase db;
    private static String DB_LOCATION;

    /**
     * Creates and opens connection to database
     */
    public DBHandler(){
        DB_LOCATION = "baseball_database";
        db = SQLiteDatabase.openOrCreateDatabase(DB_LOCATION,null,null);


        return;
    }

    /**
     * Closes database when it is no longer in use
     *
     */
    public void CloseDatabase(){
        db.close();
        return;
    }
}
