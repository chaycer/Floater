package com.company.cc.floater;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AddPlayer extends AppCompatActivity {
    String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        final LinearLayout mainLayout = findViewById(R.id.addPlayerLayout);

        // generate layout showing each player stat input
        LayoutInflater inflater = getLayoutInflater();
        FloaterApplication.addStatLines(mainLayout, inflater, FloaterApplication.getPlayerStats());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String playerId = bundle.getString("playerId");
            if (playerId != null) {
                LinearLayout ll = (LinearLayout) mainLayout.getChildAt(2);
                EditText idEdit = (EditText) ll.getChildAt(1);
                idEdit.setText(playerId);
            }
        }

        // add buttons
        View save = inflater.inflate(R.layout.save_changes_button, null);
        View add = inflater.inflate(R.layout.add_season_stats, null);

        Button saveButton = save.findViewById(R.id.saveChangesButton);
        FloaterApplication.setAddOnClick(mainLayout, saveButton, "player", "player", getApplicationContext(), inflater);

        Button hittingButton = add.findViewById(R.id.hittingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, hittingButton, "batting", "player", getApplicationContext(), inflater);

        Button fieldingButton = add.findViewById(R.id.fieldingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, fieldingButton, "fielding", "player", getApplicationContext(), inflater);

        Button pitchingButton = add.findViewById(R.id.pitchingAddButton);
        FloaterApplication.setAddOnClick(mainLayout, pitchingButton, "pitching", "player", getApplicationContext(), inflater);

        mainLayout.addView(save);
        mainLayout.addView(add);
    }

    public boolean addStatsToPlayer(LinearLayout mainLayout, String type){
        boolean addNulls = false;
        List<InsertStat> ret = new LinkedList<>();

        int count = mainLayout.getChildCount();
        for (int i = 0; i < count; i++){
            View childLayout = mainLayout.getChildAt(i);
            if (childLayout instanceof LinearLayout){
                String stat = "";
                String val = "";
                LinearLayout ll = (LinearLayout) childLayout;

                int childCount = ll.getChildCount();
                for (int j = 0; j < childCount; j++){
                    View text = ll.getChildAt(j);
                    if (text instanceof TextView){
                        TextView tv = (TextView) text;
                        stat = tv.getText().toString();
                    }
                    else if (text instanceof EditText){
                        EditText et = (EditText) text;
                        val = et.getText().toString();
                    }
                    else if (text instanceof CheckBox){
                        CheckBox cb = (CheckBox) text;
                        addNulls = cb.isChecked();
                    }
                }
                ret.add(new InsertStat(type, stat, val));
            }
        }

        Iterator<InsertStat> iterator = ret.iterator();
        while (iterator.hasNext()){
            InsertStat is = iterator.next();

            //return if we don't have the requisite values for the keys
            if (type.compareTo("player") != 0){
                if (is.getColumn().compareTo("player_id") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return false;
                    }
                }
                else if (is.getColumn().compareTo("year") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return false;
                    }
                }
                else if (is.getColumn().compareTo("team_id") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return false;
                    }
                }
                else if (type.compareTo("fielding") == 0 && is.getColumn().compareTo("pos") == 0){
                    if (is.getValue().compareTo("") == 0){
                        return false;
                    }
                }
            }

            //remove null values from list if we aren't inserting them
            if (!addNulls){
                if (is.getValue().compareTo("") == 0){
                    iterator.remove();
                }
            }
        }

        if (type.compareTo("player") == 0){
        }
        else if (type.compareTo("batting") == 0){

        }
        else if (type.compareTo("fielding") == 0){

        }
        else if (type.compareTo("pitching") == 0){

        }

        return true;
    }

}
