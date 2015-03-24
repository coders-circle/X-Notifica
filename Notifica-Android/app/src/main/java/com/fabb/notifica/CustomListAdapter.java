package com.fabb.notifica;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class CustomListAdapter extends BaseAdapter {
    public static class CustomListItem {
        public String subjects;
        public String teachers;
        public String times;
    }
    Context context;
    ArrayList<CustomListItem> array;
    public CustomListAdapter(Activity context, ArrayList<CustomListItem> array){
        this.array = array;
        this.context = context;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, parent, false);
        }

        TextView subject = (TextView) convertView.findViewById(R.id.subject_name);
        TextView teacher = (TextView) convertView.findViewById(R.id.teacher_name);
        TextView time = (TextView) convertView.findViewById(R.id.time);

        CustomListItem info = array.get(position);
        subject.setText(info.subjects);
        teacher.setText(info.teachers);
        time.setText(info.times);
        return convertView;
    }
}
