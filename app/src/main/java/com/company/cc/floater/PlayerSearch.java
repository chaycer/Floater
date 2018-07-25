package com.company.cc.floater;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_search);
        String player = getIntent().getExtras().getString(getString(R.string.company_name));
        TextView tv = findViewById(R.id.playerSearchSearchingFor);
        tv.setText("Searching for \"" + player + "\"");

        //CRR Adding search
        Cursor result = FloaterApplication.db.playerSearchQuery(player);

        // CNP generating page based off result
        LinearLayout mainLayout = findViewById(R.id.playerSearchLayout);
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addPlayerLines(mainLayout, inflater, result, this);
    }
}
