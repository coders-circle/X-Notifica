package com.fabb.notifica;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment  extends Fragment implements UpdateListener {

    protected InfoListAdapter listAdapter;
    protected ExpandableListView expListView;
    protected List<InfoListAdapter.Item> listItems;
    protected boolean privileged = false;

    protected String info_name = "Event";

    @Override
    public void onCreate(Bundle save) {
        super.onCreate(save);
        SharedPreferences preferences = MainActivity.GetPreferences(getActivity());
        String user_type = preferences.getString("user-type", "");
        if ((user_type != null && user_type.equals("Teacher")) || preferences.getInt("privilege", 0) == 1) {
            setHasOptionsMenu(true);
            privileged = true;
        }
        setRetainInstance(true);
        UpdateService.AddUpdateListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_info, menu);
        registerForContextMenu(expListView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        expListView = (ExpandableListView) getActivity().findViewById(R.id.assignment_list);
        prepareListData();
        listAdapter = new InfoListAdapter(getActivity(), listItems);
        expListView.setAdapter(listAdapter);
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
        Intent i = new Intent(getActivity(), InfoAdder.class);
        i.putExtra("parentActivity", info_name+"s");
        startActivity(i);
    }

    protected ArrayList<Long> mIds = new ArrayList<>();

    protected void prepareListData() {
    }

    @Override
    public void OnUpdateComplete(boolean hasUpdated, int eventCnt, int assignmentCnt) {
        if (!hasUpdated)
            return;
        try {
            prepareListData();
            listAdapter = new InfoListAdapter(getActivity(), listItems);
            expListView.setAdapter(listAdapter);
            expListView.invalidate();
            registerForContextMenu(expListView);
        } catch (Exception ignore) {
        }
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
                json.put("message_type", "Delete "+info_name);
                json.put("user_id", preferences.getString("user-id", ""));
                json.put("password", preferences.getString("password", ""));
                json.put("postid", mIds.get(groupPosition));

                new InfoAdder.PostTask(getActivity(), json, false, "Deleting").execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onContextItemSelected(item);
    }
}
