package com.fabb.notifica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements UpdateListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mPageTitles;
    private ActionBarDrawerToggle mDrawerToggle;

    public Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database db = new Database(this);
        db.DeletePassedData();

        SharedPreferences preferences = GetPreferences(this);
        String toast = preferences.getString("user-name", "") + "\n" + preferences.getString("user-type", "");
        Toast.makeText(this, toast, Toast.LENGTH_LONG).show();

        //GcmRegister.Register(this);

        mPageTitles = getResources().getStringArray(R.array.page_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,
                toolbar,               /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //if (getSupportActionBar() != null)
                    //getSupportActionBar().setTitle("Notifica");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //if (getSupportActionBar() != null)
                    //getSupportActionBar().setTitle("Notifica");
            }
        };


        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mPageTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        selectItem(0);

        UpdateService.AddUpdateListener(this);
    }

    private int new_assignment_cnt = 0;
    private int new_event_cnt = 0;
    public void UpdateDrawer() {
        if (new_assignment_cnt > 0)
            mPageTitles[1] = "Assignments    (" + new_assignment_cnt + " new)";
        else
            mPageTitles[1] = "Assignments";
        if (new_event_cnt > 0)
            mPageTitles[2] = "Events    (" + new_event_cnt + " new)";
        else
            mPageTitles[2] = "Events";
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mPageTitles));
    }

    @Override
    public void OnUpdateComplete(boolean hasUpdated, int eventCnt, int assignmentCnt) {
        if (menu != null)
            menu.findItem(R.id.action_update).setVisible(true);

        if (hasUpdated) {
            new_assignment_cnt = assignmentCnt;
            new_event_cnt = eventCnt;
            UpdateDrawer();
        }

        Database db = new Database(this);
        db.DeletePassedData();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private Fragment routine_fragment = new RoutineFragment();
    private Fragment assignment_fragment = new AssignmentFragment();
    private Fragment event_fragment = new EventFragment();
    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = routine_fragment;
                break;
            case 1:
                fragment = assignment_fragment;
                new_assignment_cnt = 0;
                UpdateDrawer();
                break;
            case 2:
                fragment = event_fragment;
                new_event_cnt = 0;
                UpdateDrawer();
                break;
            default:
                fragment = null;
        }
        if (fragment != null) {// Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPageTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logout) {
            LogOut();
            return true;
        }
        else if (id == R.id.action_update) {
            item.setVisible(false);
            new UpdateService.UpdateTask(this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void LogOut() {
        Database db = new Database(this);
        db.DeleteAll();

        SharedPreferences preferences = GetPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("logged-in", false);
        editor.remove("password");
        editor.apply();

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public static SharedPreferences GetPreferences(Context context) {
        return context.getSharedPreferences("Notifica_Main_Preferences", Context.MODE_PRIVATE);
    }

}
