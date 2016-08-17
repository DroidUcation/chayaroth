package com.example.chaya.bontact.Ui.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.ChanelsTypes;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.R;

/**
 * Created by chaya on 7/25/2016.
 */
public class DialogInput {

    AlertDialog.Builder alertBuilder;
    AlertDialog alert;
    Context context;
    EditText input;
    int id_surfer;
    SendResponseHelper sendResponseHelper;
    InnerConversationDataManager innerConversationDataManager;

    public DialogInput(Context context) {
        this.context = context;
        sendResponseHelper = new SendResponseHelper();
    }

    public void create(int id_surfer, int channel) {

        alertBuilder = new AlertDialog.Builder(context);
        input = new EditText(context);
        this.id_surfer = id_surfer;
        innerConversationDataManager = new InnerConversationDataManager(context, id_surfer);

        if (channel == ChanelsTypes.callback) {
            Agent agent = AgentDataManager.getAgentInstanse();
            String phone_number = "";
            if (agent != null && agent.getRep() != null) {
                phone_number = agent.getRep().telephone;
            }
            input.setText(phone_number);
            alertBuilder.setTitle("Enter your phone number").setMessage("include your country code")
                    .setView(input)
                    .setPositiveButton("Call", callbackOnClickListener)
                    .setNegativeButton("Cancel", callbackOnClickListener);
        } else if (channel == ChanelsTypes.sms) {
            alertBuilder.setTitle("please enter your msg")
                    .setView(input)
                    .setPositiveButton("Send", smsOnClickListener)
                    .setNegativeButton("Cancel", smsOnClickListener);
        } /*else if (channel == ChanelsTypes.email) {
            alertBuilder.setMessage("please enter your msg")
                    .setView(input)
                    .setPositiveButton("Yes", emailOnClickListener)
                    .setNegativeButton("No", emailOnClickListener);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            alert = alertBuilder.create();
            alert.show();
            Window window = alert.getWindow();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            return;
        }*/
        show();
    }


    public boolean checkValidNumber(String phone_number) {
        String numbersValidation = "[0-9]+";
        if (phone_number.length() > 6 && phone_number.matches(numbersValidation))
            return true;
        return false;
    }

    public void show() {
        if (alertBuilder == null)
            return;

        alert = alertBuilder.create();
        alert.show();

        // alert.setIcon(R.mipmap.bontact_launcher);
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(context.getResources().getColor(R.color.purple));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(context.getResources().getColor(R.color.purple));
        pbutton.setTextColor(context.getResources().getColor(R.color.white));
    }

    DialogInterface.OnClickListener callbackOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (checkValidNumber(input.getText().toString()) == true) {
                        sendResponseHelper.sendCallBack(context, id_surfer, input.getText().toString());
                    } else {
                        alertBuilder.setTitle("not valid phone number");
                        input.setText("");
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };
    DialogInterface.OnClickListener smsOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (!input.getText().toString().equals("") && input.getText().toString() != null) {
                        sendResponseHelper.sendSms(context, input.getText().toString(), id_surfer);
                        //  innerConversationDataManager.addTextMsgToList(ChanelsTypes.sms, input.getText().toString(), false);
                    } else
                        dialog.dismiss();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

}

