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

public class AddTeamProfileAsync extends AsyncTask<Object, Boolean, Boolean> {

    protected Boolean doInBackground(Object... params){
        if (params.length < 8){
            return false;
        }
        addTeamInfo((LinearLayout) params[0], (LayoutInflater) params[1], (Cursor) params[2],
                (Context) params[3], (String) params[4], (String) params[5], (Activity) params[6], (DBHandler) params[7]);
        return true;
    }

    public void addTeamInfo(final LinearLayout mainLayout, LayoutInflater inflater, Cursor result,
                            final Context context, final String teamName, final String teamId,
                            Activity activity, DBHandler db){

        while (result.moveToNext()){
            CursorRow cursorRow = new CursorRow(result, result.getPosition());
            final View dynamicLayout = inflater.inflate(R.layout.year_header, null);
            TextView year = dynamicLayout.findViewById(R.id.yearHeader);
            final String yearStr = cursorRow.getValueByColumnName("year");
            year.setText(yearStr); // set row header

            Button rosterButton = dynamicLayout.findViewById(R.id.rosterButton);
            rosterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Intent startIntent = new Intent(context, TeamRoster.class);
                    startIntent.putExtra("name", teamName);
                    startIntent.putExtra("team_id", teamId);
                    startIntent.putExtra("year", yearStr);
                    context.startActivity(startIntent);
                }
            });

            String[] exclude = {"name", "year"};
            final LinearLayout ll = dynamicLayout.findViewById(R.id.yearHeaderVertical);

            // generate lines
            final LinkedList<View> views = FloaterApplication.addStatsFromRow(ll, inflater, cursorRow, exclude, true, rosterButton, null);

            dynamicLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view == dynamicLayout) {
                        Iterator<View> iterator = views.iterator();
                        while (iterator.hasNext()) {
                            View nextView = iterator.next();
                            nextView.setVisibility(nextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        }
                    }
                }
            });

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainLayout.addView(dynamicLayout);
                }
            });
        }

        result.close();
        db.close();
    }
}
