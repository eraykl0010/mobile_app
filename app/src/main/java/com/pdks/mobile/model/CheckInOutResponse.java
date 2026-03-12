package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class CheckInOutResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("action")
    private String action; // "check_in" veya "check_out"

    @SerializedName("time")
    private String time;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getAction() { return action; }
    public String getTime() { return time; }
}