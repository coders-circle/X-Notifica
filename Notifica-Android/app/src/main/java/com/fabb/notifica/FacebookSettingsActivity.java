package com.fabb.notifica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class FacebookSettingsActivity extends ActionBarActivity {

    private ArrayList<String> group_ids = new ArrayList<>();
    CallbackManager callbackManager;
    public static boolean both_permissions_asked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fb_settings_content_frame, new FacebookSettingsFragment())
                .commit();

        final LoginButton loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("user_groups", "user_managed_groups"));

        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Ask for both publish and read permissions and then restart the activity to get groups list
                if (!both_permissions_asked) {
                    LoginManager.getInstance().logInWithPublishPermissions(FacebookSettingsActivity.this, Arrays.asList("publish_actions"));
                    both_permissions_asked = true;
                }
                else {
                    finish();
                    startActivity(new Intent(FacebookSettingsActivity.this, FacebookSettingsActivity.class));
                }
            }

            @Override
            public void onCancel() {
                Log.d("Facebook Login", "Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.fb_settings_toolbar);
        setSupportActionBar(toolbar);

        if (AccessToken.getCurrentAccessToken() != null) {
            findViewById(R.id.fb_settings_content_frame).setVisibility(View.VISIBLE);

            // Get a list of facebook groups that the user is member of
            GraphRequest request = new GraphRequest(AccessToken.getCurrentAccessToken(), "me/groups", null,
                    HttpMethod.GET, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    // add each group-name to the list including a "None" value
                    Spinner spinner = (Spinner)findViewById(R.id.spinner_fb_groups);
                    ArrayList<String> group_names = new ArrayList<>();
                    JSONObject json = graphResponse.getJSONObject();

                    JSONArray data = json.optJSONArray("data");
                    group_names.clear(); group_ids.clear();

                    SharedPreferences preferences = MainActivity.GetPreferences(FacebookSettingsActivity.this);
                    String existing_id = preferences.getString("fb-group-id", "");
                    int defaultSelection = 0;

                    group_names.add("None"); group_ids.add("");
                    if (data != null)
                    for (int i=0; i<data.length(); ++i) {
                        JSONObject group = data.optJSONObject(i);
                        if (group == null)
                            continue;

                        group_names.add(group.optString("name"));
                        String id = group.optString("id");
                        group_ids.add(id);

                        // default selection:
                        if (existing_id != null && existing_id.equals(id))
                            defaultSelection = i+1;
                    }
                    ArrayAdapter adapter = new ArrayAdapter<>(FacebookSettingsActivity.this, android.R.layout.simple_spinner_item, group_names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setSelection(defaultSelection);
                }
            });
            request.executeAsync();
        }
        else {
            findViewById(R.id.fb_settings_content_frame).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onFbSettingsChanged(View view) {
        Spinner spinner = (Spinner)findViewById(R.id.spinner_fb_groups);
        int id = spinner.getSelectedItemPosition();
        SharedPreferences preferences = MainActivity.GetPreferences(this);
        preferences.edit().putString("fb-group-id", group_ids.get(id)).apply();

        Toast.makeText(this, "Facebook connection settings saved", Toast.LENGTH_SHORT).show();
    }

    public static class FacebookSettingsFragment extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_facebook_settings, container, false);
        }
    }
}
