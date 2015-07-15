package com.fabb.notifica;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

public class InfoListAdapter extends BaseExpandableListAdapter {

    public static class Item {
        String summary, details, extra;
        boolean unseen;

        public Item(String summary, String details, String extra, boolean unseen) {
            this.summary = summary;
            this.details = details;
            this.extra = extra;
            this.unseen = unseen;
        }
    }

    private Context mContext;
    private List<Item> mListItems;

    public InfoListAdapter(Context context, List<Item> listItems) {
        this.mContext = context;
        this.mListItems = listItems;
    }

    public void SetListItems(List<Item> listItems) {
        mListItems = listItems;
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.info_item, parent, false);
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
            convertView = inflater.inflate(R.layout.info_group, parent, false);
        }

        if (item.unseen)
            convertView.setBackgroundResource(R.drawable.selector_background_red);
        else
            convertView.setBackgroundResource(R.drawable.selector_background);

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.summary_view);
        lblListHeader.setText(item.summary);

        TextView lblListExtra = (TextView) convertView.findViewById(R.id.extra_view);
        lblListExtra.setText(item.extra);
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
