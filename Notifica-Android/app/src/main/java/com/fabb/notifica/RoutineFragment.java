package com.fabb.notifica;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.RedirectException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RoutineFragment extends Fragment implements UpdateListener {
    DaysCollectionPagerAdapter mDaysCollection;
    ViewPager mViewPager;

    private Activity mActivity;

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


        mActivity = getActivity();
        if (!mLoaded) {
            Database db = new Database(getActivity());
            for (int d = 0; d < 7; ++d) {
                routine.add(db.GetRoutine(RoutineElement.Day.values()[d]));
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
            int i = mViewPager.getCurrentItem();
            mDaysCollection = new DaysCollectionPagerAdapter(getChildFragmentManager());
            mViewPager.setAdapter(mDaysCollection);
            mViewPager.invalidate();
            mViewPager.setCurrentItem(i);
            Toast.makeText(mActivity, "Routine Updated", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ignore) {
        }
    }

    @Override
    public void OnUpdated(int eventCnt, int assignmentCnt, int routineCnt) {
        if (routineCnt > 0) {
            routine.clear();
            Database db = new Database(mActivity);
            for (int d = 0; d < 7; ++d) {
                routine.add(db.GetRoutine(RoutineElement.Day.values()[d]));
            }
            Refresh();
        }
    }

    public static class DayFragment extends Fragment {
        public static final String ARG_DAY = "day";
        public static final String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_routine_list, container, false);

            ListView lv = (ListView) rootView.findViewById(R.id.routine_list);
            Bundle args = getArguments();

            Calendar c = Calendar.getInstance();
            long now = c.getTimeInMillis();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long passed = (now - c.getTimeInMillis())/1000/60;

            int day = (c.get(Calendar.DAY_OF_WEEK)-1 + args.getInt(ARG_DAY))%7;
            List<RoutineElement> rtn = routine.get(day);
            ArrayList<CustomListAdapter.CustomListItem> infos = new ArrayList<>();
            boolean first = true;
            long lastTime = 0;
            for (final RoutineElement r : rtn) {
                if (first) {
                    lastTime = r.startTime;
                    first = false;
                }
                if (lastTime != r.startTime) {
                    CustomListAdapter.CustomListItem info = new CustomListAdapter.CustomListItem();
                    info.teachers = "Break";
                    infos.add(info);
                }
                String subject = "";
                String teacher = "";
                String time = "";
                if (r.subject != null) {
                    subject = r.subject.name;
                    if (r.type == 0)
                        subject += " (Lecture)";
                    else if (r.type == 1)
                        subject += " (Tutorial)";
                    else if (r.type == 2)
                        subject += " (Practical)";
                }
                if (r.teacher != null) {
                    teacher = r.teacher.name;
                }
                String sTime = String.format("%02d", r.startTime/60) + ":" + String.format("%02d", r.startTime%60);
                String eTime = String.format("%02d", r.endTime/60) + ":" + String.format("%02d", r.endTime%60);
                time = sTime + " - " + eTime;
                lastTime = r.endTime;
                CustomListAdapter.CustomListItem info = new CustomListAdapter.CustomListItem();
                info.subjects = subject;
                info.teachers = teacher;
                info.times = time;
                infos.add(info);

            }

            CustomListAdapter adapter = new CustomListAdapter(getActivity(), infos);
            lv.setAdapter(adapter);
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



