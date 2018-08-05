package com.company.cc.floater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;

import static com.company.cc.floater.FloaterApplication.addStatsFromRow;

/**
 * Async task to create a team profile
 */
public class AddTeamProfileAsync extends AsyncTask<Object, Boolean, Boolean> {

    /**
     * Runs background task to update profile
     * @param params - LinearLayout, LayoutInflater, Cursor, Context, String, String, Activity, DBHandler, Integer
     * @return true if successful, false if not
     */
    protected Boolean doInBackground(Object... params){
        if (params.length != 9){
            return false;
        }
        addTeamInfo((LinearLayout) params[0], (LayoutInflater) params[1], (Cursor) params[2],
                (Context) params[3], (String) params[4], (String) params[5], (Activity) params[6], (DBHandler) params[7], (Integer) params[8]);
        return true;
    }

    /**
     * Given a cursor with team info, displays all the info
     * @param mainLayout - Vertical layout to add info to
     * @param inflater - inflater to generate new views
     * @param result - cursor containing the information
     * @param context - application context
     * @param teamName - name of the team, can be null
     * @param teamId - team ID
     * @param activity - activity being called from
     * @param db - DBHandler (to close it after use)
     * @param yearOfTeam - if team name not passed, use this to determine what the header should say
     */
    private void addTeamInfo(final LinearLayout mainLayout, LayoutInflater inflater, Cursor result,
                            final Context context, String teamName, final String teamId,
                            final Activity activity, DBHandler db, Integer yearOfTeam){
        String[] exclude = {"name", "year"};
        String[] parkExclude = {"team_id", "attendance"};

        // If we don't have a name but do have a year, find the name from the year
        if ((teamName == null || teamName.equals("") || teamName.isEmpty()) && yearOfTeam != null){
            Cursor tmp = db.teamStatsQuery(teamId, yearOfTeam);
            tmp.moveToFirst();
            CursorRow tmpRow = new CursorRow(tmp, tmp.getPosition());
            teamName = tmpRow.getValueByColumnName("name");
        }

        // Set team name header
        final String tempName = teamName;
        final TextView name = mainLayout.findViewById(R.id.teamName);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                name.setText(tempName);
            }
        });

        // Populate team stat rows
        while (result.moveToNext()){
            CursorRow cursorRow = new CursorRow(result, result.getPosition());

            final View dynamicLayout = inflater.inflate(R.layout.year_header, null);
            TextView year = dynamicLayout.findViewById(R.id.yearHeader);
            final String yearStr = cursorRow.getValueByColumnName("year");
            year.setText(yearStr); // set row header
            final String tName = cursorRow.getValueByColumnName("name");

            // add link to team roster
            Button rosterButton = dynamicLayout.findViewById(R.id.rosterButton);
            rosterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Intent startIntent = new Intent(context, TeamRoster.class);
                    startIntent.putExtra("name", tName);
                    startIntent.putExtra("team_id", teamId);
                    startIntent.putExtra("year", yearStr);
                    context.startActivity(startIntent);
                }
            });


            final LinearLayout ll = dynamicLayout.findViewById(R.id.yearHeaderVertical);

            // generate team lines
            final LinkedList<View> views = FloaterApplication.addStatsFromRow(ll, inflater, cursorRow, exclude, true, rosterButton, null);

            // generate park lines
            Cursor parks = db.parkQuery(teamId, Integer.parseInt(yearStr));
            final LinkedList<LinkedList<View>> parksList = new LinkedList<>();
            while(parks.moveToNext()){
                CursorRow parkRow = new CursorRow(parks, parks.getPosition());
                parksList.add(FloaterApplication.addStatsFromRow(ll, inflater, parkRow, parkExclude, true, null, null));
            }

            // make clicking the header expand the section
            dynamicLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view == dynamicLayout) {
                        Iterator<View> iterator = views.iterator();
                        while (iterator.hasNext()) {
                            View nextView = iterator.next();
                            nextView.setVisibility(nextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        }

                        Iterator<LinkedList<View>> parkIterator = parksList.iterator();
                        while(parkIterator.hasNext()) {
                            LinkedList<View> park = parkIterator.next();
                            iterator = park.iterator();

                            while (iterator.hasNext()) {
                                View nextView = iterator.next();
                                nextView.setVisibility(nextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                            }
                        }
                    }
                }
            });

            // add everything on the UI thread
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainLayout.addView(dynamicLayout);
                }
            });
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloaterApplication.addHomeButton(mainLayout, activity, context);
            }
        });
        result.close();
        db.close();
    }
}
