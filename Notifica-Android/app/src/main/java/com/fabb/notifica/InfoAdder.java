package com.fabb.notifica;

import android.app.Activity;
import android.app.ProgressDialog;
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

        faculties = Faculty.listAll(Faculty.class);
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
            subjects = Subject.listAll(Subject.class);
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

        grouplist.add("All");
        for (int i=0; i<26; ++i) {
            grouplist.add(String.valueOf((char)(i + 65)));
        }
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
        String user_type = preferences.getString("user-type", "");
        if (user_type == null || user_type.equals("Student")) {
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

        String fb_message;
        Subject subject = null;
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
                posting_message = "Posting Assignment";

                fb_message = "Assignment\n\n";
            }
            else {
                fb_message = "Notice\n\n";
                posting_message = "Posting Notice";
            }

            new PostTask(this, json, true, posting_message).execute();


            String fb_groupId = preferences.getString("fb-group-id", "");
            if (((CheckBox)findViewById(R.id.checkbox_fb_post)).isChecked()
                    && fb_groupId!=null && !fb_groupId.equals("")) {

                // Facebook message details
                if (!groups.equals(""))
                    fb_message += "For group: " + groups;
                if (subject != null)
                    fb_message += "Subject: " + subject.name + "\n\n";
                fb_message += summary + "\n\n" + details + "\n\n";

                DateFormat sdf = DateFormat.getDateInstance();
                fb_message += "Submission Date: " + sdf.format(cal.getTime());

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
                                    Toast.makeText(InfoAdder.this, "Failed to post on Facebook", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(InfoAdder.this, "Posted on Facebook", Toast.LENGTH_SHORT).show();
                                Log.d("FB Post Response", response.toString());
                            }
                        }
                ).executeAsync();
            }

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
                Toast.makeText(mActivity, "Success", Toast.LENGTH_SHORT).show();
                if (mShutdown)
                    mActivity.finish();
                return;
            }
            Toast.makeText(mActivity, "Failed\n"+ mReturn.optString("failure_message"), Toast.LENGTH_SHORT).show();
        }
    }
}
