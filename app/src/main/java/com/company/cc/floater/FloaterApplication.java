package com.company.cc.floater;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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


    public static LinkedList<View> addStatsFromRow(LinearLayout mainLayout, LayoutInflater inflater, CursorRow row, String[] toExclude, boolean hide){
        LinkedList<View> views = new LinkedList<>();
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

    public static void addPlayerStatsFromCursor(LinearLayout mainLayout, LayoutInflater inflater, String playerId, int type, Context context){

        DBHandler db = new DBHandler(context);
        Cursor playerTeams = db.playerTeamsQuery(playerId, null);

        LinkedList<CursorRow> rowList = new LinkedList<CursorRow>();
        while (playerTeams.moveToNext()){
            rowList.add(new CursorRow(playerTeams, playerTeams.getPosition()));
        }
        playerTeams.close();
        Iterator<CursorRow> iterator = rowList.iterator();
        while (iterator.hasNext()){

            // First, generate the headers
            CursorRow row = iterator.next();
            final View dynamicLayout = inflater.inflate(R.layout.key_header, null);

            String year = row.getValueByIndex(0);
            String teamId = row.getValueByIndex(1);

            TextView name = dynamicLayout.findViewById(R.id.keyHeaderTeam);
            name.setText(year);

            TextView value = dynamicLayout.findViewById(R.id.keyHeaderYear);
            value.setText(teamId);

            String stats = null;
            String[] exclude = {"player_id", "year", "team_id"};
            String[] fieldExclude = {"player_id", "year", "team_id", "pos"};
            if (type == BATTING){
                stats = battingStatsColumns();
            }
            else if (type == PITCHING){
                stats = pitchingStatsColumns();
            }
            else if (type == FIELDING){
                stats = fieldingStatsColumns();
            }

            // Now, generate the individual stat lines
            Cursor playerStats = db.playerStatsQuery(playerId, Integer.parseInt(year), null, stats);


            LinearLayout LL = dynamicLayout.findViewById(R.id.keyHeaderVertical);
            final LinkedList<LinkedList<View>> hiddenViews = new LinkedList<>();


            if (playerStats.moveToNext()){ // changing to if instead of while since sometimes duplicates get returned
                CursorRow statRow = new CursorRow(playerStats, playerStats.getPosition(), true);
                if (type == FIELDING) {
                    hiddenViews.add(addStatsFromRow(LL, inflater, statRow, fieldExclude, true));
                    TextView pos = dynamicLayout.findViewById(R.id.keyHeaderPos);
                    pos.setText(statRow.getValueByColumnName("fielding.pos"));
                    pos.setVisibility(View.VISIBLE);
                }
                else{
                    hiddenViews.add(addStatsFromRow(LL, inflater, statRow, exclude, true));
                }
            }
            CursorRow statRow = new CursorRow(playerStats, playerStats.getPosition(), true);

            if (type == FIELDING) {
                hiddenViews.add(addStatsFromRow(LL, inflater, statRow, fieldExclude, true));
                TextView pos = dynamicLayout.findViewById(R.id.keyHeaderPos);
                pos.setText(statRow.getValueByColumnName("fielding.pos"));
                pos.setVisibility(View.VISIBLE);
            }
            else{
                hiddenViews.add(addStatsFromRow(LL, inflater, statRow, exclude, true));
            }

            dynamicLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (v == dynamicLayout) {
                        Iterator<LinkedList<View>> viewIterator = hiddenViews.iterator();
                        while (viewIterator.hasNext()){
                            LinkedList<View> viewList = viewIterator.next();
                            Iterator<View> listIterator = viewList.iterator();
                            while (listIterator.hasNext()) {
                                View nextView = listIterator.next();
                                nextView.setVisibility(nextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                            }
                        }
                    }
                }
            });

            playerStats.close();

            mainLayout.addView(dynamicLayout);

        }

        db.close();

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
                name.setText(result.getString(result.getColumnIndex("name_first")) + " "
                        + result.getString(result.getColumnIndex("name_last"))); // set label
            }
            String active = result.getString(result.getColumnIndex("debut"));
            if (!active.isEmpty() && active.length() >= 4){
                active = active.substring(0, 4);
            }
            if (!active.isEmpty() && !result.getString(result.getColumnIndex("final_game")).isEmpty()
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

    public static void addStatSearchLines(LinearLayout mainLayout, LayoutInflater inflater, final List<FilterSearch> filters, final Context context, int count){
        DBHandler db = new DBHandler(context);
        Cursor result = db.filterSearchQuery(filters);
        String[] exclude = {"player_id", "name_first", "name_last", "year", "team_id", "pos"};
        result.moveToPosition(count);
        int max = count + 100;

        // TODO: show "no results" if cursor empty
        if (result == null || (result.getCount() < 1)){

            result.close();
            db.close();
            return;
        }

        while (result.moveToNext() && count <= max){
            final CursorRow player = new CursorRow(result, result.getPosition(), true);
            View dynamicLayout = inflater.inflate(R.layout.stat_return_layout, null);
            TextView name = dynamicLayout.findViewById(R.id.playerNameTextView);
            TextView team = dynamicLayout.findViewById(R.id.teamTextView);
            TextView year = dynamicLayout.findViewById(R.id.yearTextView);
            TextView pos = dynamicLayout.findViewById(R.id.positionTextView);

            if(player.getValueByColumnName("name_first").isEmpty()
                    || player.getValueByColumnName("name_last").isEmpty()){
                continue;
            }

            if (!player.getValueByColumnName("name_first").isEmpty()) {
                name.setText(player.getValueByColumnName("name_first") + " "
                        + player.getValueByColumnName("name_last")); // set label
            }

            team.setText(player.getValueByColumnName("team_id"));
            year.setText(player.getValueByColumnName("year"));
            pos.setText(player.getValueByColumnName("pos"));
            if (pos.getText() != null){
                pos.setVisibility(View.VISIBLE);
                //mainLayout.findViewById(R.id.playerSearchPositionHeader).setVisibility(View.VISIBLE);
            }

            LinearLayout LL = dynamicLayout.findViewById(R.id.statResultsHorizontalLayout);
            LL.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent startIntent = new Intent(context, PlayerProfile.class);
                    startIntent.putExtra("CursorRow", player);
                    context.startActivity(startIntent);
                }
            });

            LinearLayout verticalLayout = dynamicLayout.findViewById(R.id.statResultsVerticalLayout);

            addStatsFromRow(verticalLayout, inflater, player, exclude, false);
            mainLayout.addView(dynamicLayout);

            // add load more button after 100 rows
            if (++count > max){
                View buttonLayout = inflater.inflate(R.layout.load_more_button, null);
                Button loadButton = buttonLayout.findViewById(R.id.loadMoreButton);
                mainLayout.addView(buttonLayout);

                loadButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent startIntent = new Intent(context, StatSearchResults.class);
                        startIntent.putExtra("filters", (StatSearch.serialList) filters);
                        startIntent.putExtra("count", "count - 1");
                        context.startActivity(startIntent);
                    }
                });
            }
        }
        result.close();
        db.close();
    }

    /**
     * Sets up the save button that will take the user to the player profile after saving to
     * the database
     * @param saveButton - Button to set up
     * @param context - context of the calling function
     */
    public static void setSaveButton(Button saveButton, final Context context){
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, PlayerProfile.class);
                context.startActivity(startIntent);
            }
        });
    }

    /**
     * Sets up the save button that will take the user to the hitting stats entry page after saving to
     * the database
     * @param hittingButton - Button to set up
     * @param context - context of the calling function
     */
    public static void setHittingButton(Button hittingButton, final Context context){
        hittingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, AddHitting.class);
                context.startActivity(startIntent);
            }
        });
    }

    /**
     * Sets up the save button that will take the user to the fielding stats entry page after saving to
     * the database
     * @param fieldingButton - Button to set up
     * @param context - context of the calling function
     */
    public static void setFieldingButton(Button fieldingButton, final Context context){
        fieldingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, AddFielding.class);
                context.startActivity(startIntent);
            }
        });
    }

    /**
     * Sets up the save button that will take the user to the pitching stats entry page after saving to
     * the database
     * @param pitchingButton - Button to set up
     * @param context - context of the calling function
     */
    public static void setPitchingButton(Button pitchingButton, final Context context){
        pitchingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, AddPitching.class);
                context.startActivity(startIntent);
            }
        });
    }
}
