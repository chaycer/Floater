package com.company.cc.floater;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

import static com.company.cc.floater.FloaterApplication.BATTING;
import static com.company.cc.floater.FloaterApplication.PITCHING;

public class PlayerSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_search);
        String player = getIntent().getExtras().getString(getString(R.string.company_name));
        TextView tv = findViewById(R.id.playerSearchSearchingFor);
        tv.setText("Searching for \"" + player + "\"");

        //CRR Adding search
        DBHandler db = new DBHandler(this);
        Cursor result = db.playerSearchQuery(player);

        // CNP generating page based off result
        LinearLayout mainLayout = findViewById(R.id.playerSearchLayout);
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addPlayerLines(mainLayout, inflater, result, this);

        View dynamicLayout = inflater.inflate(R.layout.single_button, null);
        Button button = dynamicLayout.findViewById(R.id.blankButton);
        button.setText("Add New Player To Database");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), AddPlayer.class);
                startActivity(startIntent);
            }
        });
        mainLayout.addView(dynamicLayout);

        result.close();
        db.close();
    }
}
