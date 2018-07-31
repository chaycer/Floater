package com.company.cc.floater;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlayerProfile extends FragmentActivity {
    private ViewPager viewPager;
    private static final int NUM_PAGES = 4;
    private PagerAdapter pagerAdapter;

    private CursorRow playerRow;

    private String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        viewPager = findViewById(R.id.playerProfileViewPager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(5);

        playerRow = (CursorRow) getIntent().getExtras().getSerializable("CursorRow");
        if (playerRow == null) {
            playerId = getIntent().getExtras().getString("playerId");
            playerRow = FloaterApplication.playerTableRow(playerId, getApplicationContext());
        }
        else{
            playerId = playerRow.getValueByColumnName("player_id");
        }
    }

    @Override
    public void onBackPressed(){
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }

    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0: return ProfileFragment.newInstance(playerRow);
                case 1: return HittingFragment.newInstance(playerRow);
                case 2: return PitchingFragment.newInstance(playerRow);
                case 3: return FieldingFragment.newInstance(playerRow);
                default: return ProfileFragment.newInstance(playerRow);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }

    public static class ProfileFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_player_profile, container, false);

            CursorRow row = (CursorRow) getArguments().getSerializable("CursorRow");

            TextView pName = v.findViewById(R.id.playerName);
            String[] fullNameColumns = {"name_first", "name_last"};
            pName.setText(row.getValueByColumnName(fullNameColumns[0]) + " " + row.getValueByColumnName(fullNameColumns[1]));

            LinearLayout LL = v.findViewById(R.id.playerProfileLinearLayout);
            LayoutInflater inflater2 = getLayoutInflater();
            FloaterApplication.addStatsFromRow(LL, inflater2, row, fullNameColumns, false, null, null);
            return v;
        }

        public static ProfileFragment newInstance(CursorRow row){
            ProfileFragment fragment = new ProfileFragment();
            Bundle b = new Bundle();
            b.putSerializable("CursorRow", row);
            fragment.setArguments(b);

            return fragment;
        }
    }

    public static class HittingFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_stat_profile, container, false);

            CursorRow row = (CursorRow) getArguments().getSerializable("CursorRow");

            TextView pName = v.findViewById(R.id.playerName);
            String[] fullNameColumns = {"name_first", "name_last"};
            pName.setText(row.getValueByColumnName(fullNameColumns[0]) + " " + row.getValueByColumnName(fullNameColumns[1]));

            LinearLayout sv = v.findViewById(R.id.statProfileVerticalLayout);
            LayoutInflater inflater2 = getLayoutInflater();
            AddStatsAsync add = new AddStatsAsync();
            add.execute(sv, inflater2, row.getValueByColumnName("player_id"), FloaterApplication.BATTING, getActivity(), getActivity());
            return v;
        }

        public static HittingFragment newInstance(CursorRow row){
            HittingFragment fragment = new HittingFragment();
            Bundle b = new Bundle();
            b.putSerializable("CursorRow", row);
            fragment.setArguments(b);

            return fragment;
        }
    }

    public static class PitchingFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_stat_profile, container, false);

            CursorRow row = (CursorRow) getArguments().getSerializable("CursorRow");

            TextView pName = v.findViewById(R.id.playerName);
            String[] fullNameColumns = {"name_first", "name_last"};
            pName.setText(row.getValueByColumnName(fullNameColumns[0]) + " " + row.getValueByColumnName(fullNameColumns[1]));

            LinearLayout sv = v.findViewById(R.id.statProfileVerticalLayout);
            LayoutInflater inflater2 = getLayoutInflater();
            AddStatsAsync add = new AddStatsAsync();
            add.execute(sv, inflater2, row.getValueByColumnName("player_id"), FloaterApplication.PITCHING, getActivity(), getActivity());
            return v;
        }

        public static PitchingFragment  newInstance(CursorRow row){
            PitchingFragment  fragment = new PitchingFragment ();
            Bundle b = new Bundle();
            b.putSerializable("CursorRow", row);
            fragment.setArguments(b);

            return fragment;
        }
    }

    public static class FieldingFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_stat_profile, container, false);

            CursorRow row = (CursorRow) getArguments().getSerializable("CursorRow");

            TextView pName = v.findViewById(R.id.playerName);
            String[] fullNameColumns = {"name_first", "name_last"};
            pName.setText(row.getValueByColumnName(fullNameColumns[0]) + " " + row.getValueByColumnName(fullNameColumns[1]));

            LinearLayout sv = v.findViewById(R.id.statProfileVerticalLayout);
            LayoutInflater inflater2 = getLayoutInflater();

            AddStatsAsync add = new AddStatsAsync();
            add.execute(sv, inflater2, row.getValueByColumnName("player_id"), FloaterApplication.FIELDING, getActivity(), getActivity());
            return v;
        }

        public static FieldingFragment newInstance(CursorRow row){
            FieldingFragment fragment = new FieldingFragment();
            Bundle b = new Bundle();
            b.putSerializable("CursorRow", row);
            fragment.setArguments(b);

            return fragment;
        }
    }
}
