package com.example.chaya.bontact.Ui.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getActivity().setTitle(R.string.settings_title);

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        SwitchPreferenceCompat new_msg_push = (SwitchPreferenceCompat) findPreference(getResources().getString(R.string.new_message_push_key));
        if (new_msg_push != null) {
            new_msg_push.setChecked(true);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        boolean x = sharedPreferences.getBoolean(s, false);
        Toast.makeText(getContext(), "CHANGE " + s + " to " + x, Toast.LENGTH_SHORT).show();
        updateSettings(s, sharedPreferences);
    }

    public void updateSettings(String key, SharedPreferences preferences) {
        boolean x = preferences.getBoolean(key, false);
        //Toast.makeText(getContext(), preferences.getAll().toString(), Toast.LENGTH_LONG).show();
        Log.d("pref",preferences.getAll().toString());
        preferences.getAll().toString();

    }

}