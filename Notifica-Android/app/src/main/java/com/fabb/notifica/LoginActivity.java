package com.fabb.notifica;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via user-id/password.
 */
public class LoginActivity extends Activity {
    // Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    public final String LoginUrl = "login";

    // UI references.
    private EditText mUserIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mLoginMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AutoUpdateApk(getApplicationContext());

        SharedPreferences preferences = MainActivity.GetPreferences(this);
        //noinspection ConstantConditions
        if (preferences.getBoolean("logged-in", false)
                && !preferences.getString("user-id", "").equals("")
                && !preferences.getString("password", "").equals("")
                && !preferences.getString("user-type", "").equals("")) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }


        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUserIdView = (EditText) findViewById(R.id.userIdView);

        mPasswordView = (EditText) findViewById(R.id.passwordView);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mLoginMessageView = (TextView)findViewById(R.id.login_message);

        mUserIdView.setText(preferences.getString("user-id", ""));
    }

    /**
     * Attempts to sign in the account specified by the login form.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userId = mUserIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid user id.
        if (TextUtils.isEmpty(userId)) {
            mUserIdView.setError(getString(R.string.error_field_required));
            focusView = mUserIdView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuthTask = new UserLoginTask(this, userId, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private JSONObject GetLoginJson(String userId, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("message_type", "Login Request");
            json.put("user_id", userId);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUserId;
        private final String mPassword;
        private final Context mContext;
        private String failureMessage;
        private JSONObject response;

        private UpdateService.UpdateResult updateResult = new UpdateService.UpdateResult();

        UserLoginTask(Context context, String userId, String password) {
            mUserId = userId;
            mPassword = password;
            mContext = context;
            failureMessage = "";
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Network network = new Network();
                String result = network.PostJson(LoginUrl, GetLoginJson(mUserId, mPassword));
                response = new JSONObject(result);

                if (!response.has("message_type")
                        || !response.optString("message_type").equals("Login Result")
                        || !response.has("login_result")
                        || !response.optString("login_result").equals("Success")) {
                    if (response.has("failure_message"))
                        failureMessage = response.optString("failure_message");
                    else
                        failureMessage = "Couldn't login. Please verify you are connected to internet and try again";
                    return false;
                }
            } catch (Exception e) {
                Log.e("Error Logging", e.getMessage());
                failureMessage = "Couldn't login. Please verify you are connected to internet and try again";
                return false;
            }


            SharedPreferences preferences = MainActivity.GetPreferences(mContext);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("user-id", mUserId);
            editor.putString("password", mPassword);
            editor.putString("user-type", response.optString("user_type"));
            editor.putString("user-name", response.optString("name"));
            if (response.optString("user_type").equals("Student")) {
                editor.putString("faculty-code", response.optString("faculty_code"));
                editor.putInt("batch", response.optInt("batch"));
                editor.putInt("privilege", response.optInt("privilege"));
            }
            editor.putInt("routine-start", 0);
            editor.putInt("routine-end", 0);
            editor.putBoolean("logged-in", true);
            editor.putLong("updated-at", 0);

            editor.putBoolean("gcm_token_sent", false);
            editor.putBoolean("just-logged-in", true);
            editor.apply();
            UpdateService.Update(LoginActivity.this, updateResult);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                UpdateService.FinishUpdate(updateResult);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                switch (failureMessage) {
                    case "Invalid Password":
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        break;
                    case "Invalid User":
                        mUserIdView.setError(getString(R.string.error_invalid_user_id));
                        mUserIdView.requestFocus();
                        break;
                    default:
                        mLoginMessageView.setText(failureMessage);
                        break;
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public static void LogOut(Context context) {
        Database.DeleteAll();

        SharedPreferences preferences = MainActivity.GetPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("logged-in", false);
        editor.remove("password");
        editor.remove("fb-group-id");
        editor.apply();

        LoginManager.getInstance().logOut();
    }
}




