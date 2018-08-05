package com.company.cc.floater;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TeamRoster extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_roster);

        final String teamName = getIntent().getExtras().getString("name");
        final String teamId = getIntent().getExtras().getString("team_id");
        final String year = getIntent().getExtras().getString("year");

        TextView nameHeader = findViewById(R.id.teamName);
        nameHeader.setText(teamName);

        TextView yearHeader = findViewById(R.id.yearHeader);
        yearHeader.setText(year);

        DBHandler db = new DBHandler(this);
        Cursor result = db.teamRosterSearch(teamId, Integer.parseInt(year));

        LinearLayout mainLayout = findViewById(R.id.rosterLinearLayout);
        LayoutInflater inflater = getLayoutInflater();

        while (result.moveToNext()){
            CursorRow cursorRow = new CursorRow(result, result.getPosition());

            //create underlined player name
            SpannableString pName = new SpannableString(
                    cursorRow.getValueByColumnName("name_first") + " " + cursorRow.getValueByColumnName("name_last"));
            pName.setSpan(new UnderlineSpan(), 0, pName.length(), 0);

            //add player to screen
            View dynamicLayout = inflater.inflate(R.layout.roster_line, null);
            TextView tv = dynamicLayout.findViewById(R.id.playerName);
            tv.setText(pName);

            final String playerId = cursorRow.getValueByColumnName("player_id");

            tv.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    Intent startIntent = new Intent(getApplicationContext(), PlayerProfile.class);
                    startIntent.putExtra("playerId", playerId);
                    startActivity(startIntent);
                }
            });

            mainLayout.addView(dynamicLayout);
        }

        FloaterApplication.addHomeButton(mainLayout, this, getApplicationContext());

        result.close();
        db.close();
    }
}
