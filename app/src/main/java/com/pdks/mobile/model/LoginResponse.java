package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    private String token;

    @SerializedName("personnel_id")
    private int personnelId;

    @SerializedName("personnel_name")
    private String personnelName;

    @SerializedName("is_patron")
    private boolean isPatron;

    @SerializedName("department")
    private String department;

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public int getPersonnelId() { return personnelId; }
    public String getPersonnelName() { return personnelName; }
    public boolean isPatron() { return isPatron; }
    public String getDepartment() { return department; }
}