
package com.example.chaya.bontact.Models;

import java.sql.Date;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Conversation {

    @SerializedName("idSurfer")
    @Expose
    public int idSurfer;
    @SerializedName("visitor_name")
    @Expose
    public String visitor_name;
    @SerializedName("avatar")
    @Expose
    public String avatar;
    @SerializedName("returning")
    @Expose
    public boolean returning;
    @SerializedName("closed")
    @Expose
    public boolean closed;
    @SerializedName("resloved")
    @Expose
    public boolean resloved;
    @SerializedName("lastdate")
    @Expose
    public String lastdate;
    @SerializedName("lastSentence")
    @Expose
    public String lastSentence;
    @SerializedName("lasttype")
    @Expose
    public int lasttype;
    @SerializedName("actionId")
    @Expose
    public int actionId;
    @SerializedName("reply")
    @Expose
    public boolean reply;
    @SerializedName("page")
    @Expose
    public String page;
    @SerializedName("ip")
    @Expose
    public String ip;
    @SerializedName("browser")
    @Expose
    public String browser;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("unread")
    @Expose
    public int unread;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("agent")
    @Expose
    public String agent;
    @SerializedName("displayname")
    @Expose
    public String displayname;

    public int getIdSurfer() {
        return idSurfer;
    }

    public void setIdSurfer(int idSurfer) {
        this.idSurfer = idSurfer;
    }

    public String getVisitor_name() {
        return visitor_name;
    }

    public void setVisitor_name(String visitor_name) {
        this.visitor_name = visitor_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isResloved() {
        return resloved;
    }

    public void setResloved(boolean resloved) {
        this.resloved = resloved;
    }

    public String getLastdate() {
        return lastdate;
    }

    public void setLastdate(String lastdate) {
        this.lastdate = lastdate;
    }

    public int getLasttype() {
        return lasttype;
    }

    public void setLasttype(int lasttype) {
        this.lasttype = lasttype;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getLastSentence() {
        return lastSentence;
    }

    public void setLastSentence(String lastSentence) {

        this.lastSentence = lastSentence;
    }
}