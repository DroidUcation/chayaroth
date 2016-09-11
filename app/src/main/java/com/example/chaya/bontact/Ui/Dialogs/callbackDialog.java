package com.example.chaya.bontact.Ui.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.R;

/**
 * Created by chaya on 7/25/2016.
 */
public class callbackDialog {

    Context context;
    EditText input;
    TextInputLayout textInputLayout;
    int id_surfer;
    SendResponseHelper sendResponseHelper;
    InnerConversationDataManager innerConversationDataManager;
    AlertDialog alert;
    TextView msgView;

    public callbackDialog(Context context) {
        this.context = context;
        sendResponseHelper = new SendResponseHelper();
    }

    public void create(int id_surfer) {

        input = new EditText(context);
        this.id_surfer = id_surfer;
        innerConversationDataManager = new InnerConversationDataManager(context, id_surfer);
        Agent agent = AgentDataManager.getAgentInstance();
        String phone_number = "";
        if (agent != null && agent.getRep() != null) {
            phone_number = agent.getRep().telephone;
        }
        input.setText(phone_number);
        textInputLayout = new TextInputLayout(context);
        textInputLayout.addView(input);
        alert = new AlertDialog.Builder(context).setTitle("Enter your phone number").setMessage("include your country code")
                .setView(textInputLayout)
                .setPositiveButton("Call", null)
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
            String num = input.getText().toString().trim();
            if (checkValidNumber(num) == true) {
                if (num != null) {
                    num = num.replace("+", "");
                    sendResponseHelper.sendCallBack(context, id_surfer, num);
                }
                alert.dismiss();
            } else {
                textInputLayout.setError(context.getResources().getString(R.string.invalid_callback_phone_number));
            }
        }
    };
    View.OnClickListener negativeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alert.dismiss();
        }
    };

    public boolean checkValidNumber(String phone_number) {
        String numbersValidation = "[+0-9]+";
        if (phone_number.length() > 6 && phone_number.matches(numbersValidation))
            return true;
        return false;
    }
}


