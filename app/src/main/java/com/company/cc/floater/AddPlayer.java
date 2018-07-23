package com.company.cc.floater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class AddPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        LinearLayout mainLayout = findViewById(R.id.addPlayerLayout);
        // generate layout showing each player stat input
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addStatLines(mainLayout, inflater, FloaterApplication.getPlayerStats());

        // add buttons
        View save = inflater.inflate(R.layout.save_changes_button, null);
        View add = inflater.inflate(R.layout.add_season_stats, null);

        /*
        TODO: NEED TO SAVE STATS AS PART OF ONCLICK LISTENER
         */
        Button saveButton = save.findViewById(R.id.saveChangesButton);
        Button hittingButton = add.findViewById(R.id.hittingAddButton);
        Button fieldingButton = add.findViewById(R.id.fieldingAddButton);
        Button pitchingButton = add.findViewById(R.id.pitchingAddButton);

        FloaterApplication.setSaveButton(saveButton, this);
        FloaterApplication.setHittingButton(hittingButton, this);
        FloaterApplication.setFieldingButton(fieldingButton, this);
        FloaterApplication.setPitchingButton(pitchingButton, this);

        mainLayout.addView(save);
        mainLayout.addView(add);

/*
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), PlayerProfile.class);
                startActivity(startIntent);
            }
        });



        hittingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), AddHitting.class);
                startActivity(startIntent);
            }
        });


        fieldingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), AddFielding.class);
                startActivity(startIntent);
            }
        });


        pitchingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), AddPitching.class);
                startActivity(startIntent);
            }
        });*/


    }

}
