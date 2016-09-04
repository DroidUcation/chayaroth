package com.example.chaya.bontact.Models;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Agent {
    public Agent() {
        rep = new Rep();
        settings = new Settings();
    }

    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("settings")
    @Expose
    public Settings settings;
    @SerializedName("rep")
    @Expose
    public Rep rep;

    @Generated("org.jsonschema2pojo")
    public class Rep {

        @SerializedName("id_Customer")
        @Expose
        public int idCustomer;
        @SerializedName("id_Representive")
        @Expose
        public int idRepresentive;
        @SerializedName("username")
        @Expose
        public String username;
        @SerializedName("code")
        @Expose
        public String code;
        @SerializedName("key")
        @Expose
        public String key;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("telephone")
        @Expose
        public String telephone;
        @SerializedName("avatar")
        @Expose
        public String avatar;

        public String getName() {
            if (name == null)
                return username;
            return name;
        }

    }

    @Generated("org.jsonschema2pojo")
    public class Settings {

        @SerializedName("gender")
        @Expose
        public boolean gender;
        @SerializedName("Opening_statement")
        @Expose
        public String openingStatement;
        @SerializedName("department")
        @Expose
        public int department;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("online")
        @Expose
        public boolean online;
        @SerializedName("msgPushNotification")
        @Expose
        public boolean msgPushNotification;
        @SerializedName("visitorPushNotification")
        @Expose
        public boolean visitorPushNotification;


    }

    public String getToken() {
        return token;
    }

    public String getName() {
        if (rep != null)
            return rep.getName();
        return null;
    }
    public int getIdRep() {
        if (rep != null)
            return rep.idRepresentive;
        return 0;
    }
    public Rep getRep() {
        return rep;
    }

    public Settings getSettings() {
        return settings;
    }
}