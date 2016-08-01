package com.example.chaya.bontact.DataManagers;

import com.example.chaya.bontact.Models.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 7/31/2016.
 */
public class VisitorsDataManager {
    public static List<Visitor> visitorsList = null;

    public VisitorsDataManager() {
        initVisitorsList();

    }

    public static List<Visitor> getVisitorsList() {
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

    public static void addVisitorToList(Visitor visitor) {
        initVisitorsList();
        getVisitorsList().add(visitor);
    }

  /*  public static void updateVisitorDetails(Visitor visitor) {
        initVisitorsList();
        for (Visitor item : visitorsList) {
            if (item.idSurfer == visitor.idSurfer)

        }
    }*/
}
