package com.example.chaya.bontact.Ui.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;
import com.example.chaya.bontact.Socket.io.SocketManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,ServerCallResponseToUi,View.OnKeyListener {

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
        usernameEditText = (EditText) findViewById(R.id.etxt_user_name);
        passEditText = (EditText) findViewById(R.id.etxt_password);
        usernameEditText.setOnKeyListener(this);
        passEditText.setOnKeyListener(this);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_login_loading);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Dialog dialog=new Dialog(this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_login:
                doLogin();
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
        {
            switch (view.getId())
            {
                case R.id.etxt_password:
                    doLogin();
                    break;
            }
            return true;
        }
        return false; // pass on to other listeners.
    }
    public void doLogin()
    {

        String userName = usernameEditText.getText().toString();
        String password = passEditText.getText().toString();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(CheckValidInputs(userName,password)==true)
        {
            progressBar.setVisibility(View.VISIBLE);
            agentDataManager=new AgentDataManager();
            agentDataManager.getDataFromServer(userName,password,this);
        }
    }

    public void SendResponseMessage(String textMsg)
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    public void OnServerCallResponseToUi(boolean isSuccsed, String response, ErrorType errorType, Class sender) {
        if (sender == AgentDataManager.class) {
            if (isSuccsed == true && response != null) {

                if (agentDataManager.saveData(response, this) == true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            SocketManager socketManager= new SocketManager(MainActivity.this);
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
}

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


