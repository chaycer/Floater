package com.company.cc.floater;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FloaterApplication extends Application{
    static CharSequence battingStats[] = new CharSequence[] {"player_id", "year", "team_id", "g", "ab", "r", "h", "double", "triple", "hr", "rbi", "sb", "cs", "bb", "so", "ibb", "sh", "sf", "g_idp"};
    static CharSequence fieldingStats[] = new CharSequence[] {"player_id", "year", "team_id", "pos", "g", "gs", "inn_outs", "po", "a", "e", "dp", "pb", "wp", "sb", "cs", "zr"};
    static CharSequence pitchingStats[] = new CharSequence[] {"player_id", "year", "team_id", "w", "l", "g", "gs", "cg", "sho", "sv", "ip", "h", "er", "hr", "bb", "so", "baopp", "era", "ibb", "wp", "hbp", "bk", "bfp", "gf", "r", "sh", "sf", "g_idp"};

    static CharSequence playerStats[] = new CharSequence[] {"player_id", "first_name", "last_name", "birth_day", "birth_month", "birth_year", "birth_country", "death_day", "death_month", "death_year", "bats", "throws", "debut", "final_game"};
    static CharSequence teamStats[] = new CharSequence[] {"team_id", "name", "year", "league", "div_id", "rank", "g", "w", "l", "ws_win", "attendance"};
    static CharSequence parkStats[] = new CharSequence[] {"park_id", "park_name", "park_alias", "city", "state", "country"};

    static CharSequence operators[] = new CharSequence[] {"=", "<", ">", "<=", ">="};

    static int BATTING = 1;
    static int FIELDING = 2;
    static int PITCHING = 3;

    static int PLAYER = 10;
    static int TEAM = 20;
    static int PARK = 30;

    public static CharSequence[] getBattingStats() {
        return battingStats;
    }

    public static CharSequence[] getFieldingStats() {
        return fieldingStats;
    }

    public static CharSequence[] getPitchingStats() {
        return pitchingStats;
    }

    public static CharSequence[] getPlayerStats() {
        return playerStats;
    }

    public static CharSequence[] getTeamStats() {
        return teamStats;
    }

    public static CharSequence[] getParkStats() {
        return parkStats;
    }

    public CharSequence[] getOperators() {
        return operators;
    }

    public int getBATTING() {
        return BATTING;
    }

    public int getFIELDING(){
        return FIELDING;
    }

    public int getPITCHING(){
        return PITCHING;
    }

    /**
     * @return - all the columns of a stat array in a comma-delimited string with format tableName.ColumnName
     */
    public static String delimitedStatsColumns(String prefix, CharSequence[] names){
        String stats = "";
        prefix = prefix + ".";
        for (int i = 0; i < names.length; i++){
            String tempprefix = prefix;
            if (names[i].toString().compareTo("era") == 0){
                tempprefix = "ERA_stats.";
            }
            if (i == 0) {
                stats = stats + tempprefix + names[i];
            }
            else{
                stats = stats + "," + tempprefix + names[i];
            }
        }
        return stats;
    }

    /**
     * @return - all the batting columns in a comma-delimited string with format tableName.ColumnName
     */
    public static String battingStatsColumns(){
        return delimitedStatsColumns("batting", battingStats);
    }

    /**
     * @return - all the pitching columns in a comma-delimited string with format tableName.ColumnName
     */
    public static String pitchingStatsColumns(){
        return delimitedStatsColumns("pitching", pitchingStats);
    }

    /**
     * @return - all the fielding columns in a comma-delimited string with format tableName.ColumnName
     */
    public static String fieldingStatsColumns(){
        return delimitedStatsColumns("fielding", fieldingStats);
    }

    /**
     * Displays a list of label/value-to-edit pairs for the given stat group
     * @param mainLayout - layout to display each line on
     * @param inflater - LayoutInflater for the mainLayout
     * @param stats - the stats to display
     */
    public static void addStatLines(LinearLayout mainLayout, LayoutInflater inflater, CharSequence[] stats){
        for (int i = 0; i < stats.length; i++){
            View dynamicLayout = inflater.inflate(R.layout.stat_line, null);
            TextView tv = dynamicLayout.findViewById(R.id.statNameTextView);
            EditText et = dynamicLayout.findViewById(R.id.statInputEditText);

            tv.setText(stats[i]); // set label
            mainLayout.addView(dynamicLayout);
        }
        View dynamicLayout = inflater.inflate(R.layout.null_overwrite_checkbox, null);
        mainLayout.addView(dynamicLayout);
    }

    /**
     * Adds stats to a vertical linear layout from a CursorRow with a label/value style on the
     * column name and value
     * @param mainLayout - Vertical layout to add the stat lines to
     * @param inflater - Inflater for the layout
     * @param row - CursorRow containing all the label/values
     * @param toExclude - Array of column names to exclude, can be null
     * @param hide - Should the generated lines be hidden by default?
     * @param button - A view to be added at the top if desired, can be null
     * @return A list of all the lines (horizontal linear layouts) added
     */
    public static LinkedList<View> addStatsFromRow(LinearLayout mainLayout, LayoutInflater inflater, CursorRow row, String[] toExclude, boolean hide, View button){
        LinkedList<View> views = new LinkedList<>();
        if (button != null){
            views.add(button);
        }
        for (int i = 0; i < row.getSize(); i++){
            boolean exclude = false;
            if (toExclude != null) {
                for (int j = 0; j < toExclude.length; j++) {
                    if (row.getColumnNameByIndex(i).compareTo(toExclude[j]) == 0) {
                        exclude = true;
                    }
                }
            }
            if (exclude){
                continue;
            }
            View dynamicLayout = inflater.inflate(R.layout.stat_line_no_edit, null);
            TextView name = dynamicLayout.findViewById(R.id.statNameNoEditTextView);
            name.setText(row.getColumnNameByIndex(i));

            TextView value = dynamicLayout.findViewById(R.id.statValueNoEditTextView);
            value.setText(row.getValueByIndex(i));

            if (hide){
                dynamicLayout.setVisibility(View.GONE);
            }
            mainLayout.addView(dynamicLayout);
            views.add(dynamicLayout);
        }
        return views;
    }


    /**
     * Displays each player and years active from a cursor containing player information
     * @param mainLayout - layout to display each line on
     * @param inflater - LayoutInflater for the mainLayout
     * @param result - cursor containing data from the player table
     * @param context - context of the calling function
     */
    public static void addPlayerLines(LinearLayout mainLayout, LayoutInflater inflater, final Cursor result, final Context context){

        // TODO: show "no results" if cursor empty
        if (result == null || (result.getCount() < 1)){

            result.close();
            return;
        }

        while (result.moveToNext()){
            View dynamicLayout = inflater.inflate(R.layout.player_search_layout, null);
            TextView name = dynamicLayout.findViewById(R.id.playerNameTextView);
            TextView years = dynamicLayout.findViewById(R.id.yearsActiveTextView);

            if(result.getString(result.getColumnIndex("name_first")).isEmpty()
                    || result.getString(result.getColumnIndex("name_last")).isEmpty()){
                continue;
            }

            if (!result.getString(result.getColumnIndex("name_first")).isEmpty()) {
                SpannableString pName = new SpannableString(
                        result.getString(result.getColumnIndex("name_first")) + " "
                                + result.getString(result.getColumnIndex("name_last")));
                pName.setSpan(new UnderlineSpan(), 0, pName.length(), 0);
                name.setText(pName); // set label
            }
            String active = result.getString(result.getColumnIndex("debut"));
            if (active != null && active.length() >= 4){
                active = active.substring(0, 4);
            }
            if (active != null&& result.getString(result.getColumnIndex("final_game")) != null
                    && result.getString(result.getColumnIndex("final_game")).length() >= 4){
                active = active + " - " + result.getString(result.getColumnIndex("final_game")).substring(0, 4);
            }
            years.setText(active);

            final CursorRow player = new CursorRow(result, result.getPosition());
            LinearLayout LL = dynamicLayout.findViewById(R.id.playerSearchHorizontalLayout);
            LL.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent startIntent = new Intent(context, PlayerProfile.class);
                    startIntent.putExtra("CursorRow", player);
                    context.startActivity(startIntent);
                }
            });

            mainLayout.addView(dynamicLayout);
        }
    }

    /**
     * Sets the buttons for the add stats screens and processes the input from those screens
     * @param mainLayout - Layouts to get all the information from
     * @param button - Button object to set the on click listener for
     * @param buttonType - The actual button that was pressed: player profile, batting, pitching, fielding
     * @param homeType - The screen the button is pressed from: player, batting, pitching, or fielding
     * @param context - application context
     */
    public static void setAddOnClick(final LinearLayout mainLayout, Button button, final String buttonType, final String homeType, final Context context){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CursorRow cursorRow = addStatsToPlayer(mainLayout, homeType, context);
                if (buttonType.compareTo("player") == 0){
                    if (cursorRow != null) {
                        Intent startIntent = new Intent(context, PlayerProfile.class);
                        startIntent.putExtra("CursorRow", cursorRow);
                        context.startActivity(startIntent);
                    }
                }
                else if (buttonType.compareTo("batting") == 0){
                    if (cursorRow != null) {
                        Intent startIntent = new Intent(context, AddHitting.class);
                        startIntent.putExtra("CursorRow", cursorRow);
                        context.startActivity(startIntent);
                    }
                }
                else if (buttonType.compareTo("fielding") == 0){
                    if (cursorRow != null) {
                        Intent startIntent = new Intent(context, AddFielding.class);
                        startIntent.putExtra("CursorRow", cursorRow);
                        context.startActivity(startIntent);
                    }
                }
                else if (buttonType.compareTo("pitching") == 0){
                    if (cursorRow != null) {
                        Intent startIntent = new Intent(context, AddPitching.class);
                        startIntent.putExtra("CursorRow", cursorRow);
                        context.startActivity(startIntent);
                    }
                }
            }
        });
    }

    /**
     * Adds the stats entered in the given layout to the database
     * @param mainLayout - Layout to acquire the data from
     * @param homeType - The screen the layout is from: player, batting, pitching, or fielding
     * @param context - application context
     * @return The CursorRow with the result of the add, or just the player row
     */
    public static CursorRow addStatsToPlayer(LinearLayout mainLayout, String homeType, Context context){
        boolean addNulls = false;
        boolean empty = true;
        List<InsertStat> ret = new LinkedList<>();

        int count = mainLayout.getChildCount();
        for (int i = 0; i < count; i++){
            View childLayout = mainLayout.getChildAt(i);
            if (childLayout instanceof LinearLayout){
                String stat = "";
                String val = "";
                LinearLayout ll = (LinearLayout) childLayout;

                int childCount = ll.getChildCount();
                for (int j = 0; j < childCount; j++){
                    View text = ll.getChildAt(j);
                    if (text instanceof CheckBox){
                        CheckBox cb = (CheckBox) text;
                        addNulls = cb.isChecked();
                    }
                    else if (text instanceof Button){
                    }
                    else if (text instanceof EditText){
                        EditText et = (EditText) text;
                        val = et.getText().toString();
                    }
                    else {
                        TextView tv = (TextView) text;
                        stat = tv.getText().toString();
                    }
                }
                if (empty && val.compareTo("") != 0){
                   empty = false;
                }
                if (stat.compareTo("") != 0) {
                    ret.add(new InsertStat(homeType, stat, val));
                }
            }
        }

        if (empty){
            return null;
        }

        String playerId = "";
        String teamId = "";
        String firstName = "";
        String lastName = "";
        String year = "";
        String pos = "";

        Iterator<InsertStat> iterator = ret.iterator();
        while (iterator.hasNext()){
            InsertStat is = iterator.next();

            if (homeType.compareTo("player") != 0){
                if (is.getColumn().compareTo("player_id") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return null;
                    }
                }
                else if (is.getColumn().compareTo("year") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return playerTableRow(playerId, context);
                    }
                }
                else if (is.getColumn().compareTo("team_id") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return playerTableRow(playerId, context);
                    }
                }
                else if (homeType.compareTo("fielding") == 0 && is.getColumn().compareTo("pos") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return playerTableRow(playerId, context);
                    }
                }
            }
            else{
                if (is.getColumn().compareTo("first_name") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return null;
                    }
                }
                else if (is.getColumn().compareTo("last_name") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return null;
                    }
                }
            }
            //remove values from list if we aren't inserting them
            if (is.getColumn().compareTo("player_id") == 0){
                playerId = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().compareTo("team_id") == 0){
                teamId = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().compareTo("year") == 0){
                year = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().compareTo("first_name") == 0){
                firstName = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().compareTo("last_name") == 0){
                lastName = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().compareTo("pos") == 0){
                pos = is.getValue();
                iterator.remove();
            }
            else if (!addNulls){
                if (is.getValue().compareTo("") == 0){
                    iterator.remove();
                }
            }
        }

        DBHandler db = new DBHandler(context);
        int season = -1;
        if (year.compareTo("") != 0){
            season = Integer.parseInt(year);
        }
        Cursor result = db.insertPlayerData(playerId, firstName, lastName, season, teamId, pos, ret);
        result.moveToFirst();
        final CursorRow cursorRow = new CursorRow(result, result.getPosition());
        result.close();
        db.close();

        return cursorRow;
    }

    /**
     * Given a playerId, returns the row from the player table for that player
     * @param playerId - player_id from the player table
     * @param context - application context
     * @return A CursorRow containing the data from the player table for the given player
     */
    public static CursorRow playerTableRow(String playerId, Context context){
        DBHandler db = new DBHandler(context);
        Cursor result = db.playerTableQuery(playerId);
        result.moveToFirst();
        CursorRow cursorRow = new CursorRow(result, result.getPosition());
        result.close();
        db.close();
        return cursorRow;
    }

}
