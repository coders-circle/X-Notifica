package com.fabb.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                DayFragment page = (DayFragment)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
                ((MainActivity)getActivity()).swipeRefreshLayout.setListView(page.mListView);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                ((MainActivity)getActivity()).swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });


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


        DayFragment page = (DayFragment)mViewPager.getAdapter().instantiateItem(mViewPager, mViewPager.getCurrentItem());
        ((MainActivity)getActivity()).swipeRefreshLayout.setListView(page.mListView);
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

        public ListView mListView;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_routine_list, container, false);

            mListView = (ListView) rootView.findViewById(R.id.routine_list);
            registerForContextMenu(mListView);

            final SharedPreferences preferences = MainActivity.GetPreferences(getActivity());

            String user_type = preferences.getString("user-type","");
            final boolean isteacher = user_type != null && user_type.equals("Teacher");

            mListView.setLongClickable(true);
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

                if (isteacher)
                    info.teachers = null;
                else {
                    String[] userids = r.teachers_ids.split("\\s");
                    info.teachers = new ArrayList<>();
                    for (String userId: userids)
                        info.teachers.add(Database.GetTeacher(userId));
                }
                info.faculty = r.faculty;
                info.group = r.groups;
                info.batch = r.year;
                info.type = r.type;
                info.time = time;
                infos.add(info);

                lastInfo = info;
                lastElement = r;
            }
            mListView.addFooterView(new View(getActivity()));
            RoutineAdapter adapter = new RoutineAdapter(getActivity(), infos);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isteacher) {
                        AttendanceListActivity.info = (RoutineAdapter.Item) mListView.getItemAtPosition(position);
                        if (!AttendanceListActivity.info.isBreak)
                            startActivity(new Intent(getActivity(), AttendanceListActivity.class));
                    }
                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
                    if (isteacher || preferences.getInt("privilege", 0) == 1) {
                        selectedItem = (RoutineAdapter.Item) mListView.getItemAtPosition(position);
                        if (!selectedItem.isBreak)
                            mListView.showContextMenu();

                    }
                    return true;
                }
            });
            return rootView;
        }
        private static RoutineAdapter.Item selectedItem;


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_context_routine, menu);

            String user_type = MainActivity.GetPreferences(getActivity()).getString("user-type", "");
            final boolean isteacher = user_type != null && user_type.equals("Teacher");
            if (!isteacher)
                menu.findItem(R.id.show_attendances).setVisible(false);
            else
                menu.findItem(R.id.show_attendances).setVisible(true);

        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            Intent i;
            switch (item.getItemId()) {
                case R.id.add_notice:
                    InfoAdder.routineItem = selectedItem;
                    i = new Intent(getActivity(), InfoAdder.class);
                    i.putExtra("parentActivity", "Events");
                    startActivity(i);
                    return true;
                case R.id.add_assignment:
                    InfoAdder.routineItem = selectedItem;
                    i = new Intent(getActivity(), InfoAdder.class);
                    i.putExtra("parentActivity", "Assignments");
                    startActivity(i);
                    return true;
                case R.id.show_attendances:
                    AttendanceListActivity.info = selectedItem;
                    startActivity(new Intent(getActivity(), AttendanceListActivity.class));
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
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



