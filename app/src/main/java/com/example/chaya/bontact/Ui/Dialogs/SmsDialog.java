package com.example.chaya.bontact.Ui.Dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chaya.bontact.DataManagers.InnerConversationDataManager;
import com.example.chaya.bontact.Helpers.SendResponseHelper;
import com.example.chaya.bontact.R;

/**
 * Created by chaya on 9/1/2016.
 */
public class SmsDialog {

    AlertDialog alert;
    Context context;
    EditText input;
    TextInputLayout textInputLayout;
    int id_surfer;
    SendResponseHelper sendResponseHelper;
    InnerConversationDataManager innerConversationDataManager;

    public SmsDialog(Context context) {
        this.context = context;
        sendResponseHelper = new SendResponseHelper();
    }

    public void create(int id_surfer) {
        input = new EditText(context);
        this.id_surfer = id_surfer;
        innerConversationDataManager = new InnerConversationDataManager(context, id_surfer);
        textInputLayout = new TextInputLayout(context);
        textInputLayout.addView(input);
        alert = new AlertDialog.Builder(context).setTitle("Type your SMS message below")
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
            if (!input.getText().toString().equals("") && input.getText().toString() != null) {
                sendResponseHelper.sendSms(context, input.getText().toString(), id_surfer);
                //  innerConversationDataManager.addTextMsgToList(ChannelsTypes.sms, input.getText().toString(), false);
                alert.dismiss();
            } else {
                textInputLayout.setError(context.getString(R.string.invalid_sms_content));
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



