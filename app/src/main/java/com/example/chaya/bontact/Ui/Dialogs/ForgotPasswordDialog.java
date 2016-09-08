package com.example.chaya.bontact.Ui.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.NetworkCalls.OkHttpRequests;
import com.example.chaya.bontact.NetworkCalls.ServerCallResponse;
import com.example.chaya.bontact.R;

/**
 * Created by chaya on 9/1/2016.
 */
public class ForgotPasswordDialog {

    AlertDialog alert;
    Context context;
    EditText input;
    TextInputLayout textInputLayout;
    ServerCallResponse callback;

    public ForgotPasswordDialog(Context context) {
        this.context = context;

    }

    public void create(ServerCallResponse callback) {
        this.callback = callback;
        input = new EditText(context);
        textInputLayout = new TextInputLayout(context);
        textInputLayout.addView(input);
        alert = new AlertDialog.Builder(context).setTitle(R.string.forgot_pass_title)
                .setMessage(R.string.forgot_pass_sub_title)
                .setView(textInputLayout)
                .setPositiveButton("Send", null)
                .setNegativeButton("Cancel", null).create();
        alert.setOnShowListener(onShowListener);
        alert.show();
        // alert.setIcon(R.mipmap.bontact_launcher);
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(context.getResources().getColor(R.color.purple));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(context.getResources().getColor(R.color.purple));
        pbutton.setTextColor(context.getResources().getColor(R.color.white));

    }

    DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() {

        @Override
        public void onShow(DialogInterface dialog) {
            Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(positiveListener);
            b = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
            b.setOnClickListener(negativeListener);
        }
    };
    View.OnClickListener positiveListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = input.getText().toString();
            if (!email.equals("") && email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                AgentDataManager.forgotPassword(context, email, callback);
                alert.dismiss();
              //  callback.OnServerCallResponse(true, "true", null);
            } else {
                textInputLayout.setError(context.getString(R.string.forgot_pass_error_message));
            }

        }
    };
    View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alert.dismiss();
        }
    };

}



