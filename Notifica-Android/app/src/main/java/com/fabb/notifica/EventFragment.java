package com.fabb.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class EventFragment extends Fragment implements UpdateListener {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public void onCreate(Bundle save)
    {
        super.onCreate(save);
        SharedPreferences preferences = MainActivity.GetPreferences(getActivity());
        if (preferences.getString("user-type", "").equals("Teacher")
                || preferences.getInt("privilege", 0) == 1)
            setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_item) {
            AddItem();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void AddItem() {
        Intent i = new Intent(getActivity(), EventAdder.class);
        i.putExtra("parentActivity", "Events");
        startActivity(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        expListView = (ExpandableListView) getActivity().findViewById(R.id.assignment_list);
        prepareListData();
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        expListView.setIndicatorBounds(width - GetPixelFromDips(70), width - GetPixelFromDips(20));

        UpdateService.AddUpdateListener(this);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        Database db = new Database(getActivity());
        List<Event> ass = db.GetEvents();
        int i = 0;
        for (Event as: ass){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(as.date*1000);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

            String title = as.summary
                    + "\nDate:  " + format1.format(cal.getTime());

            List<String> children = new ArrayList<>();
            String contents = as.details;
            children.add(contents);

            listDataHeader.add(title);
            listDataChild.put(listDataHeader.get(i), children);
            i++;
        }
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void OnUpdated(int eventCnt, int assignmentCnt, int routineCnt) {
        /*
        TODO: Refresh event list as database is just updated
        * */
     }
}
