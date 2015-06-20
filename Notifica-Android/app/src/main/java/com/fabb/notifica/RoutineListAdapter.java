package com.fabb.notifica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

public class RoutineListAdapter extends BaseExpandableListAdapter {

    public static class Item {
        String summary, details, extra;
        public Item(String summary, String details, String extra) {
            this.summary = summary;
            this.details = details;
            this.extra = extra;
        }
    }

    private Context mContext;
    private List<Item> mListItems;

    public RoutineListAdapter(Context context, List<Item> listItems) {
        this.mContext = context;
        this.mListItems = listItems;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListItems.get(groupPosition).details;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.details_view);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListItems.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mListItems.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Item item = (Item) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        }

        String headerTitle = item.summary + "\n" + item.extra;
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.summary_view);
        lblListHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
