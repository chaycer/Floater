package com.company.cc.floater;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Filter;
import android.widget.LinearLayout;

import java.util.List;

public class StatSearchResults extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_search_results);

        LinearLayout mainLayout = findViewById(R.id.statSearchLayout);
        LayoutInflater inflater = getLayoutInflater();

        SerialList filters = (SerialList) getIntent().getExtras().getSerializable("filters");
        int count = Integer.parseInt(getIntent().getStringExtra("count"));

        AddStatSearchAsync add = new AddStatSearchAsync();
        add.execute(mainLayout, inflater, filters, getApplicationContext(), count, this);

        //FloaterApplication.addStatSearchLines(mainLayout, inflater, filters, getApplicationContext(), count);

    }

}
