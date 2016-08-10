package com.example.chaya.bontact.DataManagers;

import android.content.Context;
import android.content.Intent;

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
    public static final int ACTION_NEW_VISITOR = 0;
    public static final int ACTION_REMOVE_VISITOR = 1;


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

    public static Visitor getVisitorByIdSurfer(int id_surfer) {
        if (getVisitorsList().size() > 0)
            for (Visitor visitor : getVisitorsList()) {
                if (visitor.idSurfer == id_surfer)
                    return visitor;
            }
        return null;
    }

    public static void addVisitorToList(Context context, Visitor newVisitor) {
        initVisitorsList();
        if (newVisitor != null) {
            for (Visitor item : getVisitorsList()) {
                if (item.idSurfer == newVisitor.idSurfer)
                    return;
            }
            getVisitorsList().add(newVisitor);
            int position = getVisitorsList().indexOf(newVisitor);
            notifyAdapter(context, ACTION_NEW_VISITOR, position);
        }
    }

    public static void removeVisitorFromList(Context context, Visitor visitor) {
        initVisitorsList();
        if (visitor != null) {
            int position = getVisitorsList().indexOf(visitor);
            getVisitorsList().remove(visitor);
            notifyAdapter(context, ACTION_REMOVE_VISITOR, position);
        }
    }

    private static void notifyAdapter(Context context, int action, int position) {
        Intent intent = new Intent(context.getResources().getString(R.string.change_visitors_list_action));
        intent.setType("*/*");
        intent.putExtra(context.getResources().getString(R.string.notify_adapter_key_action), action);
        intent.putExtra(context.getResources().getString(R.string.notify_adapter_key_item_postion), position);

        if (context != null)
            context.sendBroadcast(intent);
        else if (contextStatic != null)
            contextStatic.sendBroadcast(intent);
    }

    public static int getBrowserIcon(String browser) {
        switch (browser.toLowerCase()) {
            case "firefox":
                return R.drawable.firefox;
            case "chrome":
                return R.drawable.chrome;
            case "ie":
            case "internet explorer":
                return R.drawable.msie;
            case "yandex":
                return R.drawable.yandex;
            case "safari":
            case "mobile safari":
                return R.drawable.safari;
            case "opera":
                return R.drawable.opera;
            case "edge":
                return R.drawable.edge;
            default:
                return R.drawable.default_browser;
        }
    }


}
