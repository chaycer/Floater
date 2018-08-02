package com.company.cc.floater;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Add pitching stats screen
 */
public class AddPitching extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pitching);

        FloaterApplication.setupAddStatsScreen((LinearLayout) findViewById(R.id.addPitchingLayout), this,
                FloaterApplication.getPitchingStatsNoEra(), "pitching", getApplicationContext());
    }
}
