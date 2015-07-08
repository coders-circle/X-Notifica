package com.fabb.notifica;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends BaseAdapter {
    private Context context;
    private List<Student> students;
    public ArrayList<Boolean> states = new ArrayList<>();

    public AttendanceAdapter(Activity context, List<Student> students){
        this.students = students;
        this.context = context;
        for (int i=0; i<students.size(); ++i)
            states.add(false);
    }

    public AttendanceAdapter(Activity context, List<Student> students, ArrayList<Boolean> states){
        this.students = students;
        this.context = context;
        this.states = states;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.attendance_item, parent, false);
        }

        TextView textview = (TextView)convertView.findViewById(R.id.attendance_textview);

        Student student = students.get(position);
        textview.setText(student.roll + ". " + student.name);


        CheckBox checkbox = (CheckBox)convertView.findViewById(R.id.attendance_checkbox);
        checkbox.setChecked(states.get(position));
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                states.set(position, isChecked);
            }
        });
        return convertView;
    }
}
