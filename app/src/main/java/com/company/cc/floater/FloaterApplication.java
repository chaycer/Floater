package com.company.cc.floater;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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

    public static void addStatLines(LinearLayout mainLayout, LayoutInflater inflater, CharSequence[] stats){
        for (int i = 0; i < stats.length; i++){
            View dynamicLayout = inflater.inflate(R.layout.stat_line, null);
            TextView tv = dynamicLayout.findViewById(R.id.statNameTextView);
            EditText et = dynamicLayout.findViewById(R.id.statInputEditText);

            tv.setText(stats[i]); // set label
            mainLayout.addView(dynamicLayout);
        }
    }

    public static void setSaveButton(Button saveButton, final Context context){
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, PlayerProfile.class);
                context.startActivity(startIntent);
            }
        });
    }

    public static void setHittingButton(Button hittingButton, final Context context){
        hittingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, AddHitting.class);
                context.startActivity(startIntent);
            }
        });
    }

    public static void setFieldingButton(Button fieldingButton, final Context context){
        fieldingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, AddFielding.class);
                context.startActivity(startIntent);
            }
        });
    }

    public static void setPitchingButton(Button pitchingButton, final Context context){
        pitchingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(context, AddPitching.class);
                context.startActivity(startIntent);
            }
        });
    }
}
