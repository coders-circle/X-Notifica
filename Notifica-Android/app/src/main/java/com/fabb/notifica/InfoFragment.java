package com.fabb.notifica;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment  extends Fragment implements UpdateListener {

    protected InfoListAdapter listAdapter;
    protected ExpandableListView expListView;
    protected List<InfoListAdapter.Item> listItems;
    protected boolean privileged = false;

    protected String info_name = "Event";
    protected String display_name = "Notice";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        expListView = (ExpandableListView) getActivity().findViewById(R.id.assignment_list);
        prepareListData();
        listAdapter = new InfoListAdapter(getActivity(), listItems);
        expListView.setAdapter(listAdapter);
        RefreshEmptyTextView();
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
        RefreshItems();
    }

    private void RefreshEmptyTextView() {
        TextView emptytv = (TextView)getActivity().findViewById(R.id.empty_info_text_view);
        if (listItems.size() == 0) {
            emptytv.setText("No recent " + display_name.toLowerCase() + "s found");
            emptytv.setVisibility(View.VISIBLE);
        }
        else
            emptytv.setVisibility(View.INVISIBLE);
    }

    protected void RefreshItems() {
        try {
            prepareListData();
            listAdapter.SetListItems(listItems);
            RefreshEmptyTextView();

//            listAdapter = new InfoListAdapter(getActivity(), listItems);
//            expListView.setAdapter(listAdapter);
//            expListView.invalidate();
//            registerForContextMenu(expListView);
        } catch (Exception ignore) {}
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (!privileged)
            return;
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_context_info, menu);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (!privileged)
            return super.onContextItemSelected(item);

        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item
                .getMenuInfo();
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        final int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            if (item.getItemId() == R.id.delete_info) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete " + display_name)
                        .setMessage("Are you sure you want to delete this?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences preferences = MainActivity.GetPreferences(getActivity());
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("message_type", "Delete " + info_name);
                                    json.put("user_id", preferences.getString("user-id", ""));
                                    json.put("password", preferences.getString("password", ""));
                                    json.put("postid", mIds.get(groupPosition));

                                    new InfoAdder.PostTask(getActivity(), json, false, "Deleting").execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else if (item.getItemId() == R.id.share_info) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Share " + info_name + " to Facebook")
                        .setMessage("Are you sure you want to share this post?")
                        .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PostToFacebook(mIds.get(groupPosition));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        }
        return super.onContextItemSelected(item);
    }

    protected void PostToFacebook(long id) {}
}
