package com.fabb.notifica;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceActivity extends ActionBarActivity {

    public static RoutineAdapter.Item info = null;
    private RoutineAdapter.Item mInfo;
    private List<Student> mStudents;
    private AttendanceAdapter mAdapter;

    private Attendance mAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Toolbar toolbar = (Toolbar)findViewById(R.id.at_settings_toolbar);
        setSupportActionBar(toolbar);
        mInfo = info;
        if (mInfo.group == null)
            mInfo.group = "";

        TextView headingTextView = (TextView)findViewById(R.id.at_heading);

        Calendar cal = Calendar.getInstance();
        String heading = mInfo.batch + " " + mInfo.faculty.name;
        if (!mInfo.group.equals(""))
            heading += " Group: " + mInfo.group;

        DateFormat format1 = DateFormat.getDateInstance();
        heading += "\n" + "Date:  " + format1.format(cal.getTime());

        headingTextView.setText(heading);

        ListView attendanceListView = (ListView)findViewById(R.id.attendance_list_view);

        mAttendance = Database.GetAttedance(info.faculty, info.batch, info.group);
        if (mAttendance == null) {
            mAttendance = new Attendance();
            mAttendance.remoteId = -1;

            if (!mInfo.group.equals("")) {
                mStudents = Student.find(Student.class, "faculty = ? and batch = ? and groups = ?",
                        new String[]{mInfo.faculty.getId()+"", mInfo.batch+"", mInfo.group}, null, "roll", null);
            }
            else
                mStudents = Student.find(Student.class, "faculty = ? and batch = ?",
                        new String[]{mInfo.faculty.getId()+"", mInfo.batch+""}, null, "roll", null);

            mAdapter = new AttendanceAdapter(this, mStudents);
        }
        else {
            List<AttendanceElement> elements = AttendanceElement.find(AttendanceElement.class, "attendance = ?", mAttendance.getId()+"");
            mStudents = new ArrayList<>();
            ArrayList<Boolean> states = new ArrayList<>();
            for (AttendanceElement element: elements) {
                mStudents.add(element.student);
                states.add(element.presence);
            }
            mAdapter = new AttendanceAdapter(this, mStudents, states);
        }

        attendanceListView.setAdapter(mAdapter);

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
        mAttendance.batch = mInfo.batch;
        mAttendance.groups = mInfo.group;
        mAttendance.faculty = mInfo.faculty;
        mAttendance.isUpdated = false;

        mAttendance.save();

        AttendanceElement.deleteAll(AttendanceElement.class, "attendance=?", mAttendance.getId()+"");
        int i = 0;
        for (Student student: mStudents) {
            AttendanceElement element = new AttendanceElement();
            element.attendance = mAttendance;
            element.presence = mAdapter.states.get(i);
            element.student = student;
            element.save();
            ++i;
        }

        new UpdateService.UpdateTask(this).execute();
        finish();
    }


//    public static class PostTask extends AsyncTask<Void, Void, Void> {
//        private final Activity mActivity;
//        JSONObject mJson;
//        String result="";
//        boolean success=false;
//        JSONObject mReturn;
//        boolean mShutdown=false;
//        UpdateService.UpdateResult mUpdateResult = new UpdateService.UpdateResult();
//
//        private ProgressDialog mDialog;
//
//        PostTask(Activity activity, JSONObject json, boolean shutdown, String postingMessage) {
//            mActivity = activity;
//            mJson=json;
//            mShutdown=shutdown;
//
//            mDialog = new ProgressDialog(mActivity);
//            mDialog.setMessage(postingMessage);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            mDialog.show();
//        }
//
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                success = false;
//                Network network = new Network();
//                result = network.PostJson(postUrl, mJson);
//                mReturn = new JSONObject(result);
//                if (mReturn.optString("post_result").equals("Success")) {
//                    UpdateService.Update(mActivity, mUpdateResult);
//                    success = true;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(final Void v) {
//            if (mDialog.isShowing())
//                mDialog.dismiss();
//
//            if (success) {
//                UpdateService.FinishUpdate(mUpdateResult);
//                Toast.makeText(mActivity, "Success", Toast.LENGTH_SHORT).show();
//                if (mShutdown)
//                    mActivity.finish();
//                return;
//            }
//            Toast.makeText(mActivity, "Failed\nCheck your internet connection", Toast.LENGTH_SHORT).show();
//        }
//    }
}