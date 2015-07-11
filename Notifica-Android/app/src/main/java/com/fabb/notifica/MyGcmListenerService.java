package com.fabb.notifica;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONObject;

public class MyGcmListenerService extends  GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        if (!settings.getBoolean("pref_key_push_notify", true))
            return;

        SharedPreferences preferences = MainActivity.GetPreferences(this);
        if (!preferences.getBoolean("logged-in", false))
            return;

        String userType = preferences.getString("user-type", "");
        if (userType != null && userType.equals("Teacher"))
            return;
        
        String message = data.getString("message");
        String title = data.getString("title");
        long id = Long.parseLong(data.getString("remote_id"));
        sendNotification(title, message, id);
    }

    private void sendNotification(String title, String message, long remote_id) {
        new NotificationTask(title, message, remote_id).execute();
    }

    public class NotificationTask extends AsyncTask<Void, Void, Void> {
        private String title;
        private String message;
        private long remote_id;
        private boolean notify = false;

        NotificationTask(String title, String message, long remote_id) {
            this.title = title;
            this.message = message;
            this.remote_id = remote_id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONObject json = new JSONObject();
                json.put("type", title);
                json.put("remote_id", remote_id);
                Network network = new Network();
                JSONObject result = new JSONObject(network.PostJson("check_expired", json));
                notify = !result.optBoolean("expired");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void v) {
            if (notify) {
                Intent intent = new Intent(MyGcmListenerService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("notification-title", title);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyGcmListenerService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyGcmListenerService.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0, notificationBuilder.build());
            }

            new UpdateService.UpdateTask(MyGcmListenerService.this).execute();
        }
    }
}