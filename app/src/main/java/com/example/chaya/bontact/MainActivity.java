package com.example.chaya.bontact;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.example.chaya.bontact.ServerCalls.OkHttpRequests;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEditText;
    private  EditText passEditText;
    private  ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TODO:Maybe put this lines when application is starting and also manage accounts...
        //checks if user is logged in
      /*  SharedPreferences Preferences = getSharedPreferences(getResources().getString(R.string.sp_user_details), MODE_PRIVATE);
       if( Preferences.contains(getResources().getString(R.string.token)))//user is logged in
        {
            Intent intent =new Intent(MainActivity.this,MenuActivity.class);
            startActivity(intent);
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
         pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

     //Todo:style the progress dialog
        }

    @Override
    public void onClick(View v) {
    switch (v.getId())
    { case R.id.btn_login:
                //get inputs
                usernameEditText = (EditText) findViewById(R.id.etxt_user_name);
                passEditText = (EditText) findViewById(R.id.etxt_password);
                String userName = usernameEditText.getText().toString();
                String password = passEditText.getText().toString();

                //validation
                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password) && Patterns.EMAIL_ADDRESS.matcher(userName).matches())
                {
                    //valid input
                    getUserFromServer(userName,password);
                }
                else
                {
                    //not valid inputs - print invalid msgs
                     if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password))
                         reEnterDetails(R.string.empty_deatails);
                     else
                         reEnterDetails(R.string.invalid_deatails);
                }
     }
    }

    public void getUserFromServer(String userName,String password)
    {
        pd.show();
        String url = getResources().getString(R.string.domain_api) + getResources().getString(R.string.login_api);
        url += "?username=" + userName + "&pass=" + password;
        Callback callback= new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               reEnterDetails(R.string.connection_problem);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    //throw new IOException("Unexpected code " + response);
                    reEnterDetails(R.string.some_problem);
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
                               reEnterDetails(R.string.user_not_exists);}
                        });
                    }

                } catch (JSONException e) {
                    reEnterDetails(R.string.some_problem);
                }
                pd.dismiss();
            }
        };

        OkHttpRequests requests = new OkHttpRequests(url,callback);
        try {
            requests.run();
        } catch (Exception e) {
           // e.printStackTrace();
            reEnterDetails(R.string.some_problem);
        }
    }

    public void reEnterDetails(int string_err)
    {
        usernameEditText.setText("");
        passEditText.setText("");
         final TextView errorString=(TextView) findViewById(R.id.error_details);
        errorString.setText(getResources().getString(string_err));
        errorString.setVisibility(View.VISIBLE);
        passEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0)
                    errorString.setVisibility(View.GONE);
                           }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
}


