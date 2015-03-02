package com.fabb.notifica;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
//import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RoutineFragment extends Fragment {
    DaysCollectionPagerAdapter mDaysCollection;
    ViewPager mViewPager;

    private static boolean mLoaded = false;
    private static List<List<RoutineElement>> routine = new ArrayList<>();

    @Override
    public void onCreate(Bundle save)
    {
        super.onCreate(save);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create the adapter that will return a fragment
        mDaysCollection = new DaysCollectionPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) getActivity().findViewById(R.id.routine_fragment);
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mDaysCollection);

        //PagerTitleStrip strip = (PagerTitleStrip) getActivity().findViewById(R.id.pager_tab_strip);
        PagerTabStrip strip =(PagerTabStrip) getActivity().findViewById(R.id.pager_tab_strip);
        strip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        strip.setDrawFullUnderline(true);


        if (!mLoaded) {
            Database db = new Database(getActivity());
            for (int d = 0; d < 7; ++d) {
                routine.add(db.GetRoutine(RoutineElement.Day.values()[d]));
            }
            mLoaded = true;
        }

        Calendar cal = Calendar.getInstance();
        mViewPager.setCurrentItem(cal.get(Calendar.DAY_OF_WEEK) - 1);
    }


    public static class DayFragment extends Fragment {
        public static final String ARG_DAY = "day";
        public static final String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_routine_list, container, false);
            Bundle args = getArguments();
            TableLayout table = (TableLayout)rootView;

            Calendar c = Calendar.getInstance();
            long now = c.getTimeInMillis();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long passed = (now - c.getTimeInMillis())/1000/60;

            int day = args.getInt(ARG_DAY);

            for (final RoutineElement r : routine.get(day)) {
                TableRow tr =  new TableRow(getActivity());
                TextView c1 = new TextView(getActivity());
                TableRow tr1 =  new TableRow(getActivity());
                TextView c2 = new TextView(getActivity());

                /*if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    c2.setBackgroundDrawable(getResources().getDrawable(R.drawable.focus));
                } else {
                    c2.setBackground(getResources().getDrawable(R.drawable.focus));
                }
                c2.setClickable(true);*/
                c2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Database db = new Database(getActivity());
                        Toast.makeText(getActivity(),
                                r.subject.name + "\nTeacher: " + r.teacher.name+"\nFaculty: "+db.GetFaculty(r.subject).name,
                                Toast.LENGTH_LONG).show();
                    }
                });

                c1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                c2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                c2.setTypeface(null, Typeface.BOLD);
                String sTime = r.startTime / 60 + ":" + r.startTime % 60;
                String eTime = r.endTime / 60 + ":" + r.endTime % 60;

                // Highlight current period
                if (r.startTime <= passed && r.endTime >= passed && day+1 == c.get(Calendar.DAY_OF_WEEK)) {
                    tr.setBackgroundColor(Color.rgb(128, 128, 128));
                    tr1.setBackgroundColor(Color.rgb(128, 128, 128));
                    c1.setTextColor(Color.WHITE);
                    c2.setTextColor(Color.WHITE);
                }
                else {
                    c1.setTextColor(Color.DKGRAY);
                    c2.setTextColor(Color.DKGRAY);
                }

                c1.setText(sTime + " - " + eTime);
                c1.setPadding(6, 2, 2, 2);
                c2.setText(r.subject.name);
                c2.setPadding(6, 2, 2, 10);
                tr.addView(c1);
                tr1.addView(c2);
                table.addView(tr);
                table.addView(tr1);
            }

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



