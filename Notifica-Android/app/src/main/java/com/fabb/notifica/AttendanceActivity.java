package com.fabb.notifica;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceActivity extends ActionBarActivity {

    public static Attendance attendance;
    private List<Student> mStudents;
    private ListView mListView;

    private Attendance mAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = (Toolbar)findViewById(R.id.at_settings_toolbar);
        setSupportActionBar(toolbar);
        mAttendance = attendance;

        TextView headingTextView = (TextView)findViewById(R.id.at_heading);

        Calendar cal = Calendar.getInstance();
        String heading = attendance.batch + " " + mAttendance.faculty.name;
        if (!mAttendance.groups.equals(""))
            heading += " Group: " + mAttendance.groups;

        DateFormat format1 = DateFormat.getDateInstance();
        heading += "\n" + "Date:  " + format1.format(cal.getTime());

        headingTextView.setText(heading);


        mListView = (ListView)findViewById(R.id.attendance_list_view);

        List<AttendanceElement> elements = AttendanceElement.find(AttendanceElement.class, "attendance = ?", mAttendance.getId()+"");
        ArrayList<Boolean> states = new ArrayList<>();

        if (elements.size() == 0) {
            if (!mAttendance.groups.equals("")) {
                mStudents = Student.find(Student.class, "faculty = ? and batch = ? and groups = ?",
                        new String[]{mAttendance.faculty.getId()+"", mAttendance.batch+"", mAttendance.groups}, null, "roll", null);
            }
            else
                mStudents = Student.find(Student.class, "faculty = ? and batch = ?",
                        new String[]{mAttendance.faculty.getId()+"", mAttendance.batch+""}, null, "roll", null);
            for (int i=0; i<mStudents.size(); ++i)
                states.add(true);
        }
        else {
            mStudents = new ArrayList<>();
            for (AttendanceElement element: elements) {
                mStudents.add(element.student);
                states.add(element.presence);
            }
        }

        List<String> labels = new ArrayList<>();
        for (Student st: mStudents)
            labels.add(st.roll + ". " + st.name);

        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, labels));
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        int i=0;
        for (Boolean state: states) {
            mListView.setItemChecked(i, state);
            ++i;
        }

        mAttendance.date = cal.getTimeInMillis() / 1000;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_attendance) {
            Save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Save() {
        mAttendance.isUpdated = false;
        mAttendance.save();

        SparseBooleanArray states = mListView.getCheckedItemPositions();
        AttendanceElement.deleteAll(AttendanceElement.class, "attendance=?", mAttendance.getId()+"");
        int i = 0;
        for (Student student: mStudents) {
            AttendanceElement element = new AttendanceElement();
            element.attendance = mAttendance;
            element.presence = states.get(i);
            element.student = student;
            element.save();
            ++i;
        }

        new UpdateService.UpdateTask(this).execute();
        finish();
    }
}