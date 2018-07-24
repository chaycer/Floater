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

public class FloaterApplication extends Application{
    static CharSequence battingStats[] = new CharSequence[] {"player_id", "year", "team_id", "league_id", "g", "ab", "r", "h", "double", "triple", "hr", "rbi", "sb", "cs", "bb", "so", "ibb", "sh", "sf", "g_idp"};
    static CharSequence fieldingStats[] = new CharSequence[] {"player_id", "year", "team_id", "league_id", "pos", "g", "gs", "inn_outs", "po", "a", "e", "dp", "pb", "wp", "sb", "cs", "zr"};
    static CharSequence pitchingStats[] = new CharSequence[] {"player_id", "year", "team_id", "league_id", "w", "l", "g", "gs", "cg", "sho", "sv", "ipouts", "h", "er", "hr", "bb", "so", "baopp", "era", "ibb", "wp", "hbp", "bk", "bfp", "gf", "r", "sh", "sf", "g_idp"};

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
        result.close();
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
