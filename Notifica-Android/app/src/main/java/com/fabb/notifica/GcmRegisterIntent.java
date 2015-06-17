package com.fabb.notifica;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

public class GcmRegisterIntent extends IntentService {

    private static final String TAG = "GcmRegIntentService";
    private static final String registerUrl = "register";

    public GcmRegisterIntent() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = MainActivity.GetPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                sendRegistrationToServer(this, token);
                sharedPreferences.edit().putString("gcm_token", token).apply();
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putString("gcm_token", "").apply();
            sharedPreferences.edit().putBoolean("gcm_token_sent", false).apply();
        }
    }

    public static void sendRegistrationToServer(Context context, String token) {
        JSONObject json = new JSONObject();
        SharedPreferences preferences = MainActivity.GetPreferences(context);

        try {
            json.put("message_type", "Gcm Registration");
            json.put("user_id", preferences.getString("user-id",""));
            json.put("password", preferences.getString("password", ""));
            json.put("token", token);

            new GcmRegisterTask(context, json, true).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class GcmRegisterTask extends AsyncTask<Void, Void, Void> {
        JSONObject mJson;
        String result="";
        boolean success=false;
        JSONObject mReturn;
        Context mContext;

        GcmRegisterTask(Context context, JSONObject json, boolean shutdown) {
            mJson=json;
            mContext = context;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                success = false;
                Network network = new Network(mContext);
                result = network.PostJson(registerUrl, mJson);
                mReturn = new JSONObject(result);
                if (mReturn.optString("register_result").equals("Success"))
                    success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            Log.d("Gcm Registration Result", success?"Success":"Failure");

            SharedPreferences.Editor editor = MainActivity.GetPreferences(mContext).edit();
            if (success)
                editor.putBoolean("gcm_token_sent", true);
            else
                editor.putBoolean("gcm_token_sent", false);
            editor.apply();
        }
    }
}