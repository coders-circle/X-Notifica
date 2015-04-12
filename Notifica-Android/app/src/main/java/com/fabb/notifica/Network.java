package com.fabb.notifica;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Network {
    private final Context mContext;

    public final String URL = "http://192.168.43.24/Notifica-Web/";
    public final String ERR_CONNECTION = "{ \"message_type\": \"ERROR CONNECTION\" }";

    Network(Context context) {
        mContext = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    public String Get(String address) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(URL + address);
        HttpResponse response;
        String result = "";
        try {
            response = client.execute(request);
            if (response != null) {
                InputStream is = response.getEntity().getContent();
                result = convertStreamToString(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "Error: Cannot Establish Connection", Toast.LENGTH_LONG).show();
            result = ERR_CONNECTION;
        }
        return result;
    }

    public String PostJson(String address, JSONObject jsonObject) {
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;

        String result = "";
        try {
            HttpPost post = new HttpPost(URL + address);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
            post.setEntity(new StringEntity(jsonObject.toString()));
            response = client.execute(post);

            if(response!=null){
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
                result = convertStreamToString(in);
            }
        } catch(Exception e) {

            e.printStackTrace();
            result = ERR_CONNECTION;
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
