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

/**
 * Add stats to player screen
 */
public class AddPlayer extends AppCompatActivity {
    String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        FloaterApplication.setupAddStatsScreen((LinearLayout) findViewById(R.id.addPlayerLayout), this,
                FloaterApplication.getPlayerStats(), "player", getApplicationContext());
    }

}
