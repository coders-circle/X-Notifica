package com.fabb.notifica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends ActionBarActivity implements UpdateListener {

    public static boolean logged_out = false;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mPageTitles;
    private ActionBarDrawerToggle mDrawerToggle;
    public CustomSwipeRefreshLayout swipeRefreshLayout;

    public Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());

        ShowHashKey();

        Database.DeleteExpired();

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
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };


        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mPageTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        toolbar.hideOverflowMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String notification_title = getIntent().getStringExtra("notification-title");
        int fragment_id;
        if (notification_title == null)
            fragment_id = 0;
        else if (notification_title.equals("Assignment"))
            fragment_id = 1;
        else
            fragment_id = 2;
        selectItem(fragment_id);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("pref_key_auto_update", true)) {
            UpdateService.AddUpdateListener(this);
            new UpdateService.UpdateTask(this).execute();
        }

        SharedPreferences preferences = GetPreferences(this);
        String token = preferences.getString("gcm_token", "");
        if (token == null || token.equals("")) {
            Intent intent = new Intent(this, GcmRegisterIntent.class);
            startService(intent);
        }

        token = preferences.getString("gcm_token", "");
        if (token != null && !token.equals(""))
        if (!GetPreferences(this).getBoolean("gcm_token_sent", false))
            GcmRegisterIntent.sendRegistrationToServer(this,  preferences.getString("gcm_token", ""));

        swipeRefreshLayout = (CustomSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        new UpdateService.UpdateTask(MainActivity.this).execute();
                                    }
                                }
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UpdateService.UpdateTask(MainActivity.this).execute();
            }
        });
    }

//    private int new_assignment_cnt = 0;
//    private int new_event_cnt = 0;
//    public void UpdateDrawer() {
//        if (new_assignment_cnt > 0)
//            mPageTitles[1] = "Assignments    (" + new_assignment_cnt + " new)";
//        else
//            mPageTitles[1] = "Assignments";
//        if (new_event_cnt > 0)
//            mPageTitles[2] = "Notices    (" + new_event_cnt + " new)";
//        else
//            mPageTitles[2] = "Notices";
//        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, mPageTitles));
//    }

    @Override
    public void OnUpdateComplete(boolean hasUpdated, int eventCnt, int assignmentCnt) {
//        if (menu != null) {
//            menu.findItem(R.id.action_update).setVisible(true);
//            menu.findItem(R.id.action_clean_update).setVisible(true);
//        }

        if (hasUpdated) {
//                String res = "";
//                if (assignmentCnt > 0)
//                    res += "\n" + assignmentCnt + " new " + (assignmentCnt > 1 ? "assignments" : "assignment");
//                if (eventCnt > 0)
//                    res += "\n" + eventCnt + " new "+ (eventCnt > 1 ? "notices" : "notice");
//                Toast.makeText(this, "Up-To-Date" + res, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Up-To-Date", Toast.LENGTH_LONG).show();

//            new_assignment_cnt = assignmentCnt;
//            new_event_cnt = eventCnt;
//            UpdateDrawer();
        }
        else
            Toast.makeText(this, "Couldn't update.\nCheck your internet connection.", Toast.LENGTH_LONG).show();

        swipeRefreshLayout.setRefreshing(false);
        Database.DeleteExpired();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private Fragment routine_fragment = new RoutineFragment();
    private Fragment assignment_fragment = new AssignmentFragment();
    private Fragment event_fragment = new NoticeFragment();
    private int lastItemSelected = 0;
    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = routine_fragment;
                break;
            case 1:
                fragment = assignment_fragment;
//                new_assignment_cnt = 0;
//                UpdateDrawer();
                break;
            case 2:
                fragment = event_fragment;
//                new_event_cnt = 0;
//                UpdateDrawer();
                break;
            case 3:
                startActivityForResult(new Intent(this, SettingsActivity.class), 0);
                selectItem(lastItemSelected);
                return;
            default:
                fragment = null;
        }
        lastItemSelected = position;

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

        int id = item.getItemId();

        if (id == R.id.action_update) {
            item.setVisible(false);
            menu.findItem(R.id.action_clean_update).setVisible(false);
            new UpdateService.UpdateTask(this).execute();
            return true;
        }
        else if (id == R.id.action_clean_update) {
            item.setVisible(false);
            menu.findItem(R.id.action_update).setVisible(false);
            GetPreferences(this).edit().putLong("updated-at", 0).apply();
            new UpdateService.UpdateTask(this).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void LogOut() {
        LoginActivity.LogOut(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (logged_out) {
            logged_out = false;
            LogOut();
        }
    }



    public void ShowHashKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.fabb.notifica", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

}
