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
                SerialList filters = createFilters(statSearchLayout);
                if (!filters.isEmpty()){
                    Intent startIntent = new Intent(getApplicationContext(), StatSearchResults.class);

                    startIntent.putExtra("filters", filters);
                    startIntent.putExtra("count", "0");
                    startActivity(startIntent);
                }
            }
        });
    }

    /**
     * Creates the list of filters based on all the information currently on screen
     * @param statSearchLayout - Layout to find the information
     * @return - A list of FilterSearch objects to run a query over
     */
    private SerialList createFilters(LinearLayout statSearchLayout){
        SerialList filters = new SerialList(new ArrayList<FilterSearch>());
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
                if (!FloaterApplication.validString(et.getText().toString())){
                    TextView tv = statSearchLayout.findViewById(R.id.errorText);
                    tv.setVisibility(View.VISIBLE);
                    return new SerialList(new ArrayList<FilterSearch>());
                }

                String fullStat = pre.getText().toString() + stat.getText().toString();
                FilterSearch fs = new FilterSearch(fullStat, op.getText().toString(), et.getText().toString());
                filters.add(fs);
            }
        }
        return filters;
    }

    /**
     * Set up a stat search button
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
                final View dynamicLayout = firstButtonClick(statSearchLayout);

                // create new add stat button
                final Button btn = dynamicLayout.findViewById(R.id.dynamicBattingButton);
                btn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        onStatButtonClick(dynamicLayout, type);
                    }
                });
            }
        });
    }

    /**
     * Adds an add stat row to a layout
     * @param layout - layout to display new fields
     * @return - new view, added to the layout
     */
    View firstButtonClick(final LinearLayout layout){

        LayoutInflater inflater = getLayoutInflater();

        final View dynamicLayout = inflater.inflate(R.layout.stat_search_layout, layout, false);

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

        // add new edittext and textview to lists
        TextView tv = dynamicLayout.findViewById(R.id.dynamicStatTextView);
        EditText et = dynamicLayout.findViewById(R.id.dynamicStatEditText);
        TextView nextPrefix = dynamicLayout.findViewById(R.id.statPrefix);
        operatorStrings.add(operatorTv);
        statStrings.add(tv);
        prefixes.add(nextPrefix);
        ets.add(et);
        layout.addView(dynamicLayout);

        return dynamicLayout;
    }

    /**
     * Set the on click listener for an add stat button
     * @param prev - previous screen's layout
     * @param type - 1 for hitting, 2 for fielding, 3 for pitching
     */
    void onStatButtonClick(final View prev, int type){
        final TextView tvPrev;
        final TextView prefixPrev;

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

    /**
     * Force back button to take you back to the home screen
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeScreen.class));
    }
}
