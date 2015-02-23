package com.fabb.notifica;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;


public class RoutineFragment extends Fragment {
    DaysCollectionPagerAdapter mDaysCollection;
    ViewPager mViewPager;

    static String[][] testStr = new String[7][6];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mDaysCollection = new DaysCollectionPagerAdapter(getChildFragmentManager());//

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        mViewPager.setAdapter(mDaysCollection);

        Database db = new Database(getActivity());

        for (int d = 0; d<7; ++d) {
            List<RoutineElement> rs = db.GetRoutine(RoutineElement.Day.values()[d]);
            int i = 0;
            for (RoutineElement r : rs) {
                testStr[d][i] = r.subject.name + "\n    " + r.startTime/60 + ":" + r.startTime%60;
                i++;
            }
        }

    }

    public static class DayFragment extends Fragment {
        public static final String ARG_DAY = "day";
        public static final String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.

            View rootView = inflater.inflate(R.layout.fragment_routine_list, container, false);
            Bundle args = getArguments();
            //((TextView) rootView.findViewById(R.id.section_label)).setText(
            //        days[args.getInt(ARG_DAY)]);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(super.getActivity(), android.R.layout.simple_list_item_1, testStr[args.getInt(ARG_DAY)]);
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
            return DayFragment.days[position];
        }
    }
}



