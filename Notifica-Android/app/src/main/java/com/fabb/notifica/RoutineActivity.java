package com.fabb.notifica;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class RoutineActivity extends ActionBarActivity {
    DaysCollectionPagerAdapter mDaysCollection;
    ViewPager mViewPager;

    static String[][] testStr = new String[7][6];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);


        UpdateService.AddNewData(this);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mDaysCollection = new DaysCollectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDaysCollection);

        Database db = new Database(this);

        for (int d = 0; d<7; ++d) {
            List<RoutineElement> rs = db.GetRoutine(RoutineElement.Day.values()[d]);
            int i = 0;
            for (RoutineElement r : rs) {
                testStr[d][i] = r.subject.name + "\n    " + (int)r.startTime/60 + ":" + (int)r.startTime%60;
                i++;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_routine) {
            startActivity(new Intent(this, RoutineActivity.class));
            return true;
        }
        if (id == R.id.action_assignments) {
            startActivity(new Intent(this, AssignmentActivity.class));
            return true;
        }
        if (id == R.id.action_events) {
            startActivity(new Intent(this, EventActivity.class));
            return true;
        }
        if (id == R.id.action_study_info) {
            //startActivity(new Intent(this, StudyInfoActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static class DayFragment extends Fragment {
        public static final String ARG_DAY = "day";
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

            View rootView = inflater.inflate(
                    R.layout.fragment_routine, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(R.id.section_label)).setText(
                    days[args.getInt(ARG_DAY)]);
            ;
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(super.getActivity(), android.R.layout.simple_list_item_1, testStr[args.getInt(ARG_DAY)]);
            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(adapter);
            return rootView;
        }

    }

    class DaysCollectionPagerAdapter extends  FragmentPagerAdapter{

        public DaysCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DayFragment();
            Bundle args = new Bundle();
            args.putInt(DayFragment.ARG_DAY, i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Day " + (position + 1);
        }
    }
}



