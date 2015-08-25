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
        public ArrayList<Teacher> teachers;
        public String time;

        public String remarks;

        public Item alternateItem = null;

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
        try {
            Item info = array.get(position);
            if (convertView == null) {
                if (info.isBreak) {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.routine_item_break, parent, false);
                } else {
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.routine_item, parent, false);
                }
            }


            TextView teacher = (TextView) convertView.findViewById(R.id.teacher_name);
            TextView time = (TextView) convertView.findViewById(R.id.time);

            if (info.isBreak) {
                teacher.setText("Break");
                time.setText("");
            } else {
                TextView subject = (TextView) convertView.findViewById(R.id.subject_name);
                TextView type = (TextView) convertView.findViewById(R.id.type);
                TextView remarks = (TextView) convertView.findViewById(R.id.remarks);

                if (info.subject != null) {
                    subject.setText(info.subject.name);
                    if (info.type == 0) {
                        type.setText(" L ");
                    } else {
                        type.setText(" P ");
                    }
                } else
                    subject.setText("");

                if (info.teachers != null) {
                    String teacherNames = "";
                    for (Teacher t : info.teachers)
                        if (t != null) {
                            if (teacherNames.length() > 0)
                                teacherNames += ", ";
                            teacherNames += t.name;
                        }
                    teacher.setText(teacherNames);
                } else if (info.faculty != null) {
                    if (info.group != null && !info.group.equals(""))
                        teacher.setText(info.batch + " " + info.faculty.name + " Group: " + info.group);
                    else
                        teacher.setText(info.batch + " " + info.faculty.name);

                }

                if (info.remarks != null && !info.remarks.equals("")) {
                    remarks.setText(info.remarks);
                    remarks.setVisibility(View.VISIBLE);
                } else
                    remarks.setVisibility(View.GONE);
            }
            time.setText(info.time);

            View alternateView = convertView.findViewById(R.id.alternate_frame);
            if (alternateView != null) {
                if (info.alternateItem != null) {
                    alternateView.setVisibility(View.VISIBLE);

                    Item alternateItem = info.alternateItem;
                    TextView teacher2 = (TextView) alternateView.findViewById(R.id.teacher_name2);
                    TextView subject2 = (TextView) alternateView.findViewById(R.id.subject_name2);
                    TextView type2 = (TextView) alternateView.findViewById(R.id.type2);
                    TextView remarks2 = (TextView) convertView.findViewById(R.id.remarks2);

                    if (alternateItem.subject != null) {
                        subject2.setText(alternateItem.subject.name);
                        if (alternateItem.type == 0) {
                            type2.setText(" L ");
                        } else {
                            type2.setText(" P ");
                        }
                    } else
                        subject2.setText("");

                    if (alternateItem.teachers != null) {
                        String teacherNames = "";
                        for (Teacher t : alternateItem.teachers)
                            if (t != null) {
                                if (teacherNames.length() > 0)
                                    teacherNames += ", ";
                                teacherNames += t.name;
                            }
                        teacher2.setText(teacherNames);
                    } else if (alternateItem.faculty != null) {
                        if (alternateItem.group != null && !alternateItem.group.equals(""))
                            teacher2.setText(alternateItem.batch + " " + alternateItem.faculty.name + " Group: " + alternateItem.group);
                        else
                            teacher2.setText(alternateItem.batch + " " + alternateItem.faculty.name);

                    }

                    if (alternateItem.remarks != null && !alternateItem.remarks.equals("")) {
                        remarks2.setText(alternateItem.remarks);
                        remarks2.setVisibility(View.VISIBLE);
                    } else
                        remarks2.setVisibility(View.GONE);
                } else
                    alternateView.setVisibility(View.GONE);
            }
        }
        catch (Exception ignored) {}
        return convertView;
    }
}
