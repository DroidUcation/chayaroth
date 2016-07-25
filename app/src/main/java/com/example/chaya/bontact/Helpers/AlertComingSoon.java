package com.example.chaya.bontact.Helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.example.chaya.bontact.R;

/**
 * Created by chaya on 7/11/2016.
 */
public class AlertComingSoon {

    public static void show(Context context)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
        builder.setMessage(context.getResources().getString(R.string.tbd))
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener()
                {  @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
                });
        AlertDialog alertDialog= builder.create();
        alertDialog.show();
    }

}
