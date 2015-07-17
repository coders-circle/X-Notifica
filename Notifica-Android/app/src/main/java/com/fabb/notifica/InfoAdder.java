package com.fabb.notifica;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class InfoAdder extends ActionBarActivity {
    private static final String postUrl = "post";
    public static RoutineAdapter.Item routineItem = null;

    Spinner mFacultyList;
    Spinner mSubjectList;
    EditText mSummaryEdit;
    EditText mDetailsEdit;
    DatePicker mDateEdit;
    Spinner mGroupsEdit;
    EditText mYearEdit;

    String parentActivity = "Events";

    List<Faculty> faculties;
    List<Subject> subjects;

    ArrayList<String> grouplist = new ArrayList<>();
    private boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_adder);

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

        SharedPreferences preferences = MainActivity.GetPreferences(this);
        String user_type = preferences.getString("user-type", "");
        if (user_type == null || user_type.equals("Student")) {
            View temp = mFacultyList;
            ((LinearLayout)temp.getParent()).removeView(temp);
            temp = findViewById(R.id.event_faculty_textview);
            ((LinearLayout)temp.getParent()).removeView(temp);
            temp = mYearEdit;
            ((LinearLayout)temp.getParent()).removeView(temp);
            temp = findViewById(R.id.event_batch_textview);
            ((LinearLayout)temp.getParent()).removeView(temp);
        }
        else {
            isTeacher = true;
            if (routineItem != null)
                mYearEdit.setText(routineItem.batch+"");
        }

        if (isTeacher) {
            faculties = Faculty.listAll(Faculty.class);
            ArrayList<String> faculty_names = new ArrayList<>();
            faculty_names.add("None");
            int faculty_selection = 0;
            int i = 0;
            for (Faculty f : faculties) {
                faculty_names.add(f.name);
                if (routineItem != null && routineItem.faculty.code.equals(f.code))
                    faculty_selection = i+1;
                i++;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, faculty_names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mFacultyList.setAdapter(adapter);
            mFacultyList.setSelection(faculty_selection);
        }

        if (parentActivity.equals("Assignments")) {
            int subject_selection = 0;
            subjects = Subject.listAll(Subject.class);
            ArrayList<String> subject_names = new ArrayList<>();
            int i = 0;
            for (Subject f: subjects) {
                subject_names.add(f.name);
                if (routineItem!=null && routineItem.subject.code.equals(f.code))
                    subject_selection=i;
                i++;
            }
            ArrayAdapter<String> sadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subject_names);
            sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSubjectList.setAdapter(sadapter);
            mSubjectList.setSelection(subject_selection);
        }
        else {
            ((LinearLayout)mSubjectList.getParent()).removeView(mSubjectList);
            View temp = findViewById(R.id.event_subject_textView);
            ((LinearLayout)temp.getParent()).removeView(temp);
        }

        grouplist.add("All");
        int group_selection = 0;
        for (int i=0; i<26; ++i) {
            String val = String.valueOf((char) (i + 65));
            grouplist.add(val);
            if (routineItem != null && val.equalsIgnoreCase(routineItem.group))
                group_selection = i+1;
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grouplist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGroupsEdit.setAdapter(adapter);
        mGroupsEdit.setSelection(group_selection);


        String fb_groupId = preferences.getString("fb-group-id", "");
        if (fb_groupId==null || fb_groupId.equals(""))
            findViewById(R.id.checkbox_fb_post).setVisibility(View.INVISIBLE);
        else {
            findViewById(R.id.checkbox_fb_post).setVisibility(View.VISIBLE);
        }


        findViewById(R.id.event_check_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateEdit.setEnabled(!((CheckBox)v).isChecked());
            }
        });
    }

    public void onPostClicked(View view) {
        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setEnabled(false);
        SharedPreferences preferences = MainActivity.GetPreferences(this);

        String summary = mSummaryEdit.getText().toString();
        String details = mDetailsEdit.getText().toString();
        String faculty_code = "";
        int year = 0;

        if (!isTeacher) {
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

        long date = -1;
        if (!((CheckBox)findViewById(R.id.event_check_date)).isChecked()) {
            Calendar cal = Calendar.getInstance();
            cal.set(mDateEdit.getYear(), mDateEdit.getMonth(), mDateEdit.getDayOfMonth());
            date = cal.getTimeInMillis() / 1000;
        }

        JSONObject json = new JSONObject();

        String title;
        Subject subject = null;
        try {
            json.put("user_id", preferences.getString("user-id",""));
            json.put("password", preferences.getString("password", ""));
            json.put("summary", summary);
            json.put("details", details);
            json.put("date", date);
            json.put("year", year);
            json.put("groups", groups);
            json.put("faculty_code", faculty_code);

            String posting_message;
            if (parentActivity.equals("Assignments")) {
                if (mSubjectList.getSelectedItemId() >= 0) {
                    subject = subjects.get((int) mSubjectList.getSelectedItemId());
                    String subject_code = subject.code;
                    json.put("subject_code", subject_code);
                }
                else
                    return;

                json.put("message_type", "Post Assignment");
                title = "Assignment";
                posting_message = "Posting Assignment";
            }
            else {
                json.put("message_type", "Post Event");
                title = "Notice";
                posting_message = "Posting Notice";
            }

            new PostTask(this, json, true, posting_message).execute();


            if (!((CheckBox)findViewById(R.id.checkbox_fb_post)).isChecked()) {
                PostToFacebook(this, title, groups, date, subject, summary, details, preferences.getString("user-name", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PostToFacebook(final Context context, String title, String groups, long date, Subject subject, String summary, String details, String posted_by) {
        SharedPreferences preferences = MainActivity.GetPreferences(context);
        String fb_groupId = preferences.getString("fb-group-id", "");
        if (fb_groupId!=null && !fb_groupId.equals("")) {
            String fb_message = title;
            if (subject != null)
                fb_message += " of " + subject.name;
            fb_message += "\n" + summary;
            if (!posted_by.equals(""))
                fb_message += "\nPosted by: " + posted_by;
            if (date != -1) {
                DateFormat sdf = DateFormat.getDateInstance();
                fb_message += "\nDate: " + sdf.format(date * 1000);
            }
            if (groups != null && !groups.equals(""))
                fb_message += "\nFor group: " + groups;
            fb_message += "\n\n" + details;
            fb_message += "\n\nPosted from: Notifica (http://notifica.herokuapp.com/)";

            // Post the message using graph api
            Bundle params = new Bundle();
            params.putString("message", fb_message);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + fb_groupId + "/feed",
                    params,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            if (response.getRawResponse() == null || response.getError() != null)
                                Toast.makeText(context, "Failed to post on Facebook", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context, "Posted on Facebook", Toast.LENGTH_SHORT).show();
                            Log.d("FB Post Response", response.toString());
                        }
                    }
            ).executeAsync();
        }
        else {
            String message = "You are not logged in using facebook \nor you have not selected your group to share posts on. \nGo to Settings and change facebook connection settings to properly configure.";
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setTitle("Facebook improperly configured")
                    .setPositiveButton("Ok", null);
            AlertDialog dialog = builder.create();
            dialog.show();
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

        private ProgressDialog mDialog;

        PostTask(Activity activity, JSONObject json, boolean shutdown, String postingMessage) {
            mActivity = activity;
            mJson=json;
            mShutdown=shutdown;

            mDialog = new ProgressDialog(mActivity);
            mDialog.setMessage(postingMessage);
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                success = false;
                Network network = new Network();
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
            if (mDialog.isShowing())
                mDialog.dismiss();

            if (success) {
                UpdateService.FinishUpdate(mUpdateResult);
                //Toast.makeText(mActivity, "Success", Toast.LENGTH_SHORT).show();
                if (mShutdown)
                    mActivity.finish();
                return;
            }
            Toast.makeText(mActivity, "Failed\nCheck your internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
