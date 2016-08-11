package com.example.chaya.bontact.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaya on 7/31/2016.
 */
@Generated("org.jsonschema2pojo")
public class Visitor {

    @SerializedName("isNew")
    @Expose
    public boolean isNew;
    @SerializedName("displayName")
    @Expose
    public String displayName;
    @SerializedName("isChatting")
    @Expose
    public boolean isChatting;
    @SerializedName("idContact")
    @Expose
    public String idContact;
    @SerializedName("statusType")
    @Expose
    public int statusType;
    @SerializedName("id_Call")
    @Expose
    public String idCall;
    @SerializedName("browseType")
    @Expose
    public String browseType;
    @SerializedName("purpose")
    @Expose
    public String purpose;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("name_Representive")
    @Expose
    public String nameRepresentive;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("timeConnect")
    @Expose
    public String timeConnect;
    @SerializedName("page")
    @Expose
    public List<String> page = new ArrayList<String>();
    @SerializedName("invitation")
    @Expose
    public String invitation;
    @SerializedName("id_Surfer")
    @Expose
    public int idSurfer;
    @SerializedName("live")
    @Expose
    public boolean live;
    @SerializedName("mail")
    @Expose
    public String mail;
    @SerializedName("os")
    @Expose
    public String os;
    @SerializedName("timeVisit")
    @Expose
    public int timeVisit;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("lastTime")
    @Expose
    public String lastTime;
    @SerializedName("id_Customer")
    @Expose
    public int idCustomer;
    @SerializedName("ip")
    @Expose
    public String ip;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("isWaiting")
    @Expose
    public boolean isWaiting;
    @SerializedName("referrer")
    @Expose
    public String referrer;
    @SerializedName("name_Surfer")
    @Expose
    public String nameSurfer;
    @SerializedName("browseVersion")
    @Expose
    public String browseVersion;
    @SerializedName("mobile")
    @Expose
    public boolean mobile;

}

