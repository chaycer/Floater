package com.company.cc.floater;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class AddFielding extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fielding);

        LinearLayout mainLayout = findViewById(R.id.addFieldingLayout);
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addStatLines(mainLayout, inflater, FloaterApplication.getFieldingStats());

        // add buttons
        View save = inflater.inflate(R.layout.save_changes_button, null);
        View add = inflater.inflate(R.layout.add_season_stats, null);

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
    }
}
