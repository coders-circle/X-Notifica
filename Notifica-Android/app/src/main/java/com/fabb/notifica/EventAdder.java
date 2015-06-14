package com.fabb.notifica;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


public class EventAdder extends ActionBarActivity {
    private static final String postUrl = "post";
    Spinner mFacultyList;
    Spinner mSubjectList;
    EditText mSummaryEdit;
    EditText mDetailsEdit;
    DatePicker mDateEdit;
    Spinner mGroupsEdit;
    EditText mYearEdit;

    String parentActivity = "Events";

    ArrayList<Faculty> faculties;
    ArrayList<Subject> subjects;

    ArrayList<String> grouplist = new ArrayList<>();

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
        mGroupsEdit = (Spinner) findViewById(R.id.event_edit_groups);
        mSubjectList = (Spinner) findViewById(R.id.event_subject_list);

        Database db = new Database(this);
        faculties = db.GetFaculties();
        ArrayList<String> faculty_names = new ArrayList<>();
        faculty_names.add("None");
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
            ((LinearLayout)mSubjectList.getParent()).removeView(mSubjectList);
            View temp = findViewById(R.id.event_subject_textView);
            ((LinearLayout)temp.getParent()).removeView(temp);
        }

        SharedPreferences preferences = MainActivity.GetPreferences(this);
        if (preferences.getString("user-type", "").equals("Student")) {
            View temp = mFacultyList;
            ((LinearLayout)temp.getParent()).removeView(temp);
            temp = findViewById(R.id.event_faculty_textview);
            ((LinearLayout)temp.getParent()).removeView(temp);
            temp = mYearEdit;
            ((LinearLayout)temp.getParent()).removeView(temp);
            temp = findViewById(R.id.event_batch_textview);
            ((LinearLayout)temp.getParent()).removeView(temp);
        }

        grouplist.add("All");
        grouplist.add("A");
        grouplist.add("B");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grouplist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupsEdit.setAdapter(adapter);
        mGroupsEdit.setSelection(0);
    }

    public void onPostClicked(View view) {
        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setEnabled(false);
        SharedPreferences preferences = MainActivity.GetPreferences(this);

        String summary = mSummaryEdit.getText().toString();
        String details = mDetailsEdit.getText().toString();
        String faculty_code = "";
        int year = 0;
        if (preferences.getString("user-type", "").equals("Student")) {
            faculty_code = preferences.getString("faculty-code", "");
            year = preferences.getInt("batch", 0);
        }
        else {
            if (mFacultyList.getSelectedItemId() > 0)
                faculty_code = faculties.get((int) mFacultyList.getSelectedItemId() - 1).code;
            if (!mYearEdit.getText().toString().equals(""))
                year = Integer.parseInt(mYearEdit.getText().toString());
        }
        String groups = grouplist.get((int)mGroupsEdit.getSelectedItemId());
        if (groups.equals("All"))
            groups = "";

        Calendar cal = Calendar.getInstance();
        cal.set(mDateEdit.getYear(), mDateEdit.getMonth(), mDateEdit.getDayOfMonth());
        long date = cal.getTimeInMillis()/1000;

        JSONObject json = new JSONObject();
        try {
            json.put("message_type", "Post Event");
            json.put("user_id", preferences.getString("user-id",""));
            json.put("password", preferences.getString("password", ""));
            json.put("summary", summary);
            json.put("details", details);
            json.put("date", date);
            json.put("year", year);
            json.put("groups", groups);
            json.put("faculty_code", faculty_code);

            if (parentActivity.equals("Assignments")) {
                if (mSubjectList.getSelectedItemId() >= 0) {
                    String subject_code = subjects.get((int) mSubjectList.getSelectedItemId()).code;
                    json.put("subject_code", subject_code);
                }
                else
                    return;

                json.put("message_type", "Post Assignment");
            }

            new PostTask(this, json, true).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class PostTask extends AsyncTask<Void, Void, Void> {
        private final Activity mActivity;
        JSONObject mJson;
        String result="";
        boolean success=false;
        JSONObject mReturn;
        boolean mShutdown=false;
        UpdateService.UpdateResult mUpdateResult = new UpdateService.UpdateResult();

        PostTask(Activity activity, JSONObject json, boolean shutdown) {
            mActivity = activity;
            mJson=json;
            mShutdown=shutdown;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                success = false;
                Network network = new Network(mActivity);
                result = network.PostJson(postUrl, mJson);
                mReturn = new JSONObject(result);
                if (mReturn.optString("post_result").equals("Success")) {
                    UpdateService.Update(mActivity, mUpdateResult);
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            if (success) {
                UpdateService.FinishUpdate(mUpdateResult);
                Toast.makeText(mActivity, "Success", Toast.LENGTH_SHORT).show();
                if (mShutdown)
                    mActivity.finish();
                return;
            }
            Toast.makeText(mActivity, "Failed\n"+ mReturn.optString("failure_message"), Toast.LENGTH_SHORT).show();
        }
    }
}
