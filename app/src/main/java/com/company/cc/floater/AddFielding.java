package com.company.cc.floater;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Add fielding stats screen
 */
public class AddFielding extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fielding);

        FloaterApplication.setupAddStatsScreen((LinearLayout) findViewById(R.id.addFieldingLayout), this,
                FloaterApplication.getFieldingStats(), "fielding", getApplicationContext());
    }
}
