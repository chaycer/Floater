package com.company.cc.floater;

import android.database.sqlite.SQLiteDatabase;


public class DBHandler {
    private SQLiteDatabase db;
    private String dbLocation;

    /**
     * Creates and opens connection to database
     */
    public DBHandler(){
        dbLocation = "android.resource://com.company.cc.floater/baseball_database";
        db = SQLiteDatabase.openOrCreateDatabase(dbLocation,null,null);


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
