package com.company.cc.floater;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerProfile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        //Bundle extras = getIntent().getExtras();
        CursorRow CR = (CursorRow) getIntent().getExtras().getSerializable("CursorRow");
        if (CR == null) {
            String playerId = getIntent().getExtras().getString("playerId");
            /*
            TODO: add a db call to pull player info based on ID and then add to a CursorRow
             */
        }

        TextView pname = findViewById(R.id.playerName);
        pname.setText(CR.getValueByColumnName("name_first") + " " + CR.getValueByColumnName("name_last"));

        /*
        final TextView mSampleTitle = (TextView) findViewById(R.id.title);
        final LinearLayout mSampleContent = (LinearLayout) findViewById(R.id.year);
        mSampleTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == mSampleTitle) {
                    mSampleContent.setVisibility(mSampleContent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            }
        });*/
    }
    /*
        //CardView hittingCard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        //display stuff on click
        final TextView mSampleTitle = (TextView) findViewById(R.id.title);
        final LinearLayout mSampleContent = (LinearLayout) findViewById(R.id.year);
        mSampleTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == mSampleTitle) {
                    mSampleContent.setVisibility(mSampleContent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            }
            });
        /*
        hittingCard = (CardView) findViewById(R.id.hittingCardView);
        //hide until title is clicked
        hittingCard.setVisibility(View.GONE);
        final TextView descriptionText = (TextView) findViewById(R.id.detail_description_content);
        final TextView showAll = (TextView) findViewById(R.id.detail_read_all);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAll.setVisibility(View.INVISIBLE);
                descriptionText.setMaxLines(Integer.MAX_VALUE);
            }
        });
}
    public void toggleContents(View v){
        hittingCard.setVisibility(hittingCard.isShown() ? View.GONE : View.VISIBLE );
    }
*/
}
