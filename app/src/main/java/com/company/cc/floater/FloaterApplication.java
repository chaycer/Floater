package com.company.cc.floater;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
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
import java.util.zip.Inflater;

public class FloaterApplication extends Application{
    static CharSequence battingStats[] = new CharSequence[] {"player_id", "year", "team_id", "g", "ab", "r", "h", "double", "triple", "hr", "rbi", "sb", "cs", "bb", "so", "ibb", "hbp", "sh", "sf", "g_idp"};
    static CharSequence fieldingStats[] = new CharSequence[] {"player_id", "year", "team_id", "pos", "g", "gs", "inn_outs", "po", "a", "e", "dp", "pb", "wp", "sb", "cs", "zr"};
    static CharSequence pitchingStats[] = new CharSequence[] {"player_id", "year", "team_id", "w", "l", "g", "gs", "cg", "sho", "sv", "ip", "h", "er", "hr", "bb", "so", "baopp", "era", "ibb", "wp", "hbp", "bk", "bfp", "gf", "r", "sh", "sf", "g_idp"};
    static CharSequence pitchingStatsNoEra[] = new CharSequence[] {"player_id", "year", "team_id", "w", "l", "g", "gs", "cg", "sho", "sv", "ip", "h", "er", "hr", "bb", "so", "baopp", "ibb", "wp", "hbp", "bk", "bfp", "gf", "r", "sh", "sf", "g_idp"};

    static CharSequence playerStats[] = new CharSequence[] {"player_id", "name_first", "name_last", "birth_day", "birth_month", "birth_year", "birth_country", "death_day", "death_month", "death_year", "bats", "throws", "debut", "final_game"};
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

    public static CharSequence[] getPitchingStatsNoEra() { return  pitchingStatsNoEra; }

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
     * @param headers - Array of column names to make into subheaders
     * @return A list of all the lines (horizontal linear layouts) added
     */
    public static LinkedList<View> addStatsFromRow(LinearLayout mainLayout, LayoutInflater inflater, CursorRow row, String[] toExclude, boolean hide, View button, String[] headers){
        LinkedList<View> views = new LinkedList<>();
        for (int i = 0; i < row.getSize(); i++){
            boolean exclude = false;
            int layout = R.layout.stat_line_no_edit;

            // exclude rows based on toExclude array
            if (toExclude != null) {
                for (int j = 0; j < toExclude.length; j++) {
                    if (row.getColumnNameByIndex(i).compareTo(toExclude[j]) == 0) {
                        exclude = true;
                        break;
                    }
                }
            }
            if (exclude){
                continue;
            }

            // make rows in headers array look like headers
            if (headers != null){
                for (int j = 0; j < headers.length; j++) {
                    if (row.getColumnNameByIndex(i).compareTo(headers[j]) == 0) {
                        layout = R.layout.stat_line_no_edit_header;
                        break;
                    }
                }
            }
            // set label and value texts
            String nameString = row.getColumnNameByIndex(i);
            String valueString = row.getValueByIndex(i);

            View dynamicLayout = addLabelValue(inflater, layout, nameString, valueString, hide);

            // add to the vertical layout
            mainLayout.addView(dynamicLayout);
            views.add(dynamicLayout);
        }

        // add the button if needed
        if (button != null && !views.isEmpty()){
            views.add(0, button);
        }

        return views;
    }

    /**
     * Adds a label/value pair to a linear layout
     * @param inflater - Inflater to create new view
     * @param layout - layout of new view
     * @param nameString - Label name
     * @param valueString - Value name
     * @param hide - whether or not to hide the returned layout
     * @return new view with texts set
     */
    public static View addLabelValue(LayoutInflater inflater, int layout, String nameString,
                                     String valueString, boolean hide){
        View dynamicLayout = inflater.inflate(layout, null);
        TextView name = dynamicLayout.findViewById(R.id.statNameNoEditTextView);

        name.setText(nameString);

        TextView value = dynamicLayout.findViewById(R.id.statValueNoEditTextView);
        value.setText(valueString);

        // hide row if needed
        if (hide){
            dynamicLayout.setVisibility(View.GONE);
        }

        return dynamicLayout;
    }

