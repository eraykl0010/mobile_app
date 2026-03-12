package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class LeaveRequest {

    @SerializedName("id")
    private String id;

    @SerializedName("personnel_name")
    private String personnelName;

    @SerializedName("department")
    private String department;

    @SerializedName("leave_type")
    private String leaveType; // "yillik", "saatlik"

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("reason")
    private String reason;

    @SerializedName("status")
    private String status; // "pending", "approved", "rejected"

    @SerializedName("request_date")
    private String requestDate;

    @SerializedName("remaining_days")
    private double remainingDays;

    // Getters
    public String getId() { return id; }
    public String getPersonnelName() { return personnelName; }
    public String getDepartment() { return department; }
    public String getLeaveType() { return leaveType; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public String getRequestDate() { return requestDate; }
    public double getRemainingDays() { return remainingDays; }

    public String getLeaveTypeDisplay() {
        switch (leaveType) {
            case "yillik":  return "Yıllık İzin";
            case "gunluk":  return "Günlük İzin";
            case "saatlik": return "Saatlik İzin";
            default:        return leaveType;
        }
    }
}