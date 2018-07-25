package com.company.cc.floater;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlayerSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_search);
        String player = getIntent().getExtras().getString(getString(R.string.company_name));
        TextView tv = findViewById(R.id.playerSearchSearchingFor);
        tv.setText("Searching for \"" + player + "\"");

        //CRR Adding search
        DBHandler db = new DBHandler(getApplicationContext());
        Cursor result = db.playerSearchQuery(player);

        // CNP generating page based off result
        LinearLayout mainLayout = findViewById(R.id.playerSearchLayout);
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addPlayerLines(mainLayout, inflater, result, this);

        db.close(); //CRR Do this once everything with database is done (will apparently crash if you try to access cursor after calling this)
    }
}
