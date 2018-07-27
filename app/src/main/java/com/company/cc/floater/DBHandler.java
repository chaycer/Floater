package com.company.cc.floater;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;



public class DBHandler extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static String DB_PATH;
    private static String DB_NAME = "baseball_database.sqlite";
    private final Context myContext;
    private String[] battingTable;
    private String[] pitchingTable;
    private String[] fieldingTable;
    private String selectAll;

    /**
     * Open connection to database
     * @param context current context of application
     */
    public DBHandler(Context context){
        super(context, DB_NAME, null, 1);
        DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.myContext = context;
        this.createDatabase();
        this.openDatabase();
        this.getColumns();


    }
    /**
     * Create empty database in memory and fill with static database
     */
    private void createDatabase(){
        boolean dbExist = checkDataBase();
        if(!dbExist) {
            this.getWritableDatabase();
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

    private void openDatabase() throws SQLiteException{
        String myPath = DB_PATH + DB_NAME;
        db = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * gets columns from database
     */
    private void getColumns() {
        Cursor setColumns = db.rawQuery("select * from batting", null);
        battingTable = setColumns.getColumnNames();
        setColumns = db.rawQuery("select * from pitching", null);
        pitchingTable = setColumns.getColumnNames();
        setColumns = db.rawQuery("select * from fielding", null);
        fieldingTable = setColumns.getColumnNames();
        StringBuilder select = new StringBuilder();
        for (String col: battingTable){
            select.append("batting." + col + " AS 'batting." + col + "',");
        }
        for (String col: pitchingTable){
            select.append("pitching." + col + " AS 'pitching." + col + "',");
        }
        for (String col: fieldingTable){
            select.append("fielding." + col + " AS 'fielding." + col + "',");
        }
        select.append("ERA_Stats.era as 'ERA_Stats.era'");
        selectAll = select.toString();

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
        String plname = "%" + name[0] + "%";
        if(name.length == 1) { //If only 1 name was put in, assume it is last name

            return db.rawQuery(String.format("Select * from player where name_first like '%s' OR name_last like '%s'",plname,plname),null);

        }
        String lastName = "%" + name[1] + "%";
        return db.rawQuery(String.format("Select * from player where name_first like '%s' AND name_last like '%s'",plname,lastName), null);

    }

    /**
     * Search for all associated seasons a player has had with a specific team.  If no team is provided, Search for all distinct season + team combos.
     * @param playerID
     * @param teamID Specific team_ID to search for.  If left blank the result will also contain all team_IDs associated with each year.
     * @return Query containing all seasons that a player has statistics for
     */
    public Cursor playerTeamsQuery(String playerID, String teamID) {

        if (teamID == null || teamID.equals("")) {
            String query = String.format("SELECT DISTINCT fielding.year, fielding.team_id " +
                    "FROM fielding " +
                    "WHERE fielding.player_id = '%s' " +
                    "union " +
                    "SELECT distinct batting.year, batting.team_id " +
                    "FROM batting " +
                    "WHERE batting.player_id = '%s' " +
                    "union " +
                    "SELECT DISTINCT pitching.year, pitching.team_id " +
                    "FROM pitching " +
                    "WHERE pitching.player_id = '%s' ", playerID, playerID, playerID);
            return db.rawQuery(query, null);
        }

        String query = String.format("Select distinct fielding.year " +
                "FROM fielding " +
                "WHERE fielding.player_id = '%s' " +
                "AND fielding.team_id = '%s' " +
                "union " +
                "SELECT distinct batting.year " +
                "FROM batting " +
                "WHERE batting.player_id = '%s' " +
                "AND batting.team_id = '%s' " +
                "union " +
                "SELECT DISTINCT pitching.year " +
                "FROM pitching " +
                "WHERE pitching.player_id = '%s' " +
                "AND pitching.team_id = '%s'",playerID,teamID,playerID,teamID,playerID,teamID);

        return db.rawQuery(query, null);
    }
    /**
     * Query to return specific stats for a player
     * @param playerID player to search for.
     * @param seasonYear year of stats.
     * @param teamID team_id of player you would like to be associated with.
     * @param table table to pull stats from (pitching, batting, fielding)
     * @return queryResult Cursor object containing the query result
     */
    public Cursor playerStatsQuery(String playerID, int seasonYear, String teamID, String table) {
        String query;
        if (table.equals("pitching")) {
            query = String.format("SELECT * FROM pitching " +
                    "INNER JOIN ERA_Stats on pitching.ip = ERA_Stats.ip and pitching.er = ERA_Stats.er " +
                    "where pitching.player_id = '%s' and pitching.year = %d and pitching.team_id = '%s'",playerID,seasonYear,teamID);
        } else {
            query = String.format("SELECT * " +
                    "FROM %s " +
                    "WHERE %s.player_id = '%s' and %s.year = %d and %s.team_id = '%s'", table,table,playerID,table,seasonYear,table,teamID);
        }

        return db.rawQuery(query,null);
    }

    public Cursor playerTableQuery(String playerID){
        String query = String.format("Select * from player where player_id = %s", playerID);
        return db.rawQuery(query,null);

    }

    /**
     * Query to be used when searching for teams so that we can retrieve the proper team_id
     * @param teamName Team name to search for
     * @return Query with a list of team_ids and seasons for those teams
     */
    public Cursor teamSearch(String teamName) {

        String cTeamName = "%" + teamName + "%";
        String query = String.format("select name, team_id, year from team where name like '%s' order by name asc",cTeamName);
        return db.rawQuery(query,null);
    }

    /**
     * Pulls the team stats for a given year
     * @param teamID team ID to search for
     * @param seasonYear Season year to search for.  If null, returns all seasons
     * @return
     */
    public Cursor teamStatsQuery(String teamID, Integer seasonYear) {
        String where;
        if (seasonYear == null || seasonYear.equals("")) {
            where = String.format("team_id = %s", teamID);
        } else {
            where = String.format("team_id = %s AND year = %d", teamID, seasonYear);
        }
        String query = "select * from team where" + where;

        return db.rawQuery(query, null);
    }

    /**
     * Pulls all players that played on a specific team in a given season
     * @param teamID teamID that you want to search for.
     * @param Season Season that you want to pull the roster for
     * @return Cursor object with the result of the query
     */
    public Cursor teamRosterSearch(String teamID, int Season) {
        @SuppressLint("DefaultLocale") String query = String.format("select distinct player.player_id, player.name_first, player.name_last, team.team_id, team.name, team.year " +
                        "FROM fielding " +
                        "JOIN team on team.team_id = fielding.team_id and team.year = fielding.year " +
                        "JOIN player on player.player_id = fielding.player_id " +
                        "where team.team_id = '%s' and team.year = %d " +
                        "union " +
                        "select distinct player.player_id, player.name_first, player.name_last, team.team_id, team.name, team.year " +
                        "from batting " +
                        "JOIN team on team.team_id = batting.team_id and team.year = batting.year " +
                        "JOIN player on player.player_id = batting.player_id " +
                        "where team.team_id = '%s' and team.year = %d " +
                        "union " +
                        "select distinct player.player_id, player.name_first, player.name_last, team.team_id, team.name, team.year " +
                        "from pitching " +
                        "JOIN team on team.team_id = pitching.team_id and pitching.year = team.year " +
                        "JOIN player on player.player_id = pitching.player_id " +
                        "where team.team_id = '%s' and team.year = %d",teamID, Season,teamID, Season,teamID, Season);
        return db.rawQuery(query,null);
    }

    /**
     * Inserts a new player into the database.  Will parse out the different columns/tables from the list of playerdata into the correct insert table query.
     * Also generates new playerID based on first and last name
     * @param playerID Can be null, if it is null I will automatically generate a playerID
     * @param firstName First name of player
     * @param lastName Last name of player
     * @param seasonYear year they played in
     * @param teamID ID of team they play for
     * @param pos Position.  Required only if you are inserting into the fielding table
     * @param playerData List of data that is to be inserted into the tables
     * @return
     */
    public Cursor insertPlayerData(String playerID, String firstName, String lastName, int seasonYear, String teamID, String pos, List<InsertStat> playerData){
        if (playerID.equals("") || playerID == null) { //No player ID passed in create a new one and insert as new player
            playerID = this.createPlayerID(firstName, lastName);
            return InsertNewPlayer(playerID, firstName, lastName, seasonYear, teamID, pos, playerData);
        }
        if(this.playerStatsQuery(playerID,seasonYear,teamID,"").getCount() > 0){ //if they exist already
            return updatePlayer(playerID, seasonYear, teamID, pos, playerData);
        } else {
            return InsertNewPlayer(playerID, firstName, lastName, seasonYear, teamID, pos, playerData);
        }

    }
    private void updateERA(){
        Cursor eras = db.rawQuery("SELECT distinct pitching.er, pitching.ip FROM pitching WHERE NOT EXISTS (SELECT ERA_Stats.er, ERA_Stats.ip FROM ERA_Stats WHERE pitching.er = ERA_Stats.er AND pitching.ip = ERA_Stats.ip)", null);
        eras.moveToFirst();
        if (eras.getCount() < 1) {
            return;
        }
        do {
            String er = eras.getString(eras.getColumnIndex("pitching.er"));
            String ip = eras.getString(eras.getColumnIndex("pitching.ip"));
            Integer era = Integer.getInteger(er)/Integer.getInteger(ip) * 9;
            db.execSQL(String.format("INSERT INTO ERA_Stats (ER,IP,ERA) VALUES ('%s','%s','%s')",er,ip,era.toString()));
        } while (eras.moveToNext() != false);
    }
    /**
     * Use to update an existing player
     * @param playerID PlayerID to use
     * @param seasonYear year they played in
     * @param teamID ID of team they play for
     * @param pos Position.  Required only if you are inserting into the fielding table
     * @param playerData List of data that is to be inserted into the tables
     * @return query cursor pointing to player data to be displayed
     */
    private Cursor updatePlayer(String playerID, int seasonYear, String teamID, String pos, List<InsertStat> playerData) {
        StringBuilder bQueryCol = new StringBuilder();
        StringBuilder pQueryCol = new StringBuilder();
        StringBuilder fQueryCol = new StringBuilder();
        StringBuilder plQueryCol = new StringBuilder();

        for (InsertStat player : playerData) {
            if (player.getTable().equals("batting")) {
                if (bQueryCol.length() == 0) {
                    bQueryCol.append("UPDATE batting SET " + player.getColumn() + " = '" + player.getValue() + "'");
                } else {
                    bQueryCol.append(", " + player.getColumn() + " = '" + player.getValue() + "'");
                }
            }
            if (player.getTable().equals("pitching")) {
                if (pQueryCol.length() == 0) {
                    pQueryCol.append("UPDATE pitching SET " + player.getColumn() + " = '" + player.getValue() + "'");
                } else {
                    pQueryCol.append(", " + player.getColumn() + " = '" + player.getValue() + "'");
                }
            }
            if (player.getTable().equals("fielding")) {
                if (fQueryCol.length() == 0) {
                    if (pos.equals("") || pos == null) {
                        throw new Error("Can't insert with a null position");
                    } else {
                        fQueryCol.append("UPDATE fielding SET " + player.getColumn() + " = '" + player.getValue() + "'");
                    }
                } else {
                        fQueryCol.append(", " + player.getColumn() + " = '" + player.getValue() + "'");
                    }
            }
            if (player.getTable().equals("player")) {
                if (plQueryCol.length() == 0) {
                    plQueryCol.append("UPDATE player SET " + player.getColumn() + " = '" + player.getValue() + "'");
                } else {
                    plQueryCol.append(", " + player.getColumn() + " = '" + player.getValue()+ "'");
                }
            }
        }
        if (bQueryCol.length() != 0) {
            String bQuery = bQueryCol.toString() + " WHERE batting.player_id = '" + playerID +
                    "' AND batting.team_id = '" + teamID +
                    "' AND batting.year = '" + seasonYear + "'";
            db.execSQL(bQuery);
        }

        if (fQueryCol.length() != 0) {
            String fQuery = fQueryCol.toString() + " WHERE fielding.player_id = '" + playerID +
                    "' AND fielding.team_id = '" + teamID +
                    "' AND fielding.year = '" + seasonYear +
                    "' AND fielding.pos = '" + pos + "'";
            db.execSQL(fQuery);
        }

        if (pQueryCol.length() != 0) {
            String pQuery = pQueryCol.toString() + " WHERE pitching.player_id = '" + playerID +
                    "' AND pitching.team_id = '" + teamID +
                    "' AND pitching.year = '" + seasonYear + "'";
            db.execSQL(pQuery);
        }

        if (plQueryCol.length() != 0) {
            String plQuery = plQueryCol.toString() + " WHERE player.player_id = '" + playerID + "'";
            db.execSQL(plQuery);
        }
        updateERA();
        return this.playerStatsQuery(playerID, seasonYear, teamID, null);
    }

    /**
     * Method for inserting a new player
     * @param playerID PlayerID to use
     * @param firstName First name of player
     * @param lastName Last name of player
     * @param seasonYear year they played in
     * @param teamID ID of team they play for
     * @param pos Position.  Required only if you are inserting into the fielding table
     * @param playerData List of data that is to be inserted into the tables
     * @return
     */
    private Cursor InsertNewPlayer(String playerID, String firstName, String lastName, int seasonYear, String teamID, String pos, List<InsertStat> playerData) {
        StringBuilder bQueryValues = new StringBuilder();
        StringBuilder bQueryCol = new StringBuilder();
        StringBuilder pQueryValues = new StringBuilder();
        StringBuilder pQueryCol = new StringBuilder();
        StringBuilder fQueryValues = new StringBuilder();
        StringBuilder fQueryCol = new StringBuilder();
        StringBuilder plQueryCol = new StringBuilder();
        StringBuilder plQueryValues = new StringBuilder();

        for (InsertStat player:playerData) {
            if (player.getTable().equals("batting")){
                if (bQueryCol.length() == 0) {
                    bQueryCol.append("Insert into batting (player_id, year, team_id, " + player.getColumn());
                    bQueryValues.append("(\'" + playerID + "\'," + seasonYear + ",\'" + teamID + "\',\'" + player.getValue() + "'");
                }
                else {
                    bQueryCol.append(", " + player.getColumn());
                    bQueryValues.append(", \'" + player.getValue() + "'");
                }
            }
            if (player.getTable().equals("pitching")){
                if (pQueryCol.length() == 0) {
                    pQueryCol.append("Insert into pitching (player_id, year, team_id, " + player.getColumn());
                    pQueryValues.append("(\'" + playerID + "\'," + seasonYear + ",\'" + teamID + "\',\'" + player.getValue() +"'");
                }
                else {
                    pQueryCol.append(", " + player.getColumn());
                    pQueryValues.append(", \'" + player.getValue() + "'");
                }
            }
            if (player.getTable().equals("fielding")){
                if (fQueryCol.length() == 0) {
                    if(pos.equals("")||pos == null){
                        throw new Error("Can't insert with a null position");
                    }
                    fQueryCol.append("Insert into fielding (player_id, year, team_id, pos, " + player.getColumn());
                    fQueryValues.append("(\'" + playerID + "\'," + seasonYear + ",\'" + teamID + "\',\'" + pos + "\',\'" + player.getValue() +"'");
                }
                else {
                    fQueryCol.append(", " + player.getColumn());
                    fQueryValues.append(", \'" + player.getValue() + "'");
                }
            }
            if (player.getTable().equals("player")){
                if (plQueryCol.length() == 0) {
                    plQueryCol.append("Insert into player (player_id, name_first, name_last, " + player.getColumn());
                    plQueryValues.append("(\'" + playerID + "\', \'" + firstName + "\', \'" + lastName + "\', \'" + player.getValue() + "'");
                }
                else {
                    plQueryCol.append(", " + player.getColumn());
                    plQueryValues.append(", \'" + player.getValue()+ "'");
                }
            }
        }

        if(bQueryCol.length() != 0) {
            bQueryCol.append(")");
            bQueryValues.append(")");
            String bQuery = bQueryCol.toString() + " Values " + bQueryValues.toString();
            db.execSQL(bQuery);
        }

        if(fQueryCol.length() != 0) {
            fQueryCol.append(")");
            fQueryValues.append(")");
            String fQuery = fQueryCol.toString() + " Values " + fQueryValues.toString();
            db.execSQL(fQuery);
        }

        if(pQueryCol.length() != 0) {
            pQueryCol.append(")");
            pQueryValues.append(")");
            String pQuery = pQueryCol.toString() + " Values " + pQueryValues.toString();
            db.execSQL(pQuery);
        }

        if(plQueryCol.length() != 0) {
            plQueryCol.append(")");
            plQueryValues.append(")");
            String plQuery = plQueryCol.toString() + " Values " + plQueryValues.toString();
            db.execSQL(plQuery);
        }
        updateERA();
        return this.playerStatsQuery(playerID,seasonYear,teamID,null);
    }

    /**
     * Create a unique playerID from the name and last name.  General structure is first 5 letters of first name + first 2 letters of last name + number
     * @param firstName
     * @param lastName
     * @return
     */
    public String createPlayerID(String firstName, String lastName) {
        StringBuilder playerID = new StringBuilder();
        if(firstName.length() < 5) {
            playerID.append(firstName);
        } else {
            playerID.append(firstName.substring(0, 5));
        }
        playerID.append(lastName.substring(0,2));
        String query = "select * from player where player_id like '" + playerID + "%'";
        Cursor result = db.rawQuery(query,null);
        int id = result.getCount() + 1;
        playerID.append(id);
        return playerID.toString().toLowerCase();
    }

    public Cursor filterSearchQuery(List<FilterSearch> playerFilters){
        StringBuilder where = new StringBuilder();
        StringBuilder select = new StringBuilder();
        String fPos = "";
        boolean first = true;
        for (FilterSearch filter:playerFilters) {
            if(fPos == "" && filter.getStat().contains("fielding")) {
                fPos = ",fielding.pos as 'fielding.pos'";
            }
            if(first){
                where.append(filter.toString());
                select.append(String.format(",%s as '%s'",filter.getStat(),filter.getStat()));
                first = false;
            } else {
                where.append(" AND " + filter.toString());
                select.append(String.format(", %s AS '%s'", filter.getStat(),filter.getStat()));
            }
        }
        String query = String.format("select distinct player.name_first as 'name_first', " +
                        "player.name_last as 'name_last', " +
                        "player.player_id as 'player_id', " +
                        "CASE " +
                        "WHEN fielding.year IS NOT NULL THEN fielding.year " +
                        "WHEN batting.year IS NOT NULL THEN batting.year " +
                        "ELSE pitching.year " +
                        "END as 'year', " +
                        "CASE " +
                        "WHEN fielding.team_id IS NOT NULL THEN fielding.team_id " +
                        "WHEN batting.team_id IS NOT NULL THEN batting.team_id " +
                        "ELSE pitching.team_id " +
                        "END as 'team_id' " +
                        "%s" +
                        "%s " +
                        "FROM fielding " +
                        "LEFT OUTER JOIN batting on batting.player_id = fielding.player_id AND batting.year = fielding.year and batting.team_id = fielding.team_id " +
                        "LEFT OUTER JOIN pitching on pitching.player_id = fielding.player_id AND pitching.year = fielding.year and pitching.team_id = fielding.team_id " +
                        "LEFT OUTER JOIN ERA_Stats on pitching.ip = ERA_Stats.ip AND pitching.er = ERA_Stats.er " +
                        "LEFT OUTER JOIN player on player.player_id = fielding.player_id " +
                        "where %s",fPos,select,where);

        return db.rawQuery(query.toString(), null);

    }

    /**
     * Search for the park that a team played in that year
     * @param teamID
     * @param year
     * @return
     */
    public Cursor parkQuery(String teamID, int year){
        Integer iYear = year;
        String where = "where team.team_id = '" + teamID + "' AND team.year = " + iYear.toString();
        String query =  "select home_game.year,home_game.team_id,home_game.park_id,home_game.span_first,home_game.span_last,home_game.games,home_game.openings,home_game.attendance," +
                "park.park_name,park.park_alias,park.city,park.state,park.country " +
                "from home_game " +
                "INNER JOIN team on home_game.team_id = team.team_id and home_game.year = team.year " +
                "INNER JOIN park on park.park_id = home_game.park_id " + where + " ORDER BY team.year asc";
        return db.rawQuery(query, null);
    }
    //Override methods

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
