package com.example.chaya.bontact.Ui.Fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
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

        if (s.equals(getResources().getString(R.string.new_visitor_push_key)) || s.equals(getResources().getString(R.string.new_message_push_key)))
            updateSettings(s, sharedPreferences);
    }

    public void updateSettings(String key, SharedPreferences preferences) {
        AgentDataManager.getAgentInstanse().getSettings().visitorPushNotification = preferences.
                getBoolean(getResources().getString(R.string.new_visitor_push_key), false);
        AgentDataManager.getAgentInstanse().getSettings().msgPushNotification = preferences.
                getBoolean(getResources().getString(R.string.new_message_push_key), true);

        /*var url = bontactServers.api + 'updatePushNotification/' + agent.TokenAgent()+
                '?visitorpush='+this.settings.push_notification.visitor+'&messagepush='+this.settings.push_notification.message;*/

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getResources().getString(R.string.base_api))
                .appendPath(getResources().getString(R.string.rout_api))
                .appendPath(getResources().getString(R.string.update_settings_api))
                .appendPath(AgentDataManager.getAgentInstanse().getToken())
                .appendQueryParameter("visitorpush",AgentDataManager.getAgentInstanse().getSettings().visitorPushNotification+"")
                .appendQueryParameter("messagepush",AgentDataManager.getAgentInstanse().getSettings().msgPushNotification+"");

        String url = builder.build().toString();
        OkHttpRequests okHttpRequests= new OkHttpRequests(url, new ServerCallResponse() {
            @Override
            public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
               getActivity().runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(getContext(), "your settings are saved", Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });

    }

}