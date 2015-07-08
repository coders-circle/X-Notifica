package com.fabb.notifica;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RoutineAdapter extends BaseAdapter {
    public static class Item {
        public Subject subject;
        public Teacher teacher;
        public String time;

        public boolean isBreak = false;

        public int type;
        public Faculty faculty;
        public String group;
        public int batch;
    }
    Context context;
    ArrayList<Item> array;
    public RoutineAdapter(Activity context, ArrayList<Item> array){
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
        Item info = array.get(position);
        if (convertView==null) {
            if (info.isBreak) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.breaktime, parent, false);
            }
            else {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.routine_item, parent, false);
            }
        }


        TextView teacher = (TextView) convertView.findViewById(R.id.teacher_name);
        TextView time = (TextView) convertView.findViewById(R.id.time);


        if (info.isBreak)
            teacher.setText("Break");
        else {
            TextView subject = (TextView) convertView.findViewById(R.id.subject_name);
            TextView type=  (TextView) convertView.findViewById(R.id.type);
            if (info.subject != null) {
                subject.setText(info.subject.name);
                if (info.type == 0) {
                    type.setText(" L ");
                } else {
                    type.setText(" P ");
                }
            }
            else
                subject.setText("");

            if (info.teacher != null)
                teacher.setText(info.teacher.name);
            else if (info.faculty != null) {
                if (info.group != null && !info.group.equals(""))
                    teacher.setText(info.batch + " " + info.faculty.name + " Group: " + info.group);
                else
                    teacher.setText(info.batch + " " + info.faculty.name);

            }
        }
        time.setText(info.time);
        return convertView;
    }
}
