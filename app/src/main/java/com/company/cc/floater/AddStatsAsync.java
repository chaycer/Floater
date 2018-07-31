package com.company.cc.floater;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;

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

    public static void addStats(final LinearLayout mainLayout, final LayoutInflater inflater, String playerId, int type, Context context, Activity activity){

        DBHandler db = new DBHandler(context);
        Cursor playerTeams = db.playerTeamsQuery(playerId, null);

        LinkedList<CursorRow> rowList = new LinkedList<CursorRow>();
        while (playerTeams.moveToNext()){
            rowList.add(new CursorRow(playerTeams, playerTeams.getPosition()));
        }
        playerTeams.close();
        Iterator<CursorRow> iterator = rowList.iterator();

        boolean hasItems = false;

        String table = null;
        String header = null;
        String[] exclude = {"player_id", "year", "team_id"};
        String[] fieldExclude = {"player_id", "year", "team_id"};
        String[] headerRow = {"pos"};
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

            // Now, generate the individual stat lines
            Cursor playerStats = db.playerStatsQuery(playerId, Integer.parseInt(year), teamId, table);

            LinearLayout LL = dynamicLayout.findViewById(R.id.keyHeaderVertical);
            final LinkedList<LinkedList<View>> hiddenViews = new LinkedList<>();


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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dynamicLayout.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (v == dynamicLayout) {
                                    Iterator<LinkedList<View>> viewIterator = hiddenViews.iterator();
                                    while (viewIterator.hasNext()) {
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
                        mainLayout.addView(dynamicLayout);
                    }
                });
            }
            playerStats.close();
        }
        db.close();

        if (!hasItems){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FloaterApplication.addSingleTextView(inflater, mainLayout, null);
                }
            });
        }
    }
}
