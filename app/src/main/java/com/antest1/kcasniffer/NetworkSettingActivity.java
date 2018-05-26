package com.antest1.kcasniffer;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class NetworkSettingActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socks5);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.menu_socks5_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, new PrefsFragment()).commit();
    }


    public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public NetworkSettingActivity activity;
        public Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName("pref");
            addPreferencesFromResource(R.xml.pref_settings);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            Map<String, ?> allEntries = getPreferenceManager().getSharedPreferences().getAll();
            for (String key : allEntries.keySet()) {
                Preference pref = findPreference(key);
                if (pref instanceof ListPreference) {
                    ListPreference etp = (ListPreference) pref;
                    pref.setSummary(etp.getEntry());
                } else if (pref instanceof EditTextPreference) {
                    EditTextPreference etp = (EditTextPreference) pref;
                    if (key.equals("socks5_pass")) {
                        EditText et = etp.getEditText();
                        String value = et.getTransformationMethod().getTransformation(etp.getText(), et).toString();
                        pref.setSummary(value);
                    } else {
                        pref.setSummary(etp.getText());
                    }
                }
            }
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            if (pref instanceof ListPreference) {
                ListPreference etp = (ListPreference) pref;
                pref.setSummary(etp.getEntry());
            } else if (pref instanceof EditTextPreference) {
                EditTextPreference etp = (EditTextPreference) pref;
                if (key.equals("socks5_pass")) {
                    EditText et = etp.getEditText();
                    String value = et.getTransformationMethod().getTransformation(etp.getText(), et).toString();
                    pref.setSummary(value);
                } else {
                    pref.setSummary(etp.getText());
                }
            }
        }
    }
}
