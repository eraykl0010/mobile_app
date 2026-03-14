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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvanceRequest that = (AdvanceRequest) o;
        return Double.compare(that.amount, amount) == 0
                && java.util.Objects.equals(id, that.id)
                && java.util.Objects.equals(personnelName, that.personnelName)
                && java.util.Objects.equals(department, that.department)
                && java.util.Objects.equals(reason, that.reason)
                && java.util.Objects.equals(status, that.status)
                && java.util.Objects.equals(requestDate, that.requestDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, personnelName, department, amount, reason, status, requestDate);
    }
}