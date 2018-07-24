package com.company.cc.floater;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatSearch extends AppCompatActivity {
    List<TextView> operatorStrings = new ArrayList<>();
    List<TextView> statStrings = new ArrayList<>();
    List<EditText> ets = new ArrayList<>();
    List<FilterSearch> filters = new ArrayList<>();
    int searchType = 1; // 1 for batting, 2 for fielding, 3 for pitching

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_search);

        final LinearLayout statSearchLayout = (LinearLayout) findViewById(R.id.statSearchLayout);
        final LinearLayout buttons = (LinearLayout) findViewById(R.id.statButtonLayout);
        final LinearLayout dynamicBattingSearch = (LinearLayout) findViewById(R.id.dynamicBattingSearch);
        final Button searchButton = (Button) findViewById(R.id.searchButton);

        //setup batting, fielding, and pitching buttons
        Button battingButton = (Button) findViewById(R.id.battingButton);
        Button fieldingButton = (Button) findViewById(R.id.fieldingButton);
        Button pitchingButton = (Button) findViewById(R.id.pitchingButton);
        setupButton(battingButton, buttons, searchButton, statSearchLayout, ((FloaterApplication) getApplication()).BATTING);
        setupButton(fieldingButton, buttons, searchButton, statSearchLayout, ((FloaterApplication) getApplication()).FIELDING);
        setupButton(pitchingButton, buttons, searchButton, statSearchLayout, ((FloaterApplication) getApplication()).PITCHING);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFilters();
                if (!filters.isEmpty()){
                    Intent startIntent = new Intent(getApplicationContext(), StatSearchResults.class);

                    //Begin Search
                    DBHandler db = new DBHandler(getApplicationContext());
                    Cursor result = db.statSearchQuery(filters);
                    //End Search

                    //startIntent.putExtra(getString(R.string.company_name), playerET.getText().toString());
                    startActivity(startIntent);
                }
            }
        });
    }

    void createFilters(){
        Iterator<TextView> statStringsIterator = statStrings.iterator();
        Iterator<TextView> operatorIterator = operatorStrings.iterator();
        Iterator<EditText> etsIterator = ets.iterator();
        while (statStringsIterator.hasNext() && operatorIterator.hasNext() && etsIterator.hasNext()){
            TextView stat = statStringsIterator.next();
            TextView op = operatorIterator.next();
            EditText et = etsIterator.next();

            if (!TextUtils.isEmpty(stat.getText()) && !TextUtils.isEmpty(op.getText()) && !TextUtils.isEmpty(et.getText())){
                FilterSearch fs = new FilterSearch(stat.getText().toString(), op.getText().toString(), et.getText().toString(), searchType);
                filters.add(fs);
            }
        }
    }

    /**
     *
     * @param btn - the button to set up
     * @param buttons - the linear layout containing the buttons
     * @param searchButton - the search button to set visible
     * @param statSearchLayout - the overall layout
     * @param type - 1 for hitting, 2 for fielding, 3 for pitching
     */
    void setupButton(Button btn, final LinearLayout buttons, final Button searchButton, final LinearLayout statSearchLayout, final int type){
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                searchType = type;
                buttons.setVisibility(View.GONE);
                searchButton.setVisibility(View.VISIBLE);
                onStatButtonClick(statSearchLayout, true, view);
            }
        });
    }

    /**
     *
     * @param layout - Parent layout to add new layout to
     * @param first - Is this the first time the button is pressed?
     */
    void onStatButtonClick(final LinearLayout layout, boolean first, final View prev){
        LayoutInflater inflater = getLayoutInflater();
        final View dynamicLayout = inflater.inflate(R.layout.stat_search_layout, null);

        final TextView tvPrev;

        if (!first) { // User pressed the button to pick a new stat
            tvPrev = prev.findViewById(R.id.dynamicStatTextView);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick a stat");
            final CharSequence[] stats;

            // set stat category
            if (searchType == ((FloaterApplication) getApplication()).BATTING){
                stats = ((FloaterApplication) getApplication()).getBattingStats();
            }
            else if (searchType == ((FloaterApplication) getApplication()).FIELDING){
                stats = ((FloaterApplication) getApplication()).getFieldingStats();
            }
            else {
                stats = ((FloaterApplication) getApplication()).getPitchingStats();
            }
            // show menu with stat options
            builder.setItems(stats, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    tvPrev.setText(stats[i]);
                    prev.findViewById(R.id.dynamicBattingButton).setVisibility(View.GONE); // hide add stat button
                    tvPrev.setVisibility(View.VISIBLE);
                }
            });
            builder.show();

        }

        // create new add stat button
        final Button btn = dynamicLayout.findViewById(R.id.dynamicBattingButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onStatButtonClick(layout, false, dynamicLayout);
            }
        });

        //create new add operator button
        final Button operatorBtn = dynamicLayout.findViewById(R.id.operatorButton);
        final TextView operatorTv = dynamicLayout.findViewById(R.id.dynamicOperatorTextView);
        operatorBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StatSearch.this);
                builder.setTitle("Pick a stat");
                final CharSequence[] operators = ((FloaterApplication) getApplication()).getOperators();
                //show menu with operator options
                builder.setItems(operators, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        operatorTv.setText(operators[i]);
                        operatorBtn.setVisibility(View.INVISIBLE);
                        operatorTv.setVisibility(View.VISIBLE);
                    }
                });
                builder.show();

            }
        });
        layout.addView(dynamicLayout);

        // add new edittext and textview to lists
        TextView tv = dynamicLayout.findViewById(R.id.dynamicStatTextView);
        EditText et = dynamicLayout.findViewById(R.id.dynamicStatEditText);
        operatorStrings.add(operatorTv);
        statStrings.add(tv);
        ets.add(et);
    }
}
