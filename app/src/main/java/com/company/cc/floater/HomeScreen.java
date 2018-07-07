package com.company.cc.floater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //player search
        Button playerSearchButton = (Button) findViewById(R.id.playerSearchButton);
        playerSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText playerET = (EditText) findViewById(R.id.playerSearchEditText);
                if (!TextUtils.isEmpty(playerET.getText())){
                    Intent startIntent = new Intent(getApplicationContext(), PlayerSearch.class);
                    startIntent.putExtra(getString(R.string.company_name), playerET.getText().toString());
                    startActivity(startIntent);
                }
            }
        });

        //team search
        Button teamSearchButton = (Button) findViewById(R.id.teamSearchButton);
        teamSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText teamET = (EditText) findViewById(R.id.teamSearchEditText);
                if (!TextUtils.isEmpty(teamET.getText())){
                    Intent startIntent = new Intent(getApplicationContext(), TeamSearch.class);
                    startIntent.putExtra(getString(R.string.company_name), teamET.getText().toString());
                    startActivity(startIntent);
                }
            }
        });

        //stat search
        Button statSearchButton = (Button) findViewById(R.id.statSearchButton);
        statSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), StatSearch.class);
                startActivity(startIntent);
            }
        });

        //add player
        Button addPlayerButton = (Button) findViewById(R.id.addPlayerButton);
        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), AddPlayer.class);
                startActivity(startIntent);
            }
        });
    }


}
