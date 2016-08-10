package com.example.chaya.bontact.Ui.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.Helpers.ErrorType;
import com.example.chaya.bontact.Helpers.InitData;
import com.example.chaya.bontact.Helpers.NetworkCheckConnection;
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.R;
/*import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;*/
import com.example.chaya.bontact.Services.RegisterGcmService;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private EditText usernameEditText;
    private EditText passEditText;
    private ProgressBar progressBar;
    AgentDataManager agentDataManager;
    Button btn_login;
    TextInputLayout userNameInputLayout;
    TextInputLayout passwordInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        usernameEditText = (EditText) findViewById(R.id.etxt_user_name);
        passEditText = (EditText) findViewById(R.id.etxt_password);
        usernameEditText.setOnKeyListener(this);
        passEditText.setOnKeyListener(this);
        TextView icon = (TextView) findViewById(R.id.icon_username);
        icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        icon = (TextView) findViewById(R.id.icon_password);
        icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_login_loading);
        userNameInputLayout = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.password_text_input_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            switch (view.getId()) {
                case R.id.etxt_password:
                    doLogin();
                    break;
            }
            return true;
        }
        return false; // pass on to other listeners.
    }

    public void doLogin() {

        String userName = usernameEditText.getText().toString();
        String password = passEditText.getText().toString();

        //close keybosrd
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (CheckValidInputs(userName, password) == true) {
            progressBar.setVisibility(View.VISIBLE);
            agentDataManager = new AgentDataManager();
            agentDataManager.getDataFromServer(userName, password, this);
        }
    }

    public void SendResponseMessage(String textMsg) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean CheckValidInputs(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            reEnterDetails(ErrorType.empty_user_name);
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            reEnterDetails(ErrorType.empty_password);
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(userName).matches()))//invalid details
        {
            reEnterDetails(ErrorType.invalid_user_name);
            return false;
        }
        //if all details are correct and full
        return true;

    }

    public void reEnterDetails(ErrorType err) {
        if (userNameInputLayout == null)
            userNameInputLayout = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        progressBar.setVisibility(View.GONE);
        usernameEditText.setText("");
        passEditText.setText("");
        if (err == null)
            err = ErrorType.other;
        switch (err) {
            case empty_user_name:
                userNameInputLayout.setError(getResources().getString(R.string.empty_user_name));
                break;
            case empty_password:
                userNameInputLayout.setError(getResources().getString(R.string.empty_password));
                break;
            case invalid_user_name:
                userNameInputLayout.setError(getResources().getString(R.string.invalid_deatails));
                passwordInputLayout.setError(getResources().getString(R.string.invalid_deatails));
                break;
            case network_problems:
                if (!NetworkCheckConnection.isConnected(this))
                    Toast.makeText(MainActivity.this, R.string.network_problem, Toast.LENGTH_SHORT).show();
                /*userNameInputLayout.setError(getResources().getString(R.string.some_problem));
                passwordInputLayout.setError(getResources().getString(R.string.some_problem));*/
                break;
            case user_not_exists:
                userNameInputLayout.setError(getResources().getString(R.string.user_not_exists));
                passwordInputLayout.setError(getResources().getString(R.string.user_not_exists));
                break;
            case other:
                userNameInputLayout.setError(getResources().getString(R.string.some_problem));
                passwordInputLayout.setError(getResources().getString(R.string.some_problem));
                break;
        }
        //todo:1-set matching strings to all errors

      /*  runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                usernameEditText.setText("");
                passEditText.setText("");

            }
        });*/
    }


    private void setupFloatingLabelError() {

        if (userNameInputLayout == null)
            userNameInputLayout = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        if (passwordInputLayout == null)
            passwordInputLayout = (TextInputLayout) findViewById(R.id.password_text_input_layout);
        userNameInputLayout.getEditText().addTextChangedListener(inputLayoutTextWatcher);
        passwordInputLayout.getEditText().addTextChangedListener(inputLayoutTextWatcher);

    }

    TextWatcher inputLayoutTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int count, int after) {
            passwordInputLayout.setError(null);
            userNameInputLayout.setError(null);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            userNameInputLayout.setError("");
        }
    };

    public class LoginResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isSuccsed = intent.getBooleanExtra(context.getResources().getString(R.string.is_successed_key), false);
            String response = intent.getStringExtra(context.getResources().getString(R.string.response_key));
                if (isSuccsed == true && response != null) {
                    if (agentDataManager.saveData(response, MainActivity.this) == true) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                InitData initData= new InitData();
                                initData.start(MainActivity.this);
                                startActivity(new Intent(MainActivity.this, MenuActivity.class));
                            }
                        });
                    } else {}
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


    /*@Override
    public void OnServerCallResponseToUi(boolean isSuccsed, String response, ErrorType errorType, Class sender) {
        if (sender == AgentDataManager.class) {
            if (isSuccsed == true && response != null) {

                if (agentDataManager.saveData(response, this) == true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            InitData initData = new InitData();
                            initData.start(MainActivity.this);
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
    }*/


}


