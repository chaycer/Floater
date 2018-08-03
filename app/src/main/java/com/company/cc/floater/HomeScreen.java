package com.company.cc.floater;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        searchButton(((FloaterApplication) getApplication()).PLAYER); // set up player search
        searchButton(((FloaterApplication) getApplication()).TEAM); // set up team search

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


    /**
     * Sets up the player and team searches
     * @param type 10 for player, 20 for team
     */
    void searchButton(int type){
        final Button searchButton;
        final EditText searchET;
        final Class newActivity;
        if (type == ((FloaterApplication) getApplication()).PLAYER){ // player search
            searchButton = findViewById(R.id.playerSearchButton);
            searchET = findViewById(R.id.playerSearchEditText);
            newActivity = PlayerSearch.class;
        }
        else { // team search
            searchButton = findViewById(R.id.teamSearchButton);
            searchET = findViewById(R.id.teamSearchEditText);
            newActivity = TeamSearch.class;
        }

        // set up the button to go to the appropriate screen
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!TextUtils.isEmpty(searchET.getText())){
                    String searchString = searchET.getText().toString(); // User-provided string to search on
                    if (!FloaterApplication.validString(searchString)){
                        TextView error = findViewById(R.id.errorText);
                        error.setVisibility(View.VISIBLE);
                        return;
                    }

                    Intent startIntent = new Intent(getApplicationContext(), newActivity);
                    startIntent.putExtra(getString(R.string.company_name), searchString);
                    startActivity(startIntent);
                }
            }
        });
    }

}
