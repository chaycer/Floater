package com.company.cc.floater;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AddPitching extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pitching);

        LinearLayout mainLayout = findViewById(R.id.addPitchingLayout);
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addStatLines(mainLayout, inflater, FloaterApplication.getPitchingStatsNoEra());

        String playerId = getIntent().getExtras().getString("playerId");
        LinearLayout ll = (LinearLayout) mainLayout.getChildAt(1);
        EditText idEdit = (EditText) ll.getChildAt(1);
        idEdit.setText(playerId);

        // add buttons
        View save = inflater.inflate(R.layout.save_changes_button, null);
        View add = inflater.inflate(R.layout.add_season_stats, null);

        Button saveButton = save.findViewById(R.id.saveChangesButton);
        FloaterApplication.setAddOnClick(mainLayout, saveButton, "player", "pitching", getApplicationContext(), inflater);

        Button hittingButton = add.findViewById(R.id.hittingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, hittingButton, "batting", "pitching", getApplicationContext(), inflater);

        Button fieldingButton = add.findViewById(R.id.fieldingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, fieldingButton, "fielding", "pitching", getApplicationContext(), inflater);

        Button pitchingButton = add.findViewById(R.id.pitchingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, pitchingButton, "pitching", "pitching", getApplicationContext(), inflater);

        mainLayout.addView(save);
        mainLayout.addView(add);
    }
}
