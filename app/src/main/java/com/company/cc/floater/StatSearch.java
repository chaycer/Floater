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
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatSearch extends AppCompatActivity {
    List<TextView> operatorStrings = new ArrayList<>();
    List<TextView> statStrings = new ArrayList<>();
    List<TextView> prefixes = new ArrayList<>();
    List<EditText> ets = new ArrayList<>();
    SerialList filters = new SerialList(new ArrayList<FilterSearch>());

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_search);

        final LinearLayout statSearchLayout = findViewById(R.id.statSearchLayout);
        final Button searchButton = findViewById(R.id.searchButton);

        //setup batting, fielding, and pitching buttons
        Button battingButton = findViewById(R.id.battingButton);
        Button fieldingButton = findViewById(R.id.fieldingButton);
        Button pitchingButton = findViewById(R.id.pitchingButton);
        setupButton(battingButton, searchButton, statSearchLayout, ((FloaterApplication) getApplication()).BATTING);
        setupButton(fieldingButton, searchButton, statSearchLayout, ((FloaterApplication) getApplication()).FIELDING);
        setupButton(pitchingButton, searchButton, statSearchLayout, ((FloaterApplication) getApplication()).PITCHING);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFilters();
                if (!filters.isEmpty()){
                    Intent startIntent = new Intent(getApplicationContext(), StatSearchResults.class);

                    startIntent.putExtra("filters", filters);
                    startIntent.putExtra("count", "0");
                    startActivity(startIntent);
                }
            }
        });
    }

    void createFilters(){
        Iterator<TextView> statStringsIterator = statStrings.iterator();
        Iterator<TextView> operatorIterator = operatorStrings.iterator();
        Iterator<TextView> prefixesIterator = prefixes.iterator();
        Iterator<EditText> etsIterator = ets.iterator();
        while (statStringsIterator.hasNext() && operatorIterator.hasNext()
                && prefixesIterator.hasNext() && etsIterator.hasNext()){
            TextView stat = statStringsIterator.next();
            TextView op = operatorIterator.next();
            TextView pre = prefixesIterator.next();
            EditText et = etsIterator.next();

            if (!TextUtils.isEmpty(stat.getText()) && !TextUtils.isEmpty(op.getText()) &&
                    !TextUtils.isEmpty(pre.getText()) && !TextUtils.isEmpty(et.getText())){
                String fullStat = pre.getText().toString() + stat.getText().toString();
                FilterSearch fs = new FilterSearch(fullStat, op.getText().toString(), et.getText().toString());
                filters.add(fs);
            }
        }
    }

    /**
     *
     * @param btn - the button to set up
     * @param searchButton - the search button to set visible
     * @param statSearchLayout - the overall layout
     * @param type - 1 for hitting, 2 for fielding, 3 for pitching
     */
    void setupButton(Button btn, final Button searchButton, final LinearLayout statSearchLayout, final int type){
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (searchButton.getVisibility() != View.VISIBLE) {
                    searchButton.setVisibility(View.VISIBLE);
                }
                final View dynamicLayout = onStatButtonClick(statSearchLayout, true, view, type);

                // create new add stat button
                final Button btn = dynamicLayout.findViewById(R.id.dynamicBattingButton);
                btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        onStatButtonClick(statSearchLayout, false, dynamicLayout, type);
                    }
                });
            }
        });
    }

    /**
     *
     * @param layout - Parent layout to add new layout to
     * @param first - Is this the first time the button is pressed?
     */
    View onStatButtonClick(final LinearLayout layout, boolean first, final View prev, int type){
        LayoutInflater inflater = getLayoutInflater();

        final TextView tvPrev;
        final TextView prefixPrev;
        if (!first) { // User pressed the button to pick a new stat
            tvPrev = prev.findViewById(R.id.dynamicStatTextView);
            prefixPrev = prev.findViewById(R.id.statPrefix);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pick a stat");
            final CharSequence[] stats;
            final String prefix;
            // set stat category
            if (type == ((FloaterApplication) getApplication()).BATTING){
                stats = ((FloaterApplication) getApplication()).getBattingStats();
                prefix = "batting.";
            }
            else if (type == ((FloaterApplication) getApplication()).FIELDING){
                stats = ((FloaterApplication) getApplication()).getFieldingStats();
                prefix = "fielding.";
            }
            else {
                stats = ((FloaterApplication) getApplication()).getPitchingStats();
                prefix = "pitching.";
            }
            // show menu with stat options
            builder.setItems(stats, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    tvPrev.setText(stats[i]);
                    String actualPrefix = prefix;
                    if (stats[i].equals("era")){
                        actualPrefix = "ERA_stats.";
                    }
                    prefixPrev.setText(actualPrefix);
                    prev.findViewById(R.id.dynamicBattingButton).setVisibility(View.GONE); // hide add stat button
                    tvPrev.setVisibility(View.VISIBLE);
                }
            });
            builder.show();

        }

        final View dynamicLayout = inflater.inflate(R.layout.stat_search_layout, null);

        // create new buttons
        if (first) {
            //create new add operator button
            final Button operatorBtn = dynamicLayout.findViewById(R.id.operatorButton);
            final TextView operatorTv = dynamicLayout.findViewById(R.id.dynamicOperatorTextView);
            operatorBtn.setOnClickListener(new View.OnClickListener() {
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
            TextView nextPrefix = dynamicLayout.findViewById(R.id.statPrefix);
            operatorStrings.add(operatorTv);
            statStrings.add(tv);
            prefixes.add(nextPrefix);
            ets.add(et);
        }
        return dynamicLayout;
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeScreen.class));
    }
}
