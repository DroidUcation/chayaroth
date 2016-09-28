package com.example.chaya.bontact.Ui.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.chaya.bontact.Helpers.SpecialFontsHelper;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;
import com.example.chaya.bontact.Ui.Dialogs.ForgotPasswordDialog;
/*import com.example.chaya.bontact.NetworkCalls.ServerCallResponseToUi;*/

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private EditText usernameEditText;
    private EditText passEditText;
    private ProgressBar progressBar;
    AgentDataManager agentDataManager;
    Button btn_login;
    TextInputLayout userNameInputLayout;
    TextInputLayout passwordInputLayout;
    TextView errorMsg;
    LoginResponseReceiver broadcastReceiver;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_title);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        usernameEditText = (EditText) findViewById(R.id.username_edittext);
        usernameEditText.setOnKeyListener(this);
        usernameEditText.addTextChangedListener(inputsTextWatcher);
        passEditText = (EditText) findViewById(R.id.password_edittext);
        passEditText.setOnKeyListener(this);
        passEditText.addTextChangedListener(inputsTextWatcher);
        TextView icon = (TextView) findViewById(R.id.icon_username);
        icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        icon = (TextView) findViewById(R.id.icon_password);
        icon.setTypeface(SpecialFontsHelper.getFont(this, R.string.font_awesome));
        progressBar = (ProgressBar) findViewById(R.id.progressbar_login);
        userNameInputLayout = (TextInputLayout) findViewById(R.id.username_input_layout);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.password_input_layout);
        errorMsg = (TextView) findViewById(R.id.error_msg);

        // errorMsg.setText(R.string.invalid_login_details);
        forgotPassword = (TextView) findViewById(R.id.forgot_pass);
        forgotPassword.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = IntentFilter.create(getResources().getString(R.string.action_login_completed), "*/*");
        broadcastReceiver = new LoginResponseReceiver();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.forgot_pass:
                ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(this);
                forgotPasswordDialog.create(new ServerCallResponse() {
                    @Override
                    public void OnServerCallResponse(boolean isSuccsed, String response, ErrorType errorType) {
                        final String msg;
                        if (response != null && response.toLowerCase().equals("true"))
                            msg = getString(R.string.forgot_pass_response_success_message);
                        else
                            msg = getString(R.string.forgot_pass_response_failure_message);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            switch (view.getId()) {
                case R.id.password_edittext:
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

        ErrorType err = null;
        if (TextUtils.isEmpty(userName)) {
            err = ErrorType.empty_user_name;
            reEnterDetails(err);
        }
        if (TextUtils.isEmpty(password)) {
            err = ErrorType.empty_password;
            reEnterDetails(err);
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(userName).matches()) && err == null)//invalid email and all fields are not empty
        {
            err = ErrorType.invalid_details;
            reEnterDetails(err);
        }
        if (err != null) {
            return false;
        }
        //if all details are correct and full
        return true;

    }

    public void reEnterDetails(ErrorType err) {
        passEditText.setText("");
        progressBar.setVisibility(View.GONE);
        if (err == null)
            err = ErrorType.other;
        switch (err) {
            case empty_user_name:
                userNameInputLayout.setError(getResources().getString(R.string.empty_user_name));
                break;
            case empty_password:
                passwordInputLayout.setError(getResources().getString(R.string.empty_password));
                break;
            case invalid_details:
                errorMsg.setVisibility(View.VISIBLE);
                break;
        /*    case network_problems:
                if (!NetworkCheckConnection.isConnected(this))
                    Toast.makeText(.this, R.string.network_problem, Toast.LENGTH_SHORT).show();
                userNameInputLayout.setError(getResources().getString(R.string.some_problem));
                passwordInputLayout.setError(getResources().getString(R.string.some_problem));
                break;
            case other:
                userNameInputLayout.setError(getResources().getString(R.string.some_problem));
                passwordInputLayout.setError(getResources().getString(R.string.some_problem));
                break;*/
        }
      /*  runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                usernameEditText.setText("");
                passEditText.setText("");

            }
        });*/
    }


    TextWatcher inputsTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int count, int after) {
            if (usernameEditText.getText().length() > 0)
                userNameInputLayout.setError(null);
            if (passEditText.getText().length() > 0)
                passwordInputLayout.setError(null);
            errorMsg.setVisibility(View.GONE);

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
                if (agentDataManager.saveData(response, LoginActivity.this) == true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                        }
                    });
                } else {
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reEnterDetails(ErrorType.invalid_details);
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
                            initData.start(.this);
                            startActivity(new Intent(.this, MenuActivity.class));
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


