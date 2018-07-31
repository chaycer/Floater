package com.company.cc.floater;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;

public class TeamProfile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_profile);

        final String teamName = getIntent().getExtras().getString("name");
        final String teamId = getIntent().getExtras().getString("team_id");

        TextView name = findViewById(R.id.teamName);
        name.setText(teamName);

        DBHandler db = new DBHandler(this);
        Cursor result = db.teamStatsQuery(teamId, null);

        LinearLayout mainLayout = findViewById(R.id.teamProfileLinearLayout);
        LayoutInflater inflater = getLayoutInflater();

        // TODO: move to async
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
                    Intent startIntent = new Intent(getApplicationContext(), TeamRoster.class);
                    startIntent.putExtra("name", teamName);
                    startIntent.putExtra("team_id", teamId);
                    startIntent.putExtra("year", yearStr);
                    startActivity(startIntent);
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

            mainLayout.addView(dynamicLayout);
        }

        result.close();
        db.close();
    }

}