    /**
     * Displays each player and years active from a cursor containing player information
     * @param mainLayout - layout to display each line on
     * @param inflater - LayoutInflater for the mainLayout
     * @param result - cursor containing data from the player table
     * @param context - context of the calling function
     */
    public static void addPlayerLines(LinearLayout mainLayout, LayoutInflater inflater, final Cursor result, final Context context){
        // if cursor has no data, display error and return
        if (result == null || (result.getCount() < 1)){
            addSingleTextView(inflater, mainLayout, null);
            try {
                result.close();
            } catch (NullPointerException ex){}
            return;
        }

        // add each row
        while (result.moveToNext()){
            View dynamicLayout = inflater.inflate(R.layout.player_search_layout, null);
            TextView name = dynamicLayout.findViewById(R.id.playerNameTextView);
            TextView years = dynamicLayout.findViewById(R.id.yearsActiveTextView);

            // if player is missing name for some reason, skip
            if(result.getString(result.getColumnIndex("name_first")).isEmpty()
                    || result.getString(result.getColumnIndex("name_last")).isEmpty()){
                continue;
            }

            // set player name and linkify it
            if (!result.getString(result.getColumnIndex("name_first")).isEmpty()) {
                SpannableString pName = linkifyString(
                        result.getString(result.getColumnIndex("name_first")) + " "
                                + result.getString(result.getColumnIndex("name_last")));
                name.setText(pName); // set label
            }
            // set years active text view based on debut and final game
            String active = result.getString(result.getColumnIndex("debut"));
            if (active != null && active.length() >= 4){
                active = active.substring(0, 4);
            }
            if (active != null&& result.getString(result.getColumnIndex("final_game")) != null
                    && result.getString(result.getColumnIndex("final_game")).length() >= 4){
                active = active + " - " + result.getString(result.getColumnIndex("final_game")).substring(0, 4);
            }
            years.setText(active);

            // add row to search results
            final CursorRow player = new CursorRow(result, result.getPosition());
            LinearLayout LL = dynamicLayout.findViewById(R.id.playerSearchHorizontalLayout);

            // make clicking row launch player profile
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
    public static void setAddOnClick(final LinearLayout mainLayout, Button button, final String buttonType,
                                     final String homeType, final Context context, final LayoutInflater inflater){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add stats to database or display error and return if failed
                CursorRow cursorRow = addStatsToPlayer(mainLayout, homeType, context, inflater);
                if (cursorRow == null || cursorRow.isEmpty()){
                    return;
                }
                // set each button type
                if (buttonType.compareTo("player") == 0){
                    Intent startIntent = new Intent(context, PlayerProfile.class);
                    startIntent.putExtra("CursorRow", cursorRow);
                    context.startActivity(startIntent);
                }
                else if (buttonType.compareTo("batting") == 0){
                    Intent startIntent = new Intent(context, AddHitting.class);
                    startIntent.putExtra("playerId", cursorRow.getValueByColumnName("player_id"));
                    context.startActivity(startIntent);
                }
                else if (buttonType.compareTo("fielding") == 0){
                    Intent startIntent = new Intent(context, AddFielding.class);
                    startIntent.putExtra("playerId", cursorRow.getValueByColumnName("player_id"));
                    context.startActivity(startIntent);
                }
                else if (buttonType.compareTo("pitching") == 0){
                    Intent startIntent = new Intent(context, AddPitching.class);
                    startIntent.putExtra("playerId", cursorRow.getValueByColumnName("player_id"));
                    context.startActivity(startIntent);
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
    public static CursorRow addStatsToPlayer(LinearLayout mainLayout, String homeType, Context context, LayoutInflater inflater){
        boolean addNulls = false; // do we overwrite existing values with null?
        boolean empty = true; // were stats not added to player?
        List<InsertStat> ret = new LinkedList<>(); // list of stats to insert in queryable form

        int count = mainLayout.getChildCount();
        // loop through all the fields in the layout to get user input
        for (int i = 0; i < count; i++){
            View childLayout = mainLayout.getChildAt(i);
            if (childLayout instanceof LinearLayout){
                String stat = "";
                String val = "";
                LinearLayout ll = (LinearLayout) childLayout;

                int childCount = ll.getChildCount();
                // loop through all the fields in the linear layout to get the values
                for (int j = 0; j < childCount; j++){
                    View text = ll.getChildAt(j);
                    if (text instanceof CheckBox){ // add nulls checkbox
                        CheckBox cb = (CheckBox) text;
                        addNulls = cb.isChecked();
                    }
                    else if (text instanceof Button){
                        continue;
                    }
                    else if (text instanceof EditText){ // user input
                        EditText et = (EditText) text;
                        val = et.getText().toString();
                        // if input is invalid, display error and return
                        if (!FloaterApplication.validString(val)){
                            View dynamicLayout = inflater.inflate(R.layout.error_layout, null);
                            TextView tv = dynamicLayout.findViewById(R.id.errorText);
                            tv.setText(context.getString(R.string.badChars));
                            mainLayout.addView(dynamicLayout);
                            return null;
                        }
                    }
                    else { // stat label
                        TextView tv = (TextView) text;
                        stat = tv.getText().toString();
                    }
                }
                if (empty && !val.equals("")){ // user inputted something
                   empty = false;
                }
                if (!stat.equals("")) {
                    ret.add(new InsertStat(homeType, stat, val));
                }
            }
        }

        if (empty){ // don't do anything if the user didn't input anything
            return null;
        }

        // set up variables to be sent to query
        String playerId = "";
        String teamId = "";
        String firstName = "";
        String lastName = "";
        String year = "";
        String pos = "";

        // loop through stats to validate input and pull out key values
        Iterator<InsertStat> iterator = ret.iterator();
        while (iterator.hasNext()){
            InsertStat is = iterator.next();

            // if this is a stat entry screen, verify we have a player id
            if (!homeType.equals("player")){
                String error = context.getString(R.string.statInvalid);
                if (homeType.equals("fielding")){
                    error = context.getString(R.string.fieldingInvalid);
                }
                if (is.getColumn().equals("player_id")){ // no player id is an error
                    if (is.getValue().equals("")){
                        addError(mainLayout, inflater, error);
                        return null;
                    }
                }
                else if (is.getColumn().equals("year")){ //missing other values is ok - we just won't actually input anything (eases navigation)
                    if (is.getValue().equals("")){
                        return playerTableRow(playerId, context);
                    }
                }
                else if (is.getColumn().equals("team_id")){
                    if (is.getValue().equals("")){
                        return playerTableRow(playerId, context);
                    }
                }
                // fielding is special since pos is also part of the key
                else if (homeType.equals("fielding") && is.getColumn().equals("pos")){
                    if (is.getValue().equals("")){
                        return playerTableRow(playerId, context);
                    }
                }
            }
            else{ // player entry screen, first and last name OR existing player ID is required
                if (is.getColumn().equals("name_first")){
                    // if the name is missing, we need an existing ID
                    if (is.getValue().equals("")){
                        if (!playerId.equals("")){
                            //check if inputted ID already exists, display error if not
                            CursorRow row = playerTableRow(playerId, context);
                            if (row ==  null || row.getValueByColumnName("name_first").equals("")){
                                addError(mainLayout, inflater, context.getString(R.string.playerInvalid));
                                return null;
                            }
                        }
                        // didn't input name or ID, that's a paddlin'
                        else{
                            addError(mainLayout, inflater, context.getString(R.string.playerInvalid));
                            return null;
                        }

                    }
                }
                else if (is.getColumn().equals("name_last")){
                    // if the name is missing, we need an existing ID
                    if (is.getValue().equals("")){
                        if (!playerId.equals("")){
                            //check if inputted ID already exists, display error if not
                            CursorRow row = playerTableRow(playerId, context);
                            if (row ==  null || row.getValueByColumnName("name_first").equals("")){
                                addError(mainLayout, inflater, context.getString(R.string.playerInvalid));
                                return null;
                            }
                        }
                        // didn't input name or ID, that's a paddlin'
                        else {
                            addError(mainLayout, inflater, context.getString(R.string.playerInvalid));
                            return null;
                        }
                    }
                }
            }
            //remove values from list that we aren't inserting (keys and potentially nulls)
            if (is.getColumn().equals("player_id")){
                playerId = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().equals("team_id")){
                teamId = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().equals("year")){
                year = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().equals("name_first")){
                firstName = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().equals("name_last")){
                lastName = is.getValue();
                iterator.remove();
            }
            else if (is.getColumn().equals("pos")){
                pos = is.getValue();
                iterator.remove();
            }
            else if (!addNulls){
                if (is.getValue().equals("")){
                    iterator.remove();
                }
            }
        }

        // if our list is empty, we should only update if we are updating the player name
        if (ret.isEmpty()){
            if (homeType.equals("player")){
                // if player ID is null, we need both a first and a last name
                if ((playerId.equals("") && (firstName.equals("") || lastName.equals("")))){
                    return null;
                }
                // if the ID is not null, we need one at least one of the names, else we'll just move on to the next screen
                if (!playerId.equals("") && firstName.equals("") && lastName.equals("")){
                    return playerTableRow(playerId, context);
                }
            }
            else{
                if (!playerId.equals("")){
                    return playerTableRow(playerId, context);
                }
                return null;
            }
        }

        //create DBHandler and query database
        DBHandler db = new DBHandler(context);
        int season = -1;
        if (year.compareTo("") != 0){
            season = Integer.parseInt(year);
        }
        Cursor result = db.insertPlayerData(playerId, firstName, lastName, season, teamId, pos, ret, homeType);
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
        if (result.getCount() < 1){
            return null;
        }
        result.moveToFirst();
        CursorRow cursorRow = new CursorRow(result, result.getPosition());
        result.close();
        db.close();
        return cursorRow;
    }

    /**
     * Adds a single text view to a vertical linear layout.
     * @param inflater - inflater to inflate
     * @param mainLayout - Vertical linear layout to add the text view to
     * @param header - String to set the view to. If null, will show "no results found"
     */
    public static void addSingleTextView(LayoutInflater inflater, LinearLayout mainLayout, String header){
        final View tableName = inflater.inflate(R.layout.no_results, mainLayout);
        if (header != null) {
            TextView tv = tableName.findViewById(R.id.noResultsTextView);
            tv.setText(header);
        }
    }

    /**
     * Creates a layout containing a Go to Team Profile button and adds to a stat layout
     * @param mainLayout - Vertical linear layout to add button to
     * @param inflater - inflater for activity
     * @param teamId - ID of team
     * @param context - application context
     * @param year - year of team for pulling the name, can be null
     * @return A new horizontal linear layout containing a team profile button
     */
    public static View startTeamProfileButton(LinearLayout mainLayout, LayoutInflater inflater,
                                              final String teamId, final Context context, final String year){
        View dynamicLayout = inflater.inflate(R.layout.single_button, null);
        Button button = dynamicLayout.findViewById(R.id.blankButton);
        button.setText("Go to Team Profile");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Intent startIntent = new Intent(context, TeamProfile.class);
                startIntent.putExtra("team_id", teamId);
                startIntent.putExtra("year", year);
                context.startActivity(startIntent);
            }
        });
        dynamicLayout.setVisibility(View.GONE);
        mainLayout.addView(dynamicLayout);

        return dynamicLayout;
    }

    /**
     * Returns a list containing a button horizontal layout in case this is needed in list form
     * @param mainLayout - Vertical linear layout to add button to
     * @param inflater - inflater for activity
     * @param teamId - ID of team
     * @param context - application context
     * @param year - year of team for pulling the name, can be null
     * @return a list containing a single entry with a horizontal layout containing a button
     */
    public static LinkedList<View> singleButtonList(LinearLayout mainLayout, LayoutInflater inflater, String teamId,
                                                    Context context, String year){
        LinkedList<View> list = new LinkedList<>();
        list.add(startTeamProfileButton(mainLayout, inflater, teamId, context, year));
        return list;
    }

    /**
     * Makes a string look like a link by underlining it
     * @param link - String to underline
     * @return the underlined string
     */
    public static SpannableString linkifyString(String link){
        SpannableString underline = new SpannableString(link);
        underline.setSpan(new UnderlineSpan(), 0, underline.length(), 0);
        return underline;
    }

    /**
     * Checks to see if a string is valid (only uses alphanumeric characters, period, or space
     * @param string - the string to check
     * @return true if the string is valid, false if not
     */
    public static boolean validString(String string){
        for (int i = 0; i < string.length(); i++){
            char c = string.charAt(i);
            if (!Character.isLetterOrDigit(c)){
                if (c != '.' && c != ' '){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Adds the error string to a linear layout
     * @param mainLayout- linear layout to add error to
     * @param inflater - inflater for activity
     * @param error - error to display
     */
    public static void addError(LinearLayout mainLayout, LayoutInflater inflater, String error){
        View dynamicLayout = inflater.inflate(R.layout.error_layout, null);
        TextView tv = dynamicLayout.findViewById(R.id.errorText);
        tv.setText(error);
        mainLayout.addView(dynamicLayout);
    }

    public static void createAddStatsButton(LinearLayout mainLayout, LayoutInflater inflater,
                                            final int type, final Context context, final String playerId){
        View dynamicLayout = inflater.inflate(R.layout.single_button, null);
        Button button = dynamicLayout.findViewById(R.id.blankButton);
        button.setText("Add/edit Player Stats");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Class statScreen;
                if (type == BATTING){
                    statScreen = AddHitting.class;
                }
                else if (type == PITCHING){
                    statScreen = AddPitching.class;
                }
                else if (type == PLAYER){
                    statScreen = AddPlayer.class;
                }
                else {
                    statScreen = AddFielding.class;
                }

                Intent startIntent = new Intent(context, statScreen);
                startIntent.putExtra("playerId", playerId);
                context.startActivity(startIntent);
            }
        });
        mainLayout.addView(dynamicLayout);
    }

    /**
     * Set up an add/edit stats screen
     * @param mainLayout - Layout to add views to
     * @param activity - Activity screen
     * @param stats - array of stats to display
     * @param homeType - type of screen (player, hitting, fielding, pitching)
     * @param context - application context
     */
    public static void setupAddStatsScreen(LinearLayout mainLayout, Activity activity,
                                           CharSequence[] stats, String homeType, Context context){
        // Add blank stat lines
        LayoutInflater inflater = activity.getLayoutInflater();
        addStatLines(mainLayout, inflater, stats);

        // Get player ID and set the ID editText
        Bundle bundle = activity.getIntent().getExtras();
        if (bundle != null) {
            String playerId = bundle.getString("playerId");
            if (playerId != null) {
                LinearLayout ll = (LinearLayout) mainLayout.getChildAt(2);
                EditText idEdit = (EditText) ll.getChildAt(1);
                idEdit.setText(playerId);
            }
        }

        // add buttons
        View save = inflater.inflate(R.layout.save_changes_button, null);
        View add = inflater.inflate(R.layout.add_season_stats, null);

        Button saveButton = save.findViewById(R.id.saveChangesButton);
        setAddOnClick(mainLayout, saveButton, "player", homeType, context, inflater);

        Button hittingButton = add.findViewById(R.id.hittingAddButton);
        setAddOnClick(mainLayout, hittingButton, "batting", homeType, context, inflater);

        Button fieldingButton = add.findViewById(R.id.fieldingAddButton);
        setAddOnClick(mainLayout, fieldingButton, "fielding", homeType, context, inflater);

        Button pitchingButton = add.findViewById(R.id.pitchingAddButton);
        setAddOnClick(mainLayout, pitchingButton, "pitching", homeType, context, inflater);

        mainLayout.addView(save);
        mainLayout.addView(add);
    }
}
