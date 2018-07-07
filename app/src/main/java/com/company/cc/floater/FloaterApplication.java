package com.company.cc.floater;

import android.app.Application;

public class FloaterApplication extends Application{
    CharSequence battingStats[] = new CharSequence[] {"player_id", "year", "team_id", "league_id", "g", "ab", "r", "h", "double", "triple", "hr", "rbi", "sb", "cs", "bb", "so", "ibb", "sh", "sf", "g_idp"};
    CharSequence fieldingStats[] = new CharSequence[] {"player_id", "year", "team_id", "league_id", "pos", "g", "gs", "inn_outs", "po", "a", "e", "dp", "pb", "wp", "sb", "cs", "zr"};
    CharSequence pitchingStats[] = new CharSequence[] {"player_id", "year", "team_id", "league_id", "w", "l", "g", "gs", "cg", "sho", "sv", "ipouts", "h", "er", "hr", "bb", "so", "baopp", "era", "ibb", "wp", "hbp", "bk", "bfp", "gf", "r", "sh", "sf", "g_idp"};
    CharSequence operators[] = new CharSequence[] {"=", "<", ">", "<=", ">="};

    int BATTING = 1;
    int FIELDING = 2;
    int PITCHING = 3;

    public CharSequence[] getBattingStats() {
        return battingStats;
    }

    public CharSequence[] getFieldingStats() {
        return fieldingStats;
    }

    public CharSequence[] getPitchingStats() {
        return pitchingStats;
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
}
