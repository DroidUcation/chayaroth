package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, AppCompatCheckBox.OnCheckedChangeListener {
    AppCompatCheckBox enableMessages;
    AppCompatCheckBox soundMessages;
    AppCompatCheckBox vibrateMessages;
    AppCompatCheckBox enableVisitors;
    AppCompatCheckBox soundVisitors;
    AppCompatCheckBox vibrateVisitors;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings_title);
        Agent.Settings.Notification messagesNotification = AgentDataManager.getAgentInstance().getSettings().newMessagesNotifications;
        Agent.Settings.Notification visitorsNotification = AgentDataManager.getAgentInstance().getSettings().newVisitorsNotifications;

        enableMessages = (AppCompatCheckBox) findViewById(R.id.new_message_enable_check);
        enableMessages.setChecked(messagesNotification.isEnabled);
        enableMessages.setOnCheckedChangeListener(this);

        soundMessages = (AppCompatCheckBox) findViewById(R.id.new_message_sound_check);
        soundMessages.setChecked(messagesNotification.sound);
        soundMessages.setEnabled(messagesNotification.isEnabled);
        soundMessages.setOnCheckedChangeListener(this);

        vibrateMessages = (AppCompatCheckBox) findViewById(R.id.new_message_vibrate_check);
        vibrateMessages.setChecked(messagesNotification.vibrate);
        vibrateMessages.setEnabled(messagesNotification.isEnabled);
        vibrateMessages.setOnCheckedChangeListener(this);

        enableVisitors = (AppCompatCheckBox) findViewById(R.id.new_visitor_enable_check);
        enableVisitors.setChecked(visitorsNotification.isEnabled);
        enableVisitors.setOnCheckedChangeListener(this);

        soundVisitors = (AppCompatCheckBox) findViewById(R.id.new_visitor_sound_check);
        soundVisitors.setChecked(visitorsNotification.sound);
        soundVisitors.setEnabled(visitorsNotification.isEnabled);
        soundVisitors.setOnCheckedChangeListener(this);

        vibrateVisitors = (AppCompatCheckBox) findViewById(R.id.new_visitor_vibrate_check);
        vibrateVisitors.setChecked(visitorsNotification.vibrate);
        vibrateVisitors.setEnabled(visitorsNotification.isEnabled);
        vibrateVisitors.setOnCheckedChangeListener(this);

        ImageView logOutBtn = (ImageView) findViewById(R.id.disconnect_account_btn);
        logOutBtn.setOnClickListener(this);

        TextView about_us= (TextView) findViewById(R.id.about_us_txt);
        about_us.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.disconnect_account_btn:
                logOut();
                break;
            case R.id.about_us_txt:
                Intent i= new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.bontact.com"));
                startActivity(i);
                break;
        }
    }

    private void logOut() {
        if (AgentDataManager.logOut(this)) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void updateSettings(boolean updateInServer) {
        final Agent.Settings settings = AgentDataManager.getAgentInstance().getSettings();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                SharedPreferences Preferences = getSharedPreferences(getResources().getString(R.string.sp_user_details), MODE_PRIVATE);
                SharedPreferences.Editor editor = Preferences.edit();
                editor.putString(getResources().getString(R.string.settings), gson.toJson(settings));
                editor.apply();
            }
        });

        if (updateInServer) {
            SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.gcm_pref), MODE_PRIVATE);
            String gcmToken = sharedPreferences.getString(getResources().getString(R.string.gcm_token), null);

            if (gcmToken != null && settings.newVisitorsNotifications != null && settings.newMessagesNotifications != null) {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority(getResources().getString(R.string.base_dev_api))
                        .appendPath(getResources().getString(R.string.rout_api))
                        .appendPath(getResources().getString(R.string.update_settings_api))
                        .appendPath(AgentDataManager.getAgentInstance().getToken())
                        .appendQueryParameter("visitorpush", AgentDataManager.getNewVisitorsNotificationSettings().isEnabled + "")
                        .appendQueryParameter("messagepush", AgentDataManager.getNewMessagesNotificationSettings().isEnabled + "")
                        .appendQueryParameter("tokendevice", gcmToken);

                String url = builder.build().toString();
                OkHttpRequests okHttpRequests = new OkHttpRequests(url, new ServerCallResponse() {
                    @Override
                    public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SettingsActivity.this, "your settings are saved", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        } else
            Toast.makeText(SettingsActivity.this, "your settings are saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean val) {
        switch (compoundButton.getId()) {
            case R.id.new_message_enable_check:
                AgentDataManager.getAgentInstance().getSettings().newMessagesNotifications.isEnabled = val;
                updateSettings(true);
                soundMessages.setEnabled(val);
                vibrateMessages.setEnabled(val);
                break;
            case R.id.new_message_sound_check:
                AgentDataManager.getAgentInstance().getSettings().newMessagesNotifications.sound = val;
                updateSettings(false);
                break;
            case R.id.new_message_vibrate_check:
                AgentDataManager.getAgentInstance().getSettings().newMessagesNotifications.vibrate = val;
                updateSettings(false);
                break;
            case R.id.new_visitor_enable_check:
                AgentDataManager.getAgentInstance().getSettings().newVisitorsNotifications.isEnabled = val;
                updateSettings(true);
                soundVisitors.setEnabled(val);
                vibrateVisitors.setEnabled(val);
                break;
            case R.id.new_visitor_sound_check:
                AgentDataManager.getAgentInstance().getSettings().newVisitorsNotifications.sound = val;
                updateSettings(false);
                break;
            case R.id.new_visitor_vibrate_check:
                AgentDataManager.getAgentInstance().getSettings().newVisitorsNotifications.vibrate = val;
                updateSettings(false);
                break;

        }
    }
}
