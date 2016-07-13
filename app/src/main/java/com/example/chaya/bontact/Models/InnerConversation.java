
package com.example.chaya.bontact.Models;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class InnerConversation {

    @SerializedName("conversationPage")
    @Expose
    public String conversationPage;
    @SerializedName("idSurfer")
    @Expose
    public int idSurfer;
    @SerializedName("actionType")
    @Expose
    public int actionType;
    @SerializedName("timeRequest")
    @Expose
    public String timeRequest;
    @SerializedName("from_s")
    @Expose
    public String from_s;
    @SerializedName("mess")
    @Expose
    public String mess;
    @SerializedName("req_id")
    @Expose
    public int req_id;
    @SerializedName("rep_request")
    @Expose
    public boolean rep_request;
    @SerializedName("record")
    @Expose
    public boolean record;
    @SerializedName("agentName")
    @Expose
    public String agentName;
    @SerializedName("datatype")
    @Expose
    public int datatype;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("systemMsg")
    @Expose
    public boolean systemMsg;
    @SerializedName("recordUrl")
    @Expose
    public String recordUrl;

 public String getConversationPage() {
        return conversationPage;
    }

    public void setConversationPage(String conversationPage) {
        this.conversationPage = conversationPage;
    }

    public String getRecordUrl() {
        return recordUrl;
    }

    public void setRecordUrl(String recordUrl) {
        this.recordUrl = recordUrl;
    }

    public boolean isSystemMsg() {
        return systemMsg;
    }

    public void setSystemMsg(boolean systemMsg) {
        this.systemMsg = systemMsg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDatatype() {
        return datatype;
    }

    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public boolean isRep_request() {
        return rep_request;
    }

    public void setRep_request(boolean rep_request) {
        this.rep_request = rep_request;
    }

    public int getReq_id() {
        return req_id;
    }

    public void setReq_id(int req_id) {
        this.req_id = req_id;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public String getFrom_s() {
        return from_s;
    }

    public void setFrom_s(String from_s) {
        this.from_s = from_s;
    }

    public String getTimeRequest() {
        return timeRequest;
    }

    public void setTimeRequest(String timeRequest) {
        this.timeRequest = timeRequest;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public int getIdSurfer() {
        return idSurfer;
    }

    public void setIdSurfer(int idSurfer) {
        this.idSurfer = idSurfer;
    }
}