package com.company.cc.floater;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Add hitting stats screen
 */
public class AddHitting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hitting);

        FloaterApplication.setupAddStatsScreen((LinearLayout) findViewById(R.id.addHittingLayout), this,
                FloaterApplication.getBattingStats(), "batting", getApplicationContext());
    }
}
