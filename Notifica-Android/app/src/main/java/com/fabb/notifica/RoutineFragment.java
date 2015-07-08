package com.fabb.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RoutineFragment extends Fragment implements UpdateListener {
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
            for (int d = 0; d < 7; ++d) {
                routine.add(RoutineElement.find(RoutineElement.class, "day = ?", new String[]{d+""}, null, "start_time", null));
            }
            mLoaded = true;
        }

        mViewPager.setCurrentItem(0);
        //Calendar cal = Calendar.getInstance();
        //mViewPager.setCurrentItem(cal.get(Calendar.DAY_OF_WEEK) - 1);

        UpdateService.AddUpdateListener(this);
    }

    public void Refresh() {
        try {
            if (!this.isVisible())
                return;
            int i = mViewPager.getCurrentItem();
            mDaysCollection = new DaysCollectionPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mDaysCollection);
            mViewPager.invalidate();
            mViewPager.setCurrentItem(i);
            //Toast.makeText(mActivity, "Routine Updated", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ignore) {
        }
    }

    @Override
    public void OnUpdateComplete(boolean hasUpdated, int eventCnt, int assignmentCnt) {
        if (!hasUpdated)
            return;
        routine.clear();
        for (int d = 0; d < 7; ++d) {
            routine.add(RoutineElement.find(RoutineElement.class, "day = ?", new String[]{d+""}, null, "start_time", null));
        }
        Refresh();
    }

    public static class DayFragment extends Fragment {
        public static final String ARG_DAY = "day";
        public static final String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_routine_list, container, false);
            SharedPreferences preferences = MainActivity.GetPreferences(getActivity());

            String user_type = preferences.getString("user-type","");
            final boolean isteacher = user_type != null && user_type.equals("Teacher");

            ListView lv = (ListView) rootView.findViewById(R.id.routine_list);
            Bundle args = getArguments();

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            int day = (c.get(Calendar.DAY_OF_WEEK)-1 + args.getInt(ARG_DAY))%7;

            final List<RoutineElement> rtn = routine.get(day);
            final ArrayList<RoutineAdapter.Item> infos = new ArrayList<>();
            RoutineElement lastElement = null;
            RoutineAdapter.Item lastInfo = null;

            for (final RoutineElement r : rtn) {
                if (lastElement != null) {
                    if (lastElement.startTime == r.startTime && lastElement.endTime == r.endTime && lastElement.type == r.type) {
                        if (isteacher && lastElement.year == r.year && lastElement.faculty.code.equals(r.faculty.code)) {
                            lastInfo.group = "";
                            continue;
                        }
                    }
                    if (lastElement.endTime < r.startTime) {
                        RoutineAdapter.Item info = new RoutineAdapter.Item();
                        info.isBreak = true;
                        infos.add(info);
                    }
                }

                String time;
                String sTime = String.format("%02d", r.startTime/60) + ":" + String.format("%02d", r.startTime%60);
                String eTime = String.format("%02d", r.endTime/60) + ":" + String.format("%02d", r.endTime%60);
                time = sTime + " - " + eTime;

                RoutineAdapter.Item info = new RoutineAdapter.Item();
                info.subject = r.subject;
                info.teacher = r.teacher;
                info.faculty = r.faculty;
                info.group = r.groups;
                info.batch = r.year;
                info.type = r.type;
                info.time = time;
                infos.add(info);

                lastInfo = info;
                lastElement = r;
            }
            lv.addFooterView(new View(getActivity()));
            RoutineAdapter adapter = new RoutineAdapter(getActivity(), infos);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isteacher) {
                        AttendanceActivity.info = infos.get(position);
                        if (!AttendanceActivity.info.isBreak)
                            startActivity(new Intent(getActivity(), AttendanceActivity.class));
                    }
                }
            });
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
            Calendar c = Calendar.getInstance();
            return DayFragment.days[(c.get(Calendar.DAY_OF_WEEK)-1+position)%7];
        }
    }
}



