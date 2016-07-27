package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.example.chaya.bontact.DataManagers.AgentDataManager;
import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Models.Agent;
import com.example.chaya.bontact.Models.Conversation;
import com.example.chaya.bontact.Ui.Activities.InnerConversationActivity;

/**
 * Created by chaya on 7/25/2016.
 */
public class AlertCallbackResponse {

    AlertDialog.Builder alertBuilder = null;
    DialogInterface.OnClickListener dialogClickListener;
    Context context;
    EditText phone_num_edit_txt;
    InnerConversationDataManager innerConversationDataManager;

    public AlertCallbackResponse(Context context) {
        this.context = context;
    }

    public AlertDialog.Builder getAlertBuilder() {
        return alertBuilder;
    }

    public void create(final Conversation currentConversation) {
        Agent agent = AgentDataManager.getAgentInstanse();
        String phone_number = "";
        if (agent != null && agent.getRep() != null) {
            phone_number = agent.getRep().telephone;
        }
        phone_num_edit_txt = new EditText(context);
        phone_num_edit_txt.setText(phone_number);
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (checkValidNumber(phone_num_edit_txt.getText().toString()) == true) {
                           /* innerConversationDataManager = new InnerConversationDataManager(context, currentConversation);
                            innerConversationDataManager.makeCallbackResponse(phone_num_edit_txt.getText().toString());*/
                            SendResponseHelper sendResponseHelper=new SendResponseHelper();
                            sendResponseHelper.sendCallBack(context,currentConversation.idSurfer,phone_num_edit_txt.getText().toString());
                        }
                        else
                        alertBuilder.setTitle("not valid phone number");
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setMessage("please enter a valid  number")
                .setView(phone_num_edit_txt)
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener);
    }


    public boolean checkValidNumber(String phone_number) {
        String numbersValidation = "[0-9]+";
        if (phone_number.length() > 6 && phone_number.matches(numbersValidation))
            return true;
        return false;
    }

    public void show() {
        if (alertBuilder != null)
            alertBuilder.show();
    }

}

