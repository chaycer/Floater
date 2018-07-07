package ccenterprises.floater;

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
        final TextView mSampleTitle = (TextView) findViewById(R.id.title);
        final LinearLayout mSampleContent = (LinearLayout) findViewById(R.id.year);
        mSampleTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == mSampleTitle) {
                    mSampleContent.setVisibility(mSampleContent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
            }
        });
    }
}
