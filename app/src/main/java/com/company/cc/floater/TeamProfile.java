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

        AddTeamProfileAsync add = new AddTeamProfileAsync();
        add.execute(mainLayout, inflater, result, getApplicationContext(), teamName, teamId, this, db);
    }

}
