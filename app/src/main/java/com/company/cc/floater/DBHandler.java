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
     * @param filters
     */
    public void createFilter(List<FilterSearch> filters) {


    }

    /**
     * Searches for player based on provided name
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
     * Search for all associated seasons a player has had with a specific team.  If no team is provided, Search for all distinct season + team combos.
     * @param playerID
     * @param teamID Specific team_ID to search for.  If left blank the result will also contain all team_IDs associated with each year.
     * @return Query containing all seasons that a player has statistics for
     */
    public Cursor playerTeamsQuery(String playerID, String teamID) {

        if (teamID == null || teamID.equals("")) {
            String query = String.format("Select distinct fielding_test.year, fielding_test.team_id " +
                    "FROM fielding_test " +
                    "WHERE fielding_test.player_id = '%s' " +
                    "union " +
                    "SELECT distinct batting_test.year, batting_test.team_id " +
                    "FROM batting_test " +
                    "WHERE batting_test.player_id = '%s' " +
                    "union " +
                    "SELECT Distinct pitching_test.year, pitching_test.team_id " +
                    "FROM pitching_test " +
                    "WHERE pitching_test.player_id = '%s' ", playerID, playerID, playerID);
            return db.rawQuery(query, null);
        }

        String query = String.format("Select distinct fielding_test.year " +
                "FROM fielding_test " +
                "WHERE fielding_test.player_id = '%s' " +
                "AND fielding_test.team_id = '%s' " +
                "union " +
                "SELECT distinct batting_test.year " +
                "FROM batting_test " +
                "WHERE batting_test.player_id = '%s' " +
                "AND batting_test.team_id = '%s' " +
                "union " +
                "SELECT Distinct pitching_test.year " +
                "FROM pitching_test " +
                "WHERE pitching_test.player_id = '%s' " +
                "AND pitching_test.team_id = '%s'",playerID,teamID,playerID,teamID,playerID,teamID);

        return db.rawQuery(query, null);
    }
    /**
     * Query to return specific stats for a player
     * @param playerID player to search for.
     * @param seasonYear year of stats.
     * @param teamID team_id of player you would like to be associated with.
     * @param stats specific stats to be returned.  Must include in format 'tableName.ColumnName', with each column delimited by a comma. If null, return all stats from pitching, batting, and fielding tables
     * @return queryResult Cursor object containing the query result
     */
    public Cursor playerStatsQuery(String playerID, int seasonYear, String teamID, String stats) {
        if (stats == null || stats.equals("")){
            stats = "*";
        }

        String query = String.format("SELECT %s" +
                        "FROM fielding_test " +
                        "LEFT OUTER JOIN batting_test on batting_test.player_id = fielding_test.player_id AND batting_test.year = fielding_test.year " +
                        "LEFT OUTER JOIN pitching_test on pitching_test.player_id = fielding_test.player_id AND pitching_test.year = fielding_test.year " +
                "where fielding_test.player_id = '%s' and fielding_test.year = %d", stats,playerID,seasonYear);
        ;

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
