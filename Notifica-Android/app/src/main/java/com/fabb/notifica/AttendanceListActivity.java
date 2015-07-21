package com.fabb.notifica;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceListActivity extends ActionBarActivity {

    public static RoutineAdapter.Item info = null;
    private RoutineAdapter.Item mInfo;

    private ListView attendanceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_list);

        Toolbar toolbar = (Toolbar)findViewById(R.id.atlist_settings_toolbar);
        setSupportActionBar(toolbar);
        mInfo = info;
        if (mInfo.group == null)
            mInfo.group = "";

        TextView headingTextView = (TextView)findViewById(R.id.atlist_heading);

        String heading = mInfo.batch + " " + mInfo.faculty.name;
        if (!mInfo.group.equals(""))
            heading += " Group: " + mInfo.group;

        headingTextView.setText(heading);

        final List<Attendance> attendanceList = Database.GetAttendances(mInfo.faculty, mInfo.batch, mInfo.group);
        attendanceListView = (ListView)findViewById(R.id.atl_attendance_list_view);
        SetAdapterData(attendanceList);

        attendanceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AttendanceActivity.attendance = attendanceList.get(position);
                startActivityForResult(new Intent(AttendanceListActivity.this, AttendanceActivity.class), 100);
            }
        });
    }

    private void SetAdapterData(List<Attendance> attendanceList) {
        Calendar cal = Calendar.getInstance();
        DateFormat format1 = DateFormat.getDateInstance();

        List<String> attendances = new ArrayList<>();
        for (Attendance attendance: attendanceList) {
            cal.setTimeInMillis(attendance.date*1000);
            attendances.add(format1.format(cal.getTime()) + " - " + attendance.GetPresentNumber() + " students present");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendances);
        attendanceListView.setAdapter(adapter);
    }

    public void onNewAttendance(View view) {
        Attendance attendance = new Attendance();
        attendance.batch = mInfo.batch;
        attendance.groups = mInfo.group;
        attendance.faculty = mInfo.faculty;
        attendance.remoteId = -1;
        AttendanceActivity.attendance = attendance;
        startActivityForResult(new Intent(this, AttendanceActivity.class), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SetAdapterData(Database.GetAttendances(mInfo.faculty, mInfo.batch, mInfo.group));
    }
}