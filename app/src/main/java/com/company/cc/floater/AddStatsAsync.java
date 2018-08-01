package com.company.cc.floater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;

import static android.view.View.GONE;
import static com.company.cc.floater.FloaterApplication.BATTING;
import static com.company.cc.floater.FloaterApplication.FIELDING;
import static com.company.cc.floater.FloaterApplication.PITCHING;
import static com.company.cc.floater.FloaterApplication.addStatsFromRow;

public class AddStatsAsync extends AsyncTask<Object, Boolean, Boolean> {

    protected Boolean doInBackground(Object... params){
        if (params.length < 6){
            return false;
        }
        addStats((LinearLayout) params[0], (LayoutInflater) params[1], (String) params[2],
                (Integer) params[3], (Context) params[4], (Activity) params[5]);
        return true;
    }

    private static void addStats(final LinearLayout mainLayout, final LayoutInflater inflater, final String playerId, final int type, final Context context, Activity activity){

        DBHandler db = new DBHandler(context);
        // Get teams/years the player played on
        Cursor playerTeams = db.playerTeamsQuery(playerId, null);

        // Make a list of all the team/year pairs
        LinkedList<CursorRow> rowList = new LinkedList<>();
        while (playerTeams.moveToNext()){
            rowList.add(new CursorRow(playerTeams, playerTeams.getPosition()));
        }
        playerTeams.close();

        boolean hasItems = false;

        String table = null;
        String header = null;
        if (type == BATTING){
            table = "batting";
            header = "Batting Stats";
        }
        else if (type == PITCHING){
            table = "pitching";
            header = "Pitching Stats";
        }
        else if (type == FIELDING){
            table = "fielding";
            header = "Fielding Stats";
        }

        final String pageHeader = header;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloaterApplication.addSingleTextView(inflater, mainLayout, pageHeader);
            }
        });


        // generate stats for each season
        for (CursorRow row: rowList){
            boolean item = generateSeason(mainLayout, row, inflater, db, playerId, table, type, context, activity, false);
            if (!hasItems){
                hasItems = item;
            }
        }

        if (!hasItems){ // display no results message if no values for given table
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FloaterApplication.addSingleTextView(inflater, mainLayout, null);
                }
            });
        }
        else{ // Generate career stats for table
            generateSeason(mainLayout, rowList.get(0), inflater, db, playerId, table, type, context, activity, true);
        }

        db.close();

        // set edit stats button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    /**
     * Generate a season's worth of stats and show on screen
     * @param mainLayout - Vertical linear layout to add to
     * @param row - team/year label/value pair in CursorRow form
     * @param inflater - inflater to generate view
     * @param db - DBHandler to get values from database
     * @param playerId - player's ID
     * @param table - Table to pull from
     * @param type - batting, pitching, or fielding?
     * @param context - activity context
     * @param activity - activity screen
     * @param career - true to show career values, false for single season
     * @return true if any stats added, false otherwise
     */
    private static boolean generateSeason(final LinearLayout mainLayout, CursorRow row, LayoutInflater inflater,
                                       DBHandler db, String playerId, String table, int type,
                                       Context context, Activity activity, boolean career){
        boolean hasItems = false;
        String[] exclude = {"player_id", "year", "team_id"};
        String[] fieldExclude = {"player_id", "year", "team_id"};
        String[] headerRow = {"pos"};

        // First, generate the headers
        final View dynamicLayout = inflater.inflate(R.layout.key_header, null);

        String year = row.getValueByIndex(0);
        final String teamId = row.getValueByIndex(1);

        TextView name = dynamicLayout.findViewById(R.id.keyHeaderTeam);
        TextView value = dynamicLayout.findViewById(R.id.keyHeaderYear);
        Cursor playerStats;
        if (career){
            name.setText("Career Stats");
            value.setVisibility(View.GONE);
            playerStats = db.careerStats(playerId, table);
        }
        else {
            name.setText(year);
            value.setText(teamId);
            playerStats = db.playerStatsQuery(playerId, Integer.parseInt(year), teamId, table);
        }

        final LinearLayout LL = dynamicLayout.findViewById(R.id.keyHeaderVertical);
        final LinkedList<LinkedList<View>> hiddenViews = new LinkedList<>();

        // Add stat lines to view
        while (playerStats.moveToNext()){
            CursorRow statRow = new CursorRow(playerStats, playerStats.getPosition(), true);
            if (type == FIELDING) {
                hiddenViews.add(addStatsFromRow(LL, inflater, statRow, fieldExclude, true, null, headerRow));
            }
            else{
                hiddenViews.add(addStatsFromRow(LL, inflater, statRow, exclude, true, null, null));
                break; // prevent duplicate rows
            }
        }

        if (!hiddenViews.isEmpty()){
            hasItems = true;
            if (!career) {
                hiddenViews.add(0, FloaterApplication.singleButtonList(LL, inflater, teamId, context, year));
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dynamicLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (v == dynamicLayout) {
                                for (LinkedList<View> viewList: hiddenViews){
                                    for (View nextView: viewList){
                                        nextView.setVisibility(nextView.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                    mainLayout.addView(dynamicLayout);
                }
            });
        }
        playerStats.close();
        return hasItems;
    }
}
