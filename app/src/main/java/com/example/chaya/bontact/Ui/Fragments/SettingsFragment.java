package com.example.chaya.bontact.Ui.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Activities.SplashActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);


    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Preference logout = (Preference) findPreference(getString(R.string.disconnect_account_key));
        logout.setOnPreferenceClickListener(this);
        SwitchPreferenceCompat new_msg_push = (SwitchPreferenceCompat) findPreference(getResources().getString(R.string.new_message_push_key));
        new_msg_push.setChecked(AgentDataManager.getMsgPushNotification());
        SwitchPreferenceCompat new_visitor_push = (SwitchPreferenceCompat) findPreference(getResources().getString(R.string.new_visitor_push_key));
        new_visitor_push.setChecked(AgentDataManager.getVisitorPushNotification());
        return view;
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
        AgentDataManager.getAgentInstance().getSettings().visitorPushNotification = preferences.
                getBoolean(getResources().getString(R.string.new_visitor_push_key), false);
        AgentDataManager.getAgentInstance().getSettings().msgPushNotification = preferences.
                getBoolean(getResources().getString(R.string.new_message_push_key), true);

        /*var url = bontactServers.api + 'updatePushNotification/' + agent.TokenAgent()+
                '?visitorpush='+this.settings.push_notification.visitor+'&messagepush='+this.settings.push_notification.message;*/

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getResources().getString(R.string.base_api))
                .appendPath(getResources().getString(R.string.rout_api))
                .appendPath(getResources().getString(R.string.update_settings_api))
                .appendPath(AgentDataManager.getAgentInstance().getToken())
                .appendQueryParameter("visitorpush", AgentDataManager.getAgentInstance().getSettings().visitorPushNotification + "")
                .appendQueryParameter("messagepush", AgentDataManager.getAgentInstance().getSettings().msgPushNotification + "");

        String url = builder.build().toString();
        OkHttpRequests okHttpRequests = new OkHttpRequests(url, new ServerCallResponse() {
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getResources().getString(R.string.disconnect_account_key)))
            if (AgentDataManager.logOut(getContext())) {
                Intent intent = new Intent(getContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        return true;
    }
}