package com.example.chaya.bontact.DataManagers;

import android.content.Context;
import android.content.Intent;

import com.example.chaya.bontact.BroadCastRecivers.NewVisitorReciver;
import com.example.chaya.bontact.Models.Visitor;
import com.example.chaya.bontact.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 7/31/2016.
 */
public class VisitorsDataManager {

    public static List<Visitor> visitorsList = null;
    public static Context contextStatic;


    public VisitorsDataManager(Context context) {
        initVisitorsList();
        contextStatic = context;

    }

    public static List<Visitor> getVisitorsList() {
        initVisitorsList();
        return visitorsList;
    }

    public static void setAllVisitorsList(List<Visitor> visitorsList) {
        initVisitorsList();
        VisitorsDataManager.visitorsList = visitorsList;
    }

    public static void initVisitorsList() {
        if (visitorsList == null)
            visitorsList = new ArrayList<>();
    }

    public static void addVisitorToList(Context context, Visitor visitor) {
        initVisitorsList();
        getVisitorsList().add(visitor);

        Intent intent = new Intent(NewVisitorReciver.ACTION_NEW_VISITOR);
        intent.setType("*/*");
       // intent.setAction(context.getResources().getString(R.string.new_visitor_action));
        //put whatever data you want to send, if any
        //intent.putExtra("message", message);
        //send broadcast
        if (context != null)
            context.sendBroadcast(intent);
        else if (contextStatic != null)
            contextStatic.sendBroadcast(intent);
    }

  /*  public static void updateVisitorDetails(Visitor visitor) {
        initVisitorsList();
        for (Visitor item : visitorsList) {
            if (item.idSurfer == visitor.idSurfer)

        }
    }*/
}
