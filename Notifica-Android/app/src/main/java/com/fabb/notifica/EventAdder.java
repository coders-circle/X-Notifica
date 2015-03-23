package com.fabb.notifica;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EventAdder extends ActionBarActivity {
    Spinner mFacultyList;
    Spinner mSubjectList;
    EditText mSummaryEdit;
    EditText mDetailsEdit;
    DatePicker mDateEdit;
    EditText mGroupsEdit;
    EditText mYearEdit;

    String parentActivity = "Events";

    ArrayList<Faculty> faculties;
    ArrayList<Subject> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_adder);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            parentActivity = extras.getString("parentActivity");
        }

        mFacultyList = (Spinner) findViewById(R.id.event_faculty_list);
        mSummaryEdit = (EditText) findViewById(R.id.event_edit_summary);
        mDetailsEdit = (EditText) findViewById(R.id.event_edit_details);
        mDateEdit = (DatePicker) findViewById(R.id.event_edit_date);
        mYearEdit = (EditText) findViewById(R.id.event_edit_year);
        mGroupsEdit = (EditText) findViewById(R.id.event_edit_groups);
        mSubjectList = (Spinner) findViewById(R.id.event_subject_list);

        Database db = new Database(this);
        faculties = db.GetFaculties();
        ArrayList<String> faculty_names = new ArrayList<>();
        for (Faculty f: faculties) {
            faculty_names.add(f.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, faculty_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFacultyList.setAdapter(adapter);
        mFacultyList.setSelection(0);

        if (parentActivity.equals("Assignments")) {
            subjects = db.GetSubjects();
            ArrayList<String> subject_names = new ArrayList<>();
            for (Subject f: subjects) {
                subject_names.add(f.name);
            }
            ArrayAdapter<String> sadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subject_names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSubjectList.setAdapter(sadapter);
            mSubjectList.setSelection(0);
        }
        else {
            View temp = findViewById(R.id.event_subject_textView);
            ((LinearLayout)mSubjectList.getParent()).removeView(mSubjectList);
            ((LinearLayout)temp.getParent()).removeView(temp);
        }
    }

    public void onPostClicked(View view) {
        String summary = mSummaryEdit.getText().toString();
        String details = mDetailsEdit.getText().toString();
        String faculty_code = faculties.get((int)mFacultyList.getSelectedItemId()).code;
        int year = Integer.parseInt(mYearEdit.getText().toString());
        String groups = mGroupsEdit.getText().toString();
        long date = 0;

        Calendar cal = Calendar.getInstance();
        cal.set(mDateEdit.getYear(), mDateEdit.getMonth(), mDateEdit.getDayOfMonth());
        date = cal.getTimeInMillis()/1000;

        if (parentActivity.equals("Assignments")) {
            String subject_code = subjects.get((int)mSubjectList.getSelectedItemId()).code;
        }
        Toast.makeText(this, "Posting data: \n" + summary + "\n" + details + "\n"
                +faculty_code + "\n" + date + "\n" + year + "\n" + groups, Toast.LENGTH_SHORT).show();
    }
}
