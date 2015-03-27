package com.fabb.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONObject;

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
    boolean privileged = false;

    @Override
    public void onCreate(Bundle save)
    {
        super.onCreate(save);
        SharedPreferences preferences = MainActivity.GetPreferences(getActivity());
        if (preferences.getString("user-type", "").equals("Teacher")
                || preferences.getInt("privilege", 0) == 1) {
            setHasOptionsMenu(true);
            privileged = true;
        }
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event, menu);
        registerForContextMenu(expListView);
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

        UpdateService.AddUpdateListener(this);
    }

    private ArrayList<Long> mIds = new ArrayList<>();
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
            mIds.add(as.id);
            listDataChild.put(listDataHeader.get(i), children);
            i++;
        }
    }

    @Override
    public void OnUpdated(int eventCnt, int assignmentCnt, int routineCnt) {
        try {
            prepareListData();
            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
            expListView.invalidate();
            registerForContextMenu(expListView);
        }
        catch (Exception ignore)
        {}
     }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (!privileged)
            return;
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            menu.add("Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!privileged)
            return super.onContextItemSelected(item);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item
                .getMenuInfo();
        SharedPreferences preferences = MainActivity.GetPreferences(getActivity());

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            JSONObject json = new JSONObject();
            try {
                json.put("message_type", "Delete Event");
                json.put("user_id", preferences.getString("user-id",""));
                json.put("password", preferences.getString("password", ""));
                json.put("postid", mIds.get(groupPosition));

                new EventAdder.PostTask(getActivity(), json, false).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onContextItemSelected(item);
    }
}
