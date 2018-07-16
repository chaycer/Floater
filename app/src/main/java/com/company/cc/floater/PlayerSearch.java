package com.company.cc.floater;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PlayerSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_search);
        String player = getIntent().getExtras().getString(getString(R.string.company_name));
        // adding some testing code to make sure passing string works

        //CRR Adding search
        DBHandler db = new DBHandler(this, 0);
        Cursor result = db.playerSearchQuery(player);
        result.moveToFirst();
            String[] names = result.getColumnNames();
        String result1 = result.getString(result.getColumnIndex("player_id"));

        result = db.playerTeamsQuery("aardsda01", "CHN");
        result.moveToFirst();
        names = result.getColumnNames();
        result1 = result.getString(result.getColumnIndex("year"));

        result = db.playerStatsQuery("aardsda01",2006,"CHN",null);
        result.moveToFirst();
        names = result.getColumnNames();

        db.close(); //CRR Do this once everything with database is done (will apparently crash if you try to access cursor after calling this)
        TextView tv = (TextView) findViewById(R.id.playerNameTextView);
        tv.setText(player);
    }
}
