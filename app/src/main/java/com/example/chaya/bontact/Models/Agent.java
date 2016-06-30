package com.example.chaya.bontact.Models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Agent {
    public Agent() {
        rep=new Rep();
        settings=new Settings();
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

        @SerializedName("username")
        @Expose
        public String username;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("id_Customer")
        @Expose
        public int idCustomer;
        @SerializedName("code")
        @Expose
        public String code;
        @SerializedName("telephone")
        @Expose
        public Object telephone;
        @SerializedName("key")
        @Expose
        public String key;
        @SerializedName("id_Representive")
        @Expose
        public int idRepresentive;

        public String getName() {
            if(name==null)
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

    }

    public String getToken() {
        return token;
    }

    public String getName() {
        if( rep!=null)
        return rep.getName();
        return null;
    }

    public Rep getRep() {
        return rep;
    }

    public Settings getSettings() {
        return settings;
    }
}