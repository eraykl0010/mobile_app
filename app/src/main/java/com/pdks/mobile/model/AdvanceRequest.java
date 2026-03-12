package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class AdvanceRequest {

    @SerializedName("id")
    private String id;       // evrakKodu (EVR000023) — string

    @SerializedName("personnel_name")
    private String personnelName;

    @SerializedName("department")
    private String department;

    @SerializedName("amount")
    private double amount;

    @SerializedName("reason")
    private String reason;

    @SerializedName("status")
    private String status;

    @SerializedName("request_date")
    private String requestDate;

    // Getters
    public String getId() { return id; }
    public String getPersonnelName() { return personnelName; }
    public String getDepartment() { return department; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
    public String getStatus() { return status != null ? status : ""; }
    public String getRequestDate() { return requestDate; }
}