package com.company.cc.floater;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.LinkedList;

import static android.view.View.GONE;
import static com.company.cc.floater.FloaterApplication.BATTING;
import static com.company.cc.floater.FloaterApplication.FIELDING;
import static com.company.cc.floater.FloaterApplication.PITCHING;
import static com.company.cc.floater.FloaterApplication.addLabelValue;

/**
 * Asynchronous task to stats to a screen
 */
public class AddStatsAsync extends AsyncTask<Object, Boolean, Boolean> {

    private static double ip = -1;
    private static double er = -1;

    private static int MAX = 1;
    private static int MIN = 2;
    private static int AVG = 3;
    private static int TOT = 4;

    /**
     * Run the async task in the background
     * @param params - LinearLayout, LayoutInflater, String, Integer, Context, Activity
     * @return
     */
    protected Boolean doInBackground(Object... params){
        if (params.length < 6){
            return false;
        }
        addStats((LinearLayout) params[0], (LayoutInflater) params[1], (String) params[2],
                (Integer) params[3], (Context) params[4], (Activity) params[5]);
        return true;
    }

    /**
     * Adds all a players' stats of a specific type to a layout
     * @param mainLayout - Vertical layout to add stats to
     * @param inflater - inflater to generate new views
     * @param playerId - player ID
     * @param type - stat type
     * @param context - application context
     * @param activity - activity this is being generated to
     */
    private static void addStats(final LinearLayout mainLayout, final LayoutInflater inflater, final String playerId, final int type, final Context context, Activity activity){

        DBHandler db = new DBHandler(context);
        // Get teams/years the player played on
        Cursor playerTeams = db.playerTeamsQuery(playerId, null);

        // Make a list of all the team/year pairs
        LinkedList<CursorRow> rowList = new LinkedList<>();
        while (playerTeams.moveToNext()){
            rowList.add(new CursorRow(playerTeams, playerTeams.getPosition()));
        }
        playerTeams.close();

        boolean hasItems = false;

        String table = null;
        String header = null;
        if (type == BATTING){
            table = "batting";
            header = "Batting Stats";
        }
        else if (type == PITCHING){
            table = "pitching";
            header = "Pitching Stats";
        }
        else if (type == FIELDING){
            table = "fielding";
            header = "Fielding Stats";
        }

        final String pageHeader = header;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloaterApplication.addSingleTextView(inflater, mainLayout, pageHeader);
            }
        });


        // generate stats for each season
        for (CursorRow row: rowList){
            boolean item = generateSeason(mainLayout, row, inflater, db, playerId, table, type, context, activity, false);
            if (!hasItems){
                hasItems = item;
            }
        }

        if (!hasItems){ // display no results message if no values for given table
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FloaterApplication.addSingleTextView(inflater, mainLayout, null);
                }
            });
        }
        else{ // Generate career stats for table
            generateSeason(mainLayout, rowList.get(0), inflater, db, playerId, table, type, context, activity, true);
        }

        db.close();

        // set edit stats button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FloaterApplication.createAddStatsButton(mainLayout, inflater, type, context, playerId);
            }
        });
    }

    /**
     * Generate a season's worth of stats and show on screen
     * @param mainLayout - Vertical linear layout to add to
     * @param row - team/year label/value pair in CursorRow form
     * @param inflater - inflater to generate view
     * @param db - DBHandler to get values from database
     * @param playerId - player's ID
     * @param table - Table to pull from
     * @param type - batting, pitching, or fielding?
     * @param context - activity context
     * @param activity - activity screen
     * @param career - true to show career values, false for single season
     * @return true if any stats added, false otherwise
     */
    private static boolean generateSeason(final LinearLayout mainLayout, CursorRow row, final LayoutInflater inflater,
                                          DBHandler db, final String playerId, final String table, final int type,
                                          final Context context, final Activity activity, final boolean career){
        boolean hasItems = false;
        String[] exclude = {"player_id", "year", "team_id"};
        String[] fieldExclude = {"player_id", "year", "team_id"};
        String[] headerRow = {"pos"};

        // First, generate the headers
        final View dynamicLayout = inflater.inflate(R.layout.key_header, null);

        String year = row.getValueByIndex(0);
        final String teamId = row.getValueByIndex(1);

        TextView name = dynamicLayout.findViewById(R.id.keyHeaderTeam);
        TextView value = dynamicLayout.findViewById(R.id.keyHeaderYear);
        Cursor playerStats;
        if (career){
            name.setText("Career Stats");
            value.setVisibility(View.GONE);
            playerStats = db.careerStats(playerId, table);
        }
        else {
            name.setText(year);
            value.setText(teamId);
            playerStats = db.playerStatsQuery(playerId, Integer.parseInt(year), teamId, table);
        }

        LinearLayout LL = dynamicLayout.findViewById(R.id.keyHeaderVertical);
        final LinkedList<LinkedList<View>> hiddenViews = new LinkedList<>();

        // Add stat lines to view
        while (playerStats.moveToNext()){
            CursorRow statRow = new CursorRow(playerStats, playerStats.getPosition(), true);
            if (type == FIELDING) {
                hiddenViews.add(addStatsFromRow(LL, inflater, statRow, fieldExclude, true, null, headerRow));
            }
            else{
                hiddenViews.add(addStatsFromRow(LL, inflater, statRow, exclude, true, null, null));
                break; // prevent duplicate rows
            }
        }

        if (!hiddenViews.isEmpty()){
            hasItems = true;
            if (!career) {
                hiddenViews.add(0, FloaterApplication.singleButtonList(LL, inflater, teamId, context, year));
            }
            else{
                if (type == PITCHING){ // if adding career pitching stats, need to show career ERA
                    if (ip > 0 && er > -1) {
                        DecimalFormat df = new DecimalFormat("#.##");
                        String era = df.format(9.00 * (er/ip));
                        LinkedList<View> temp = new LinkedList<>();
                        temp.add(FloaterApplication.addLabelValue(inflater, R.layout.stat_line_no_edit, "ERA", era, true));
                        LL.addView(temp.get(0));
                        hiddenViews.add(temp);
                    }
                }
                // add aggregate buttons for career stats
                hiddenViews.add(setupAggregateButtons(LL, inflater, type, table, playerId, context));
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dynamicLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (v == dynamicLayout) {
                                for (LinkedList<View> viewList: hiddenViews){
                                    for (View nextView: viewList){
                                        nextView.setVisibility(nextView.getVisibility() == View.VISIBLE ? GONE : View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                    mainLayout.addView(dynamicLayout);
                }
            });
        }
        playerStats.close();
        return hasItems;
    }

    /**
     * Set up the aggregate buttons (max, min, avg, total) for career stats
     * @param LL - Vertical linear layout to add everything to
     * @param inflater - inflater to generate new button row
     * @param type - stat type
     * @param table - table name
     * @param playerId - player ID
     * @param context - application context
     * @return A list containing the view with the buttons
     */
    private static LinkedList<View> setupAggregateButtons(LinearLayout LL, LayoutInflater inflater, int type,
                                       String table, String playerId, Context context){
        View view = inflater.inflate(R.layout.aggregate_buttons, null);
        Button maxButton = view.findViewById(R.id.max);
        setupSingleButton(LL, maxButton, type, table, MAX, playerId, context);
        Button minButton = view.findViewById(R.id.min);
        setupSingleButton(LL, minButton, type, table, MIN, playerId, context);
        Button avgButton = view.findViewById(R.id.average);
        setupSingleButton(LL, avgButton, type, table, AVG, playerId, context);
        Button totButton = view.findViewById(R.id.totals);
        setupSingleButton(LL, totButton, type, table, TOT, playerId, context);
        LL.addView(view);
        LinkedList<View> list = new LinkedList<>();
        list.add(view);
        return list;
    }

    /**
     * Sets up a single aggregate button
     * @param LL - Layout containing the existing statistics
     * @param button - button being set up
     * @param type - stat type
     * @param table - table name
     * @param buttonType - type of button (max, min, avg, total)
     * @param playerId - Player ID
     * @param context - application context
     */
    private static void setupSingleButton(final LinearLayout LL, final Button button, final int type,
                                          final String table, final int buttonType, final String playerId, final Context context){
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                regenerateCareer(LL, type, table, buttonType, playerId, context);
            }
        });
    }

    /**
     * Regenerates the statistics displayed in the career section based on the button clicked by the user
     * @param LL - Layout containing the existing statistics
     * @param type - stat type
     * @param table - table name
     * @param buttonType - type of button (max, min, avg, total)
     * @param playerId - Player ID
     * @param context - application context
     */
    private static void regenerateCareer(LinearLayout LL, int type, String table, int buttonType,
                                         String playerId, Context context){
        DBHandler db = new DBHandler(context);
        Cursor result;

        // do the query
        if (buttonType == MAX){
            result = db.maxStats(playerId, table);
        }
        else if (buttonType == MIN){
            result = db.minStats(playerId, table);
        }
        else if (buttonType == AVG){
            result = db.avgStats(playerId, table);
        }
        else{
            result = db.careerStats(playerId, table);
        }

        String[] exclude = {"player_id", "year", "team_id", "pos"}; // don't show anything regarding the header

        // prep era calculations
        double tempIp = -1;
        double tempEr = -1;
        DecimalFormat df = new DecimalFormat("#.##");

        int sub = 2; // child indices displaying stats start at 2

        if (type == PITCHING){ //&& (buttonType == AVG || buttonType == TOT)){ //era not in cursor
            sub = 3;
        }
        int i = 0; // counter to get correct linear layout
        int j = 1; // fielding multiplier to find correct child field
        int startIndex; // how many children before stats get displayed?
        if (type == FIELDING){ // fielding has an extra child
            startIndex = 2;
        }
        else{
            startIndex = 1;
        }
        // repopulate the stat values with results from the query
        while (result.moveToNext()) {
            int max; // number of times to loop
            int k = 0; // counter for cursorrow
            if (type == FIELDING){
                max = (j * 12) + j - 1; // 11 fielding stats, must skip the position header
            }
            else {
                max = LL.getChildCount() - sub;
            }

            CursorRow row = new CursorRow(result, result.getPosition(), exclude);
            while (i < max) {
                LinearLayout layout = (LinearLayout) LL.getChildAt(i + startIndex);

                // get stat name from child
                TextView statTv = (TextView) layout.getChildAt(0);
                String stat = statTv.getText().toString();

                // get stat value from cursorrow
                TextView valueTv = (TextView) layout.getChildAt(1);
                String value = row.getValueByIndex(k);

                // turn value into double if needed
                if (value != null && !value.equals("")) {
                    if (buttonType == AVG) {
                        value = df.format(Double.parseDouble(value));
                    }
                    // set era stats
                    if (type == PITCHING) {
                        if (stat.equals("ip")) {
                            tempIp = Double.parseDouble(value);
                        }
                        else if (stat.equals("er")) {
                            tempEr = Double.parseDouble(value);
                        }
                    }
                }
                valueTv.setText(value);

                i++;
                k++;
            }

            j++;
            i++;

            // calculate era
            if (type == PITCHING){
                String era = "";
                if ((buttonType == AVG || buttonType == TOT) && tempEr >= 0 && tempIp > 0){
                     era = df.format(9.00 * (tempEr / tempIp));
                }
                else{
                    double eraResult;
                    if (buttonType == MIN) {
                        eraResult = db.minERA(playerId);
                    }
                    else{
                        eraResult = db.maxERA(playerId);
                    }
                    if (!era.equals("")) {
                        era = df.format(eraResult);
                    }
                }
                LinearLayout layout = (LinearLayout) LL.getChildAt(i);
                TextView valueTv = (TextView) layout.getChildAt(1);
                valueTv.setText(era);
            }


            if (type != FIELDING){
                break; // sanity check
            }
        }

        result.close();
        db.close();
    }

    /**
     * Adds stats to a vertical linear layout from a CursorRow with a label/value style on the
     * column name and value
     * @param mainLayout - Vertical layout to add the stat lines to
     * @param inflater - Inflater for the layout
     * @param row - CursorRow containing all the label/values
     * @param toExclude - Array of column names to exclude, can be null
     * @param hide - Should the generated lines be hidden by default?
     * @param button - A view to be added at the top if desired, can be null
     * @param headers - Array of column names to make into subheaders
     * @return A list of all the lines (horizontal linear layouts) added
     */
    public static LinkedList<View> addStatsFromRow(LinearLayout mainLayout, LayoutInflater inflater, CursorRow row, String[] toExclude, boolean hide, View button, String[] headers){
        LinkedList<View> views = new LinkedList<>();
        for (int i = 0; i < row.getSize(); i++){
            boolean exclude = false;
            int layout = R.layout.stat_line_no_edit;

            // exclude rows based on toExclude array
            if (toExclude != null) {
                for (int j = 0; j < toExclude.length; j++) {
                    if (row.getColumnNameByIndex(i).compareTo(toExclude[j]) == 0) {
                        exclude = true;
                        break;
                    }
                }
            }
            if (exclude){
                continue;
            }

            // make rows in headers array look like headers
            if (headers != null){
                for (int j = 0; j < headers.length; j++) {
                    if (row.getColumnNameByIndex(i).compareTo(headers[j]) == 0) {
                        layout = R.layout.stat_line_no_edit_header;
                        break;
                    }
                }
            }
            // set label and value texts
            String nameString = row.getColumnNameByIndex(i);
            String valueString = row.getValueByIndex(i);


            View dynamicLayout = addLabelValue(inflater, layout, nameString, valueString, hide);

            if (valueString != null && !valueString.equals("")){
                if (nameString.equals("ip")) {
                    ip = Double.parseDouble(valueString);
                } else if (nameString.equals("er")) {
                    er = Double.parseDouble(valueString);
                }
            }

            // add to the vertical layout
            mainLayout.addView(dynamicLayout);
            views.add(dynamicLayout);
        }

        // add the button if needed
        if (button != null && !views.isEmpty()){
            views.add(0, button);
        }

        return views;
    }
}
