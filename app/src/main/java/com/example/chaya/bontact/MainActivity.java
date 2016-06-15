package com.example.chaya.bontact;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.chaya.bontact.ServerCalls.OkHttpRequests;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEditText;
    private  EditText passEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        //setupFloatingLabelError();

        }

    @Override
    public void onClick(View v) {

               switch (v.getId()) {
            case R.id.btn_login:

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
                    //not valid - print invalid msgs
                     if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password))
                        Toast.makeText(MainActivity.this, R.string.empty_deatails, Toast.LENGTH_SHORT).show();
                     else
                         Toast.makeText(MainActivity.this, R.string.invalid_deatails, Toast.LENGTH_SHORT).show();
             reEnterDetails();
                }
        }
    }


    public void getUserFromServer(String userName,String password)
    {

        String url = getResources().getString(R.string.domain_api) + getResources().getString(R.string.login_api);
        url += "?username=" + userName + "&pass=" + password;

        Callback callback= new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("fail", "onFailure: ");

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
                try {
                    final JSONObject jsonObject =new JSONObject(response.body().string());
                    Log.d("object",jsonObject.toString());
                    Log.d("msg", jsonObject.getString("message"));

                   if(jsonObject.getString("message").equals("success"))
                    {
                      SharedPreferences Preferences = getSharedPreferences("UserDeatails", MODE_PRIVATE);
                      SharedPreferences.Editor editor=Preferences.edit();
                      editor.putString(getResources().getString(R.string.agent),jsonObject.getJSONObject("rep").toString());
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
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, R.string.user_not_exist, Toast.LENGTH_SHORT).show();
                              reEnterDetails();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        OkHttpRequests requests = new OkHttpRequests(url,callback);

        try {
            requests.run();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void reEnterDetails()
    {
        usernameEditText.setText("");
        passEditText.setText("");
       // usernameEditText.setBackgroundColor(getResources().getColor(R.color.colorREquierd));


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


