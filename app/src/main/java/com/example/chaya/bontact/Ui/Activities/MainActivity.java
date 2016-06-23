package com.example.chaya.bontact.Ui.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,ServerCallResponse{

    private EditText usernameEditText;
    private  EditText passEditText;
    private ProgressBar progressBar;
    AgentDataManager agentDataManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO:Maybe put this lines when application is starting and also manage accounts...
        //checks if user is logged in
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_login_loading);
        }

    @Override
    public void onClick(View v) {
    switch (v.getId())
    {
        case R.id.btn_login:
                //get inputs
                usernameEditText = (EditText) findViewById(R.id.etxt_user_name);
                passEditText = (EditText) findViewById(R.id.etxt_password);
                String userName = usernameEditText.getText().toString();
                String password = passEditText.getText().toString();
                if(CheckValidInputs(userName,password)==true)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    agentDataManager=new AgentDataManager();
                   agentDataManager.getDataFromServer(userName,password,this);
                }
     }
    }


    public boolean CheckValidInputs(String userName,String password)
    {
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password))//empty details
        {
            reEnterDetails(ErrorType.empty_details);
            return false;
        }
        if(!(Patterns.EMAIL_ADDRESS.matcher(userName).matches()))//invalid details
        {
            reEnterDetails(ErrorType.invalid_details);
            return false;
        }
        //if all details are correct and full
     return true;

    }

    public void reEnterDetails(ErrorType err)
    {
        if(err==null)
            err= ErrorType.other;
        String Error_Str=null;
        switch (err)
        {
            case empty_details:
                Error_Str=getResources().getString(R.string.empty_deatails);
                break;
            case invalid_details:
                Error_Str=getResources().getString(R.string.invalid_deatails);
                break;
            case network_problems:
                Error_Str=getResources().getString(R.string.some_problem);
                break;
            case user_not_exists:
                Error_Str=getResources().getString(R.string.user_not_exists);
                break;
            case other:
                Error_Str=getResources().getString(R.string.some_problem);
                break;
        }
        //todo:1-set matching strings to all errors 2- set a error better viewer

        runOnUiThread(new Runnable() {
            @Override public void run() {
                final TextView errorString=(TextView) findViewById(R.id.error_details);
                progressBar.setVisibility(View.GONE);
                usernameEditText.setText("");
                passEditText.setText("");
               // errorString.setText();
                errorString.setVisibility(View.VISIBLE);

            }
        });
      //  passEditText.addTextChangedListener(textWatcher);
    }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if(s.length()>0)
                //errorString.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


    @Override
    public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType,Class sender) {
      if (sender == AgentDataManager.class) {
            if (isSuccsed == true && response != null) {

                if (agentDataManager.saveData(response, this) == true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(MainActivity.this, MenuActivity.class));
                        }
                    });
                } else {

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reEnterDetails(ErrorType.user_not_exists);
                    }
                });
            }
        }
    }



   /* public void getUserFromServer(String userName,String password)
    {

        String url = getResources().getString(R.string.domain_api) + getResources().getString(R.string.login_api);
        url += "?username=" + userName + "&pass=" + password;
        Callback callback= new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                reEnterDetails(ErrorType.network_problems);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //throw new IOException("Unexpected code " + response);
                    reEnterDetails(ErrorType.other);
                }
                try {
                    final JSONObject jsonObject =new JSONObject(response.body().string());
                    if(jsonObject.getString("message").equals("success"))//user exists
                    {
                        SharedPreferences Preferences = getSharedPreferences(getResources().getString(R.string.sp_user_details), MODE_PRIVATE);
                        SharedPreferences.Editor editor=Preferences.edit();
                        editor.clear();
                        editor.putString(getResources().getString(R.string.agent),jsonObject.getJSONObject("rep").toString());
                        editor.putString(getResources().getString(R.string.settings),jsonObject.getJSONObject("settings").toString());
                        editor.putString(getResources().getString(R.string.token),jsonObject.getString("token"));
                        editor.apply();
                        finish();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent =new Intent(MainActivity.this,MenuActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else
                    {//user not exists
                        runOnUiThread(new Runnable() {
                            @Override public void run() {
                                reEnterDetails(ErrorType.user_not_exists);}
                        });
                    }

                } catch (JSONException e) {
                    reEnterDetails(ErrorType.other);
                }

            }
        };

        OkHttpRequests requests = new OkHttpRequests(url,callback);
        try {
            requests.run();
        } catch (Exception e) {
            // e.printStackTrace();
            reEnterDetails(ErrorType.other);
        }
    }*/}





/*   private void setupFloatingLabelError() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 4) {
                    floatingUsernameLabel.setError(getString(R.string.empty_deatails));
                    floatingUsernameLabel.setErrorEnabled(true);
                } else {
                    floatingUsernameLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


