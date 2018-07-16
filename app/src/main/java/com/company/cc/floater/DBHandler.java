package com.company.cc.floater;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static String DB_PATH;
    private static String DB_NAME = "baseball_database.sqlite";
    private final Context myContext;
    private String whereClause;

    /**
     * Open connection to database
     * @param context current context of application
     * @param RO Read only Status.  0 for Read only, 1 for writeable
     */
    public DBHandler(Context context, int RO){
        super(context, DB_NAME, null, 1);
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.myContext = context;
        switch(RO){
            case 0: this.createDatabase();
                    this.openDataBaseReadOnly();
        }

    }

    /**
     * Create empty database in memory and fill with static database
     */
    private void createDatabase(){
        boolean dbExist = checkDataBase();
        if(!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Lol couldn't copy database dumbass fix your broken code");
            }
        }
    }
    /**
    * Checks if database exists
    */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath,null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e){
            //Database doesn't exist yet
        }
        if (checkDB != null){
            checkDB.close();
        }

        return checkDB != null;
        }

    /**
     * Copy database into memory from the static database
      */
    private void copyDataBase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private void openDataBaseReadOnly() throws SQLiteException{
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
    }
    /**
     *
     */
    public void createFilter(List<FilterSearch> filters) {


    }

    /**
     *
     */
    public void clearFilter() {
        whereClause = "";
    }

    /**
     * Player search query.
     * @param player Name of the player to search for
     * @return queryResult Cursor object containing the query result
     */
    public Cursor playerSearchQuery(String player) {
        String[] name = player.split(" ");
        if(name.length == 0){
            throw new Error("Blank player name");
        }
        String firstName = "%" + name[0] + "%";
        if(name.length == 1) { //If only 1 name was put in, assume it is last name

            return db.rawQuery(String.format("Select * from player where name_first like '%s'",firstName),null);

        }
        String lastName = "%" + name[1] + "%";
        return db.rawQuery(String.format("Select * from player where name_first like '%s' AND name_last like '%s'",firstName,lastName), null);

    }

    /**
     * Query to return specific stats for a player
     * @param playerID
     * @param seasonYear
     * @param teamID
     * @param stats specific stats to be returned.  If null, return
     * @return queryResult Cursor object containing the query result
     */
    public Cursor playerStatsQuery(String playerID, int seasonYear, String teamID, String stats) {
        if (stats.equals(null) || stats.equals("")){
            stats = "*";
        }

        String query = "";

        return db.rawQuery(query,null);
    }
    @Override
    public synchronized void close() {
        if(db != null)
            db.close();

        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase mdb) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase mdb, int oldVersion, int newVersion) {

    }
}
