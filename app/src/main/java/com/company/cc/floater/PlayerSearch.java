package com.company.cc.floater;

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
        TextView tv = (TextView) findViewById(R.id.playerNameTextView);
        tv.setText(player);
    }
}
