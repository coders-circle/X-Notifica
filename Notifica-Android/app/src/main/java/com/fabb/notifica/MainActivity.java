package com.fabb.notifica;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import java.util.Iterator;


public class MainActivity extends ActionBarActivity implements UpdateListener {

    public static boolean logged_out = false;

    private DrawerLayout mDrawerLayout;
    private RecyclerView drawerRecyclerView;
    private String[] mPageTitles;
    private DrawerAdapter drawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    public Menu menu;
    private String name, roll;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (drawerAdapter != null)
            outState.putInt("fragment-id" , drawerAdapter.GetSelected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = GetPreferences(this);
        name = preferences.getString("user-name", "Unknown");
        roll = preferences.getString("user-id", "ukn");

        FacebookSdk.sdkInitialize(getApplicationContext());

        Database.DeleteExpired();

        mPageTitles = getResources().getStringArray(R.array.page_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerRecyclerView = (RecyclerView)findViewById(R.id.drawer_recyclerView);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,
                toolbar,               /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        );


        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set the drawerAdapter for the list view
        UpdateDrawer();

        setSupportActionBar(toolbar);
        toolbar.hideOverflowMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        int fragment_id;
        if (savedInstanceState != null)
            fragment_id = savedInstanceState.getInt("fragment-id", 0);
        else {
            String notification_title = getIntent().getStringExtra("notification-title");
            if (notification_title == null)
                fragment_id = 0;
            else if (notification_title.equals("Assignment"))
                fragment_id = 1;
            else
                fragment_id = 2;
        }

        selectItem(fragment_id);

        UpdateService.AddUpdateListener(this);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (settings.getBoolean("just-logged-in", true) || settings.getBoolean("pref_key_auto_update", true)) {
            new UpdateService.UpdateTask(this).execute();
            settings.edit().putBoolean("just-logged-in", false).apply();
        }

        String token = preferences.getString("gcm_token", "");
        if (token == null || token.equals("")) {
            Intent intent = new Intent(this, GcmRegisterIntent.class);
            startService(intent);
        }

        token = preferences.getString("gcm_token", "");
        if (token != null && !token.equals(""))
        if (!GetPreferences(this).getBoolean("gcm_token_sent", false))
            GcmRegisterIntent.sendRegistrationToServer(this,  preferences.getString("gcm_token", ""));

        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        drawerRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    int position = recyclerView.getChildPosition(child) - 1;
                    if (position >= 0)
                        selectItem(position);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });
    }

    private int[] icons={
            R.mipmap.ic_routine,
            R.mipmap.ic_assignments,
            R.mipmap.ic_notice,
            R.mipmap.ic_settings
    };

    public void UpdateDrawer() {
        int new_assignment_cnt = 0;
        int new_notice_cnt = 0;

        int selected = 0;
        if (drawerAdapter != null)
            selected = drawerAdapter.GetSelected();

        Iterator<Notice> noticeIterator = Notice.findAll(Notice.class);
        while (noticeIterator.hasNext()) {
            if (!noticeIterator.next().seen)
                new_notice_cnt++;
        }

        Iterator<Assignment> assignmentIterator = Assignment.findAll(Assignment.class);
        while (assignmentIterator.hasNext()) {
            if (!assignmentIterator.next().seen)
                new_assignment_cnt++;
        }

        drawerAdapter = new DrawerAdapter(mPageTitles, icons, name, roll);
        drawerAdapter.setCount(1, new_assignment_cnt);
        drawerAdapter.setCount(2, new_notice_cnt);
        drawerRecyclerView.setAdapter(drawerAdapter);

        drawerAdapter.SetSelected(selected);
    }

    @Override
    public void OnUpdateComplete(boolean hasUpdated, int eventCnt, int assignmentCnt) {
//        if (menu != null) {
//            menu.findItem(R.id.action_update).setVisible(true);
//            menu.findItem(R.id.action_clean_update).setVisible(true);
//        }

        if (hasUpdated) {
            //Toast.makeText(this, "Up-To-Date", Toast.LENGTH_LONG).show();
            UpdateDrawer();
        }
        else
            Toast.makeText(this, "Couldn't update.\nCheck your internet connection.", Toast.LENGTH_LONG).show();

        Database.DeleteExpired();
    }

    private Fragment routine_fragment = new RoutineFragment();
    private Fragment assignment_fragment = new AssignmentFragment();
    private Fragment event_fragment = new NoticeFragment();
    private int lastItemSelected = 0;
    /** Swaps fragments in the main content view */
    private void selectItem(final int position) {
        final Fragment fragment;
        switch (position) {
            case 0:
                fragment = routine_fragment;
                break;
            case 1:
                fragment = assignment_fragment;
                break;
            case 2:
                fragment = event_fragment;
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
                    .commitAllowingStateLoss();
        }
        setTitle(mPageTitles[position]);
        drawerAdapter.SetSelected(position);
        drawerAdapter.notifyDataSetChanged();
        mDrawerLayout.closeDrawers();

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



//    public void ShowHashKey() {
//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo("com.fabb.notifica", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                Log.d("hash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }
//    }

}
