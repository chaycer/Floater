package com.company.cc.floater;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AddHitting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hitting);

        LinearLayout mainLayout = findViewById(R.id.addHittingLayout);
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addStatLines(mainLayout, inflater, FloaterApplication.getBattingStats());

        CursorRow playerRow = (CursorRow) getIntent().getExtras().getSerializable("CursorRow");
        LinearLayout ll = (LinearLayout) mainLayout.getChildAt(0);
        EditText idEdit = (EditText) ll.getChildAt(1);
        idEdit.setText(playerRow.getValueByColumnName("player_id"));

        // add buttons
        View save = inflater.inflate(R.layout.save_changes_button, null);
        View add = inflater.inflate(R.layout.add_season_stats, null);

        Button saveButton = save.findViewById(R.id.saveChangesButton);
        FloaterApplication.setAddOnClick(mainLayout, saveButton, "player", "batting", getApplicationContext());

        Button hittingButton = add.findViewById(R.id.hittingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, hittingButton, "batting", "batting", getApplicationContext());

        Button fieldingButton = add.findViewById(R.id.fieldingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, fieldingButton, "fielding", "batting", getApplicationContext());

        Button pitchingButton = add.findViewById(R.id.pitchingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, pitchingButton, "pitching", "batting", getApplicationContext());
        mainLayout.addView(save);
        mainLayout.addView(add);
    }
}
