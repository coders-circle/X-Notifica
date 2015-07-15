package com.fabb.notifica;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;

public class DrawerAdapter extends ArrayAdapter<String> {
    String[] objects;
    int resource;

    public DrawerAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.resource = resource;
    }

    private HashMap<Integer, Integer> counts = new HashMap<>();
    public void setCount(int index, int count) {
        counts.put(index, count);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

        TextView textView = (TextView)v.findViewById(R.id.drawer_text);
        textView.setText(objects[position]);

        TextView badge = (TextView)v.findViewById(R.id.drawer_badge);
        if (counts.containsKey(position) && counts.get(position)>0) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(counts.get(position) + "");
        }
        else
            badge.setVisibility(View.INVISIBLE);
        return v;

    }
}
