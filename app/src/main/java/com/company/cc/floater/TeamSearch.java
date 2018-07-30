package com.company.cc.floater;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TeamSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_search);

        String team = getIntent().getExtras().getString(getString(R.string.company_name));
        TextView tv = findViewById(R.id.teamSearchSearchingFor);
        tv.setText("Searching for \"" + team + "\"");

        DBHandler db = new DBHandler(this);
        Cursor result = db.teamSearch(team);

        // CNP generating page based off result
        LinearLayout mainLayout = findViewById(R.id.teamSearchLayout);
        LayoutInflater inflater = getLayoutInflater();
        addTeamLines(mainLayout, inflater, result);
        result.close();
        db.close();
    }

    public void addTeamLines(LinearLayout mainLayout, LayoutInflater inflater, final Cursor result){

        // TODO: show "no results" if cursor empty
        if (result == null || (result.getCount() < 1)){

            result.close();
            return;
        }

        while (result.moveToNext()){
            View dynamicLayout = inflater.inflate(R.layout.player_search_layout, null);
            TextView name = dynamicLayout.findViewById(R.id.playerNameTextView);
            TextView id = dynamicLayout.findViewById(R.id.yearsActiveTextView);
            final String teamId = result.getString(result.getColumnIndex("team_id"));

            if(result.getString(result.getColumnIndex("name")) == null ||
                    teamId == null){
                continue;
            }

            name.setText(result.getString(result.getColumnIndex("name")));
            id.setText(teamId);

            LinearLayout LL = dynamicLayout.findViewById(R.id.playerSearchHorizontalLayout);
            LL.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent startIntent = new Intent(getApplicationContext(), TeamProfile.class);
                    startIntent.putExtra("ID", teamId);
                    startActivity(startIntent);
                }
            });

            mainLayout.addView(dynamicLayout);
        }
    }
}
