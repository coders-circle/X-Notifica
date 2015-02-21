package com.fabb.notifica;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class AssignmentActivity extends ActionBarActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        expListView = (ExpandableListView) findViewById(R.id.assignment_list);
        prepareListData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        Database db = new Database(this);
        List<Assignment> ass = db.GetAssignments();
        int i = 0;
        for (Assignment as: ass){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(as.time);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");

            String title = as.summary + "\nSubject: " + as.subject.name
                    + "\nDate of Submission:\n    " + format1.format(cal.getTime());

            List<String> childs = new ArrayList<String>();
            String contents = as.details;
            childs.add(contents);

            listDataHeader.add(title);
            listDataChild.put(listDataHeader.get(i), childs);
            i++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_routine) {
            startActivity(new Intent(this, RoutineActivity.class));
            return true;
        }
        if (id == R.id.action_assignments) {
            startActivity(new Intent(this, AssignmentActivity.class));
            return true;
        }
        if (id == R.id.action_events) {
            startActivity(new Intent(this, EventActivity.class));
            return true;
        }
        if (id == R.id.action_study_info) {
            //startActivity(new Intent(this, StudyInfoActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(expListView.getRight() - GetPixelFromDips(50), expListView.getWidth() - GetPixelFromDips(10));
        } else {
            expListView.setIndicatorBoundsRelative(expListView.getRight()- 40, expListView.getWidth());
        }
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}
