package com.company.cc.floater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.company.cc.floater.FloaterApplication.addStatsFromRow;

/**
 * Async task to generate results of a stat search
 */
public class AddStatSearchAsync extends AsyncTask<Object, Boolean, Boolean> {

    /**
     * Runs stat search and displays results
     * @param params - LinearLayout, LayoutInflater, SerialList, Context, Integer, Activity
     * @return
     */
    protected Boolean doInBackground(Object... params){
        if (params.length != 6){
            return false;
        }
        addStatSearchLines((LinearLayout) params[0], (LayoutInflater) params[1], (SerialList) params[2],
                (Context) params[3], (Integer) params[4], (Activity) params[5]);
        return true;
    }

    /**
     * Adds 100 results of a stat search to a screen
     * @param mainLayout - layout to add results to
     * @param inflater - inflater to add new views
     * @param filters - the list of filters to search on
     * @param context - application context
     * @param count - current position in the cursor
     * @param activity - activity where the results are being added
     */
    public static void addStatSearchLines(final LinearLayout mainLayout, final LayoutInflater inflater,
                                          final SerialList filters, final Context context, int count, Activity activity){
        String[] exclude = {"player_id", "name_first", "name_last", "year", "team_id", "pos"}; // columns to exclude

        DBHandler db = new DBHandler(context);
        Cursor result = db.filterSearchQuery(filters.getList());

        result.moveToPosition(count);
        int max = count + 100; // load the next 100 rows

        // display no results message if nothing is returned
        if (result == null || (result.getCount() < 1)){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FloaterApplication.addSingleTextView(inflater, mainLayout, null);
                }
            });
            if (result == null) {
                result.close();
            }
            db.close();
            return;
        }

        // display each result
        while (result.moveToNext() && count <= max){
            final CursorRow player = new CursorRow(result, result.getPosition(), true);
            final View dynamicLayout = inflater.inflate(R.layout.stat_return_layout, mainLayout, false);
            TextView name = dynamicLayout.findViewById(R.id.playerNameTextView);
            TextView team = dynamicLayout.findViewById(R.id.teamTextView);
            TextView year = dynamicLayout.findViewById(R.id.yearTextView);
            TextView pos = dynamicLayout.findViewById(R.id.positionTextView);

            // player has to have a name
            if(player.getValueByColumnName("name_first").isEmpty()
                    || player.getValueByColumnName("name_last").isEmpty()){
                continue;
            }

            // concatenate first and last names
            if (!player.getValueByColumnName("name_first").isEmpty()) {
                name.setText(FloaterApplication.linkifyString(player.getValueByColumnName("name_first") + " "
                        + player.getValueByColumnName("name_last"))); // set label
            }

            // make clicking name go to player profile
            name.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent startIntent = new Intent(context, PlayerProfile.class);
                    startIntent.putExtra("CursorRow", player);
                    context.startActivity(startIntent);
                }
            });

            // show team and make clicking team name go to team profile
            team.setText(FloaterApplication.linkifyString(player.getValueByColumnName("team_id")));
            team.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent startIntent = new Intent(context, TeamProfile.class);
                    startIntent.putExtra("team_id", player.getValueByColumnName("team_id"));
                    startIntent.putExtra("year", player.getValueByColumnName("year"));
                    context.startActivity(startIntent);
                }
            });

            // display year and also position, if we have it
            year.setText(player.getValueByColumnName("year"));
            pos.setText(player.getValueByColumnName("pos"));
            if (pos.getText() != null){
                pos.setVisibility(View.VISIBLE);
            }

            LinearLayout verticalLayout = dynamicLayout.findViewById(R.id.statResultsVerticalLayout);

            // display stats on screen
            addStatsFromRow(verticalLayout, inflater, player, exclude, false, null, null);
            activity.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   mainLayout.addView(dynamicLayout);
               }
             });


            // add load more button after 100 rows
            if (++count > max){
                final int retCount = count -1;
                activity.runOnUiThread(new Runnable() {
                    public void run() {

                        View buttonLayout = inflater.inflate(R.layout.load_more_button, mainLayout, false);
                        Button loadButton = buttonLayout.findViewById(R.id.loadMoreButton);
                        mainLayout.addView(buttonLayout);

                        loadButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent startIntent = new Intent(context, StatSearchResults.class);
                                startIntent.putExtra("filters", filters);
                                startIntent.putExtra("count", String.format("%s", retCount));
                                context.startActivity(startIntent);
                            }
                        });
                    }
                });
            }
        }

        result.close();
        db.close();
    }
}
