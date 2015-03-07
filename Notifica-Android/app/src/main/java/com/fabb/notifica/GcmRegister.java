package com.fabb.notifica;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GcmRegister {
    private final MainActivity mainActivity;

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    GoogleCloudMessaging gcm;
    String regid;

    public static void Register(MainActivity main_activity) {
        new GcmRegister(main_activity);
    }

    GcmRegister(MainActivity main_activity) {
        mainActivity = main_activity;

        if (CheckPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(mainActivity);
            regid = getRegistrationId();

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }
        else {
            Toast.makeText(mainActivity, "This device doesn't have google play services.", Toast.LENGTH_LONG).show();
        }

    }

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean CheckPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mainActivity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mainActivity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(mainActivity, "This device is not supported.", Toast.LENGTH_LONG).show();
                mainActivity.finish();
            }
            return false;
        }
        return true;
    }

    private final String TAG = "GCM Client";
    private String getRegistrationId() {
        final SharedPreferences prefs = MainActivity.GetPreferences(mainActivity);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = GetAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void StoreRegistrationId(String regId) {
        final SharedPreferences prefs = MainActivity.GetPreferences(mainActivity);
        int appVersion = GetAppVersion();
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    public int GetAppVersion() {
        try {
            PackageInfo packageInfo = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void SendRegistrationIdToServer() {
        Network network = new Network(mainActivity);
        JSONObject json = new JSONObject();
        try {
            json.put("user_type", "student");
            json.put("faculty", "BCT");
            json.put("year", 2069);
            json.put("roll", 507);
            json.put("reg_id", regid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        network.PostJson("register-gcm.php", json);
    }

    String SENDER_ID = "30928798038";
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mainActivity);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    SendRegistrationIdToServer();
                    StoreRegistrationId(regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, msg);
            }
        }.execute(null, null, null);
    }

}
