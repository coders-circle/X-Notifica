package com.fabb.notifica;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AttendanceActivity extends ActionBarActivity {

    public static RoutineAdapter.Item info = null;
    private RoutineAdapter.Item mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = (Toolbar)findViewById(R.id.at_settings_toolbar);
        setSupportActionBar(toolbar);
        mInfo = info;

        TextView headingTextView = (TextView)findViewById(R.id.at_heading);

        List<Student> students;
        String heading = mInfo.batch + " " + mInfo.faculty.name;
        if (mInfo.group != null && !mInfo.group.equals("")) {
            heading += " Group: " + mInfo.group;

            students = Student.find(Student.class, "faculty = ? and batch = ? and groups = ?",
                    new String[]{mInfo.faculty.getId()+"", mInfo.batch+"", mInfo.group}, null, "roll", null);
        }
        else
            students = Student.find(Student.class, "faculty = ? and batch = ?",
                    new String[]{mInfo.faculty.getId()+"", mInfo.batch+""}, null, "roll", null);

        headingTextView.setText(heading);

        ListView attendanceListView = (ListView)findViewById(R.id.attendance_list_view);
        AttendanceAdapter adapter = new AttendanceAdapter(this, students);
        attendanceListView.setAdapter(adapter);
    }

}