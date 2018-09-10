package com.mamahome360.mamahomele.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResObj {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("wardAssigned")
    @Expose
    private String wardAssigned;
    @SerializedName("latlon")
    @Expose
    private String latlon;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWardAssigned() {
        return wardAssigned;
    }

    public void setWardAssigned(String wardAssigned) {
        this.wardAssigned = wardAssigned;
    }

    public String getLatlon() {
        return latlon;
    }

    public void setLatlon(String latlon) {
        this.latlon = latlon;
    }

}