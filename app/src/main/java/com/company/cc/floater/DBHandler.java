package com.company.cc.floater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;



public class DBHandler extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private static String DB_PATH;
    private static String DB_NAME = "baseball_database.sqlite";
    private final Context myContext;

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
            query = String.format("SELECT player_id,year,team_id,w,l,g,gs,cg,sho,sv,pitching.ip,h,pitching.er,hr,bb,so,baopp,ibb,wp,hbp,bk,bfp,gf,r,sh,sf,g_idp,era FROM pitching " +
                    "LEFT OUTER JOIN ERA_Stats on pitching.ip = ERA_Stats.ip and pitching.er = ERA_Stats.er " +
                    "where pitching.player_id = '%s' and pitching.year = %d and pitching.team_id = '%s'",playerID,seasonYear,teamID);
        } else {
            query = String.format("SELECT * " +
                    "FROM %s " +
                    "WHERE %s.player_id = '%s' and %s.year = %d and %s.team_id = '%s'", table,table,playerID,table,seasonYear,table,teamID);
        }

        return db.rawQuery(query,null);
    }

    public Cursor playerTableQuery(String playerID){
        String query = String.format("Select * from player where player_id = '%s'", playerID);
        return db.rawQuery(query,null);

    }

    /**
     * Query to be used when searching for teams so that we can retrieve the proper team_id
     * @param teamName Team name to search for
     * @return Query with a list of team_ids and seasons for those teams
     */
    public Cursor teamSearch(String teamName) {

        String cTeamName = "%" + teamName + "%";
        String query = String.format("select distinct name, team_id from team where name like '%s' order by name asc",cTeamName);
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
            where = String.format("team_id = '%s'", teamID);
        } else {
            where = String.format("team_id = '%s' AND year = %d", teamID, seasonYear);
        }
        String query = "select * from team where " + where;

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
    public Cursor insertPlayerData(String playerID, String firstName, String lastName, int seasonYear, String teamID, String pos, List<InsertStat> playerData, String table){
        InsertUpdatePlayer IUP = new InsertUpdatePlayer();
        if (playerID.equals("") || playerID == null) { //No player ID passed in create a new one and insert as new player
            playerID = this.createPlayerID(firstName, lastName);
            IUP.insertPlayer(playerID, firstName, lastName, playerData);
            return this.playerTableQuery(playerID);
        }

        if(table.equals("player")) {
            Cursor player = this.playerTableQuery(playerID);
            player.moveToFirst();
            int count = player.getCount();
            if(count > 0) {
                IUP.updatePlayer(playerID, playerData, firstName, lastName);
                return this.playerTableQuery(playerID);
            } else {
                IUP.insertPlayer(playerID,firstName,lastName, playerData);
                return this.playerTableQuery(playerID);
            }
        }
        Cursor player = this.playerStatsQuery(playerID,seasonYear,teamID, table);
        player.moveToFirst();
        int count = player.getCount();
        if (count > 0){

            if(table.equals("pitching")) {
                IUP.updatePitching(playerID,seasonYear,teamID,playerData);
                return this.playerTableQuery(playerID);

            }
            if(table.equals("batting")) {
                IUP.updateBatting(playerID,seasonYear,teamID,playerData);
                return this.playerTableQuery(playerID);

            }
            if(table.equals("fielding")) {
                IUP.updateFielding(playerID,seasonYear,teamID,pos,playerData);
                return this.playerTableQuery(playerID);

            }
        }
        else {
            if(table.equals("player")) {
                IUP.insertPlayer(playerID,firstName,lastName,playerData);
                return this.playerTableQuery(playerID);

            }
            if(table.equals("pitching")) {
                IUP.insertPitching(playerID,seasonYear,teamID,playerData);
                return this.playerTableQuery(playerID);

            }
            if(table.equals("batting")) {
                IUP.insertBatting(playerID,seasonYear,teamID,playerData);
                return this.playerTableQuery(playerID);

            }
            if(table.equals("fielding")) {
                IUP.insertFielding(playerID,seasonYear,teamID,pos,playerData);
                return this.playerTableQuery(playerID);

            }
        }
        return this.playerTableQuery(playerID);
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
        if(lastName.length() < 2) {
            playerID.append(lastName);
        } else {
            playerID.append(lastName.substring(0, 2));
        }
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
                        "LEFT OUTER JOIN batting ON batting.player_id = fielding.player_id AND batting.year = fielding.year and batting.team_id = fielding.team_id " +
                        "LEFT OUTER JOIN pitching ON fielding.player_id = pitching.player_id AND fielding.year = pitching.year and fielding.team_id = pitching.team_id " +
                        "LEFT OUTER JOIN ERA_Stats ON pitching.ip = ERA_Stats.ip AND pitching.er = ERA_Stats.er " +
                        "LEFT OUTER JOIN player ON player.player_id = fielding.player_id " +
                        "WHERE %s order by player.name_last",fPos,select,where);

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
                "LEFT OUTER JOIN team on home_game.team_id = team.team_id and home_game.year = team.year " +
                "LEFT OUTER JOIN park on park.park_id = home_game.park_id " + where + " ORDER BY team.year asc";
        return db.rawQuery(query, null);
    }

    /**
     * Used to return aggregate stats for the player
     * @param playerID playerID to collect stats from
     * @param table Table to be used.  If table = fielding, stats are aggregated by position.
     * @return Cursor with the requested aggregated stats
     */
    public Cursor careerStats(String playerID, String table) {
        if(table.equals("fielding")){
            return careerFieldingStats(playerID);
        } else if (table.equals("batting")){
            return careerBattingStats(playerID);
        } else { //pitching
            return careerPitchingStats(playerID);
        }
    }
    private Cursor careerFieldingStats(String playerID){
        String query = String.format("select player_id, pos, sum(g) as g, sum(gs) as gs, sum(inn_outs) as inn_outs, sum(po) as po, sum(a) as a, sum(e) as e, sum(dp) as dp, sum(pb) as pb, sum(wp) as wp, sum(sb) as sb, sum(cs) as cs, sum(zr) as zr " +
                        "from fielding " +
                        "where player_id = '%s' " +
                        "group by player_id, pos",playerID);
        return db.rawQuery(query,null);
    }
    private Cursor careerBattingStats(String playerID) {
        String query = String.format("select player_id, sum(g) as g, sum(ab) as ab, sum(r) as r, sum(h) as h, sum(double) as double, sum(triple) as triple, sum(hr) as hr, sum(rbi) as rbi, sum(sb) as sb, sum(cs) as cs, sum(bb) as bb, sum(so) as so, sum(ibb) as ibb, sum(hbp) as hbp, sum(sh) as sh, sum(sf) as sf, sum(g_idp) as g_idp " +
                        "from batting " +
                        "where player_id = '%s' " +
                        "group by player_id",playerID);
        return db.rawQuery(query,null);

    }
    private Cursor careerPitchingStats(String playerID) {
        String query = String.format("select player_id, sum(w) as w, sum(l) as l, sum(g) as g, sum(gs) as gs, sum(cg) as cg, sum(sho) as sho, sum(sv) as sv, sum(ip) as ip, sum(h) as h, sum(er) as er, sum(hr) as hr, sum(bb) as bb, sum(so) as so, sum(baopp) as baopp, sum(ibb) as ibb, sum(wp) as wp, sum(hbp) as hbp, sum(bk) as bk, sum(bfp) as bfp, sum(gf) as gf, sum(r) as r, sum(sh) as sh, sum(sf) as sf, sum(g_idp) as g_idp " +
                        "from pitching " +
                        "where player_id = '%s' " +
                        "group by player_id", playerID);
        return db.rawQuery(query,null);

    }

    /**
     * Function to return the max ERA that a player had throughout their career
     * @param playerID to return
     * @return Highest ERA player had across all seasons
     */
    public double maxERA(String playerID) {
        String query = String.format("select ERA from ERA_Stats, pitching where ERA_Stats.ip = pitching.ip and ERA_Stats.er = pitching.er and pitching.player_id = '%s'",playerID);
        Cursor results = db.rawQuery(query,null);
        results.moveToFirst();
        double hERA = results.getDouble(results.getColumnIndex("ERA")); //highest era
        while(results.moveToNext()){
            double era = results.getDouble(results.getColumnIndex("ERA"));
            if (era > hERA){
                hERA = era;
            }
        }
        return hERA;
    }
    /**
     * Function to return the mmin ERA that a player had throughout their career
     * @param playerID to return
     * @return Lowest ERA player had across all seasons
     */
    public double minERA(String playerID) {
        String query = String.format("select ERA from ERA_Stats, pitching where ERA_Stats.ip = pitching.ip and ERA_Stats.er = pitching.er and pitching.player_id = '%s'",playerID);
        Cursor results = db.rawQuery(query,null);
        results.moveToFirst();
        double mERA = results.getDouble(results.getColumnIndex("ERA")); //highest era
        while(results.moveToNext()){
            double era = results.getDouble(results.getColumnIndex("ERA"));
            if (era < mERA){
                mERA = era;
            }
        }
        return mERA;
    }
    /**
     * Return max of each stat across players career
     * @param playerID
     * @param table
     * @return Cursor pointing to DB result
     */
    public Cursor maxStats(String playerID, String table) {
        if(table.equals("fielding")){
            return maxFieldingStats(playerID);
        } else if (table.equals("batting")){
            return maxBattingStats(playerID);
        } else { //pitching
            return maxPitchingStats(playerID);
        }
    }
    private Cursor maxFieldingStats(String playerID){
        String query = String.format("select player_id, pos, max(g) as g, max(gs) as gs, max(inn_outs) as inn_outs, max(po) as po, max(a) as a, max(e) as e, max(dp) as dp, max(pb) as pb, max(wp) as wp, max(sb) as sb, max(cs) as cs, max(zr) as zr " +
                "from fielding " +
                "where player_id = '%s' " +
                "group by player_id, pos",playerID);
        return db.rawQuery(query,null);
    }
    private Cursor maxBattingStats(String playerID) {
        String query = String.format("select player_id, max(g) as g, max(ab) as ab, max(r) as r, max(h) as h, max(double) as double, max(triple) as triple, max(hr) as hr, max(rbi) as rbi, max(sb) as sb, max(cs) as cs, max(bb) as bb, max(so) as so, max(ibb) as ibb, max(hbp) as hbp, max(sh) as sh, max(sf) as sf, max(g_idp) as g_idp " +
                "from batting " +
                "where player_id = '%s' " +
                "group by player_id",playerID);
        return db.rawQuery(query,null);

    }
    private Cursor maxPitchingStats(String playerID) {
        String query = String.format("select player_id, max(w) as w, max(l) as l, max(g) as g, max(gs) as gs, max(cg) as cg, max(sho) as sho, max(sv) as sv, max(ip) as ip, max(h) as h, max(er) as er, max(hr) as hr, max(bb) as bb, max(so) as so, max(baopp) as baopp, max(ibb) as ibb, max(wp) as wp, max(hbp) as hbp, max(bk) as bk, max(bfp) as bfp, max(gf) as gf, max(r) as r, max(sh) as sh, max(sf) as sf, max(g_idp) as g_idp " +
                "from pitching " +
                "where player_id = '%s' " +
                "group by player_id", playerID);
        return db.rawQuery(query,null);

    }
    /**
     * Return min of each stat across players career
     * @param playerID
     * @param table
     * @return Cursor pointing to DB result
     */
    public Cursor minStats(String playerID, String table) {
        if(table.equals("fielding")){
            return minFieldingStats(playerID);
        } else if (table.equals("batting")){
            return minBattingStats(playerID);
        } else { //pitching
            return minPitchingStats(playerID);
        }
    }
    private Cursor minFieldingStats(String playerID){
        String query = String.format("select player_id, pos, min(g) as g, min(gs) as gs, min(inn_outs) as inn_outs, min(po) as po, min(a) as a, min(e) as e, min(dp) as dp, min(pb) as pb, min(wp) as wp, min(sb) as sb, min(cs) as cs, min(zr) as zr " +
                "from fielding " +
                "where player_id = '%s' " +
                "group by player_id, pos",playerID);
        return db.rawQuery(query,null);
    }
    private Cursor minBattingStats(String playerID) {
        String query = String.format("select player_id, min(g) as g, min(ab) as ab, min(r) as r, min(h) as h, min(double) as double, min(triple) as triple, min(hr) as hr, min(rbi) as rbi, min(sb) as sb, min(cs) as cs, min(bb) as bb, min(so) as so, min(ibb) as ibb, min(hbp) as hbp, min(sh) as sh, min(sf) as sf, min(g_idp) as g_idp " +
                "from batting " +
                "where player_id = '%s' " +
                "group by player_id",playerID);
        return db.rawQuery(query,null);

    }
    private Cursor minPitchingStats(String playerID) {
        String query = String.format("select player_id, min(w) as w, min(l) as l, min(g) as g, min(gs) as gs, min(cg) as cg, min(sho) as sho, min(sv) as sv, min(ip) as ip, min(h) as h, min(er) as er, min(hr) as hr, min(bb) as bb, min(so) as so, min(baopp) as baopp, min(ibb) as ibb, min(wp) as wp, min(hbp) as hbp, min(bk) as bk, min(bfp) as bfp, min(gf) as gf, min(r) as r, min(sh) as sh, min(sf) as sf, min(g_idp) as g_idp " +
                "from pitching " +
                "where player_id = '%s' " +
                "group by player_id", playerID);
        return db.rawQuery(query,null);

    }
    /**
     * Returns average stats for player over their career
     * @param playerID
     * @param table
     * @return Cursor with pointer to db result
     */
    public Cursor avgStats(String playerID, String table) {
        if(table.equals("fielding")){
            return avgFieldingStats(playerID);
        } else if (table.equals("batting")){
            return avgBattingStats(playerID);
        } else { //pitching
            return avgPitchingStats(playerID);
        }
    }
    private Cursor avgFieldingStats(String playerID){
        String query = String.format("select player_id, pos, avg(g) as g, avg(gs) as gs, avg(inn_outs) as inn_outs, avg(po) as po, avg(a) as a, avg(e) as e, avg(dp) as dp, avg(pb) as pb, avg(wp) as wp, avg(sb) as sb, avg(cs) as cs, avg(zr) as zr " +
                "from fielding " +
                "where player_id = '%s' " +
                "group by player_id, pos",playerID);
        return db.rawQuery(query,null);
    }
    private Cursor avgBattingStats(String playerID) {
        String query = String.format("select player_id, avg(g) as g, avg(ab) as ab, avg(r) as r, avg(h) as h, avg(double) as double, avg(triple) as triple, avg(hr) as hr, avg(rbi) as rbi, avg(sb) as sb, avg(cs) as cs, avg(bb) as bb, avg(so) as so, avg(ibb) as ibb, avg(hbp) as hbp, avg(sh) as sh, avg(sf) as sf, avg(g_idp) as g_idp " +
                "from batting " +
                "where player_id = '%s' " +
                "group by player_id",playerID);
        return db.rawQuery(query,null);

    }
    private Cursor avgPitchingStats(String playerID) {
        String query = String.format("select player_id, avg(w) as w, avg(l) as l, avg(g) as g, avg(gs) as gs, avg(cg) as cg, avg(sho) as sho, avg(sv) as sv, avg(ip) as ip, avg(h) as h, avg(er) as er, avg(hr) as hr, avg(bb) as bb, avg(so) as so, avg(baopp) as baopp, avg(ibb) as ibb, avg(wp) as wp, avg(hbp) as hbp, avg(bk) as bk, avg(bfp) as bfp, avg(gf) as gf, avg(r) as r, avg(sh) as sh, avg(sf) as sf, avg(g_idp) as g_idp " +
                "from pitching " +
                "where player_id = '%s' " +
                "group by player_id", playerID);
        return db.rawQuery(query,null);

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
    private class InsertUpdatePlayer {

        public InsertUpdatePlayer(){

        }

        private void insertPlayer(String playerID, String firstName, String lastName, List<InsertStat> playerData){
            StringBuilder plQueryCol = new StringBuilder();
            StringBuilder plQueryValues = new StringBuilder();
            plQueryCol.append("Insert into player (player_id, name_first, name_last ");
            plQueryValues.append(String.format("('%s','%s','%s'",playerID,firstName,lastName));
            for (InsertStat player:playerData) {
                if (player.getTable().equals("player")){
                    plQueryCol.append(", " + player.getColumn());
                    plQueryValues.append(", '" + player.getValue() + "'");
                }
            }
            plQueryCol.append(")");
            plQueryValues.append(")");
            String plQuery = plQueryCol.toString() + " Values " + plQueryValues.toString();
            db.execSQL(plQuery);
        }

        private void updatePlayer(String playerID, List<InsertStat> playerData, String firstName, String lastName){
            StringBuilder plQueryCol = new StringBuilder("UPDATE player SET ");
            Boolean first = true;
            if (firstName != null && !firstName.equals("")){
                plQueryCol.append(String.format("name_first = '%s'",firstName));
                first = false;
            }
            if (lastName != null && !lastName.equals("")){
                if (!first) {
                    plQueryCol.append(",");
                }
                plQueryCol.append(String.format("name_last = '%s'",lastName));
                first = false;
            }
            for (InsertStat player : playerData) {
                if (!first) {
                    plQueryCol.append(",");
                }
                plQueryCol.append(player.getColumn() + " = '" + player.getValue() + "'");
                first = false;

            }
            if (plQueryCol.length() != 0) {
                String plQuery = plQueryCol.toString() + " WHERE player.player_id = '" + playerID + "'";
                db.execSQL(plQuery);
            }
        }

        private void insertPitching(String playerID, int seasonYear, String teamID, List<InsertStat> playerData){
            StringBuilder pQueryValues = new StringBuilder();
            StringBuilder pQueryCol = new StringBuilder();
            boolean pitching = false;
            NumberFormat formatter = new DecimalFormat("#.##");
            for (InsertStat player:playerData) {
                if (player.getTable().equals("pitching")){
                    String value = "";
                    if(!player.getValue().equals("")) {
                        value = formatter.format(Float.valueOf(player.getValue()));
                    }
                    if(player.getColumn().equals("er") || player.getColumn().equals("ip")){
                        pitching = true;
                    }
                    if (pQueryCol.length() == 0) {
                        pQueryCol.append("Insert into pitching (player_id, year, team_id, " + player.getColumn());
                        pQueryValues.append("('" + playerID + "'," + seasonYear + ",'" + teamID + "','" + value +"'");
                    }
                    else {
                        pQueryCol.append(", " + player.getColumn());
                        pQueryValues.append(", '" + value + "'");
                    }
                }
            }
            if(pQueryCol.length() != 0) {
                pQueryCol.append(")");
                pQueryValues.append(")");
                String pQuery = pQueryCol.toString() + " Values " + pQueryValues.toString();
                db.execSQL(pQuery);
            }
            if (pitching) {
                updateERA(playerID,seasonYear,teamID);
            }
        }

        private void updatePitching(String playerID, int seasonYear, String teamID, List<InsertStat> playerData){
            StringBuilder pQueryCol = new StringBuilder();
            boolean pitching = false;
            NumberFormat formatter = new DecimalFormat("#.##");
            for (InsertStat player : playerData) {
                String value = "";
                if(!player.getValue().equals("")) {
                    value = formatter.format(Float.valueOf(player.getValue()));
                }
                if (player.getTable().equals("pitching")) {
                    if (player.getColumn().equals("er") || player.getColumn().equals("ip")) {
                        pitching = true;
                    }
                    if (pQueryCol.length() == 0) {
                        pQueryCol.append("UPDATE pitching SET " + player.getColumn() + " = '" + value + "'");
                    } else {
                        pQueryCol.append(", " + player.getColumn() + " = '" + value + "'");
                    }
                }
            }
            if (pQueryCol.length() != 0) {
                String pQuery = pQueryCol.toString() + " WHERE pitching.player_id = '" + playerID +
                        "' AND pitching.team_id = '" + teamID +
                        "' AND pitching.year = '" + seasonYear + "'";
                db.execSQL(pQuery);
            }
            if(pitching) {
                updateERA(playerID,seasonYear,teamID);
            }

        }

        private void insertBatting(String playerID, int seasonYear, String teamID, List<InsertStat> playerData){
            StringBuilder bQueryValues = new StringBuilder();
            StringBuilder bQueryCol = new StringBuilder();
            for (InsertStat player:playerData) {
                if (player.getTable().equals("batting")) {
                    if (bQueryCol.length() == 0) {
                        bQueryCol.append("Insert into batting (player_id, year, team_id, " + player.getColumn());
                        bQueryValues.append("('" + playerID + "'," + seasonYear + ",'" + teamID + "','" + player.getValue() + "'");
                    } else {
                        bQueryCol.append(", " + player.getColumn());
                        bQueryValues.append(", '" + player.getValue() + "'");
                    }
                }
            }
            if(bQueryCol.length() != 0) {
                bQueryCol.append(")");
                bQueryValues.append(")");
                String bQuery = bQueryCol.toString() + " Values " + bQueryValues.toString();
                db.execSQL(bQuery);
            }
        }

        private void updateBatting(String playerID, int seasonYear, String teamID, List<InsertStat> playerData){
            StringBuilder bQueryCol = new StringBuilder();
            for (InsertStat player : playerData) {
                if (player.getTable().equals("batting")) {
                    if (bQueryCol.length() == 0) {
                        bQueryCol.append("UPDATE batting SET " + player.getColumn() + " = '" + player.getValue() + "'");
                    } else {
                        bQueryCol.append(", " + player.getColumn() + " = '" + player.getValue() + "'");
                    }
                }
            }
            if (bQueryCol.length() != 0) {
                String bQuery = bQueryCol.toString() + " WHERE batting.player_id = '" + playerID +
                        "' AND batting.team_id = '" + teamID +
                        "' AND batting.year = '" + seasonYear + "'";
                db.execSQL(bQuery);
            }
        }

        private void insertFielding(String playerID, int seasonYear, String teamID, String pos, List<InsertStat> playerData){
            StringBuilder fQueryValues = new StringBuilder();
            StringBuilder fQueryCol = new StringBuilder();
            for (InsertStat player:playerData) {
                if (player.getTable().equals("fielding")){
                    if (fQueryCol.length() == 0) {
                        if(pos.equals("")||pos == null){
                            throw new Error("Can't insert with a null position");
                        }
                        fQueryCol.append("Insert into fielding (player_id, year, team_id, pos, " + player.getColumn());
                        fQueryValues.append("('" + playerID + "'," + seasonYear + ",'" + teamID + "','" + pos + "','" + player.getValue() +"'");
                    }
                    else {
                        fQueryCol.append(", " + player.getColumn());
                        fQueryValues.append(", '" + player.getValue() + "'");
                    }
                }
            }
            if(fQueryCol.length() != 0) {
                fQueryCol.append(")");
                fQueryValues.append(")");
                String fQuery = fQueryCol.toString() + " Values " + fQueryValues.toString();
                db.execSQL(fQuery);
            }
        }

        private void updateFielding(String playerID, int seasonYear, String teamID, String pos, List<InsertStat> playerData){
            StringBuilder fQueryCol = new StringBuilder();
            for (InsertStat player : playerData) {
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
            }
            if (fQueryCol.length() != 0) {
                String fQuery = fQueryCol.toString() + " WHERE fielding.player_id = '" + playerID +
                        "' AND fielding.team_id = '" + teamID +
                        "' AND fielding.year = '" + seasonYear +
                        "' AND fielding.pos = '" + pos + "'";
                db.execSQL(fQuery);
            }
        }
        private void updateERA(String playerID, int seasonYear, String teamID){
            Cursor eras = db.rawQuery(String.format("Select er, ip from pitching where player_id = '%s' and year = %d and team_id = '%s'",playerID,seasonYear,teamID),null);
            eras.moveToFirst();
            float er;
            float ip;
            if(!eras.isNull(eras.getColumnIndex("er"))) {
                er = eras.getFloat(eras.getColumnIndex("er"));
            } else {
                return;
            }
            if(!eras.isNull(eras.getColumnIndex("ip"))) {
                ip = eras.getFloat(eras.getColumnIndex("ip"));
            } else {
                return;
            }


            float era = er/ip*(float)9;
            NumberFormat formatter = new DecimalFormat("#.##");
            String ser = formatter.format(er);
            String sip = formatter.format(ip);
            String sera = formatter.format(era);
            Cursor exists = db.rawQuery(String.format("Select * from ERA_Stats where ip = '%s' and er = '%s'",sip,ser),null);
            if(exists.getCount()>0){ //already exists, quit out
                return;
            }
            db.execSQL(String.format("INSERT INTO ERA_Stats (ER,IP,ERA) VALUES ('%s','%s','%s')",ser,sip,sera)); //doesn't exist, insert into


        }
    }
}
