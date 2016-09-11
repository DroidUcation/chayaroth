package com.example.chaya.bontact.Models;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chaya on 9/11/2016.
 */
@Generated("org.jsonschema2pojo")
public class Representative {
    @SerializedName("id_Representive")
    @Expose
    public int idRepresentive;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("img")
    @Expose
    public String img;

}
