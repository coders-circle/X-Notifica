package com.fabb.notifica;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar)findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content_frame, new SettingsFragment())
                .commit();

    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            findPreference("pref_key_logout")
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            MainActivity.logged_out = true;
                            getActivity().finish();
                            return true;
                        }
                    });

            // We don't display fb_connection_settings for un-privileged users
            SharedPreferences preferences = MainActivity.GetPreferences(getActivity());
            String user_type = preferences.getString("user-type", "");
            if ((user_type == null || !user_type.equals("Teacher")) && preferences.getInt("privilege", 0) == 0) {
                ((PreferenceCategory)findPreference("pref_key_misc"))
                        .removePreference(findPreference("pref_key_fb"));
            }
        }
    }
}
