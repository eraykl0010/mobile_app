package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

/**
 * İzin talebi GÖNDERME modeli.
 * LeaveRequest sınıfı sunucudan GELEN izin verilerini temsil eder,
 * bu sınıf ise sunucuya GÖNDERİLEN talebi temsil eder.
 *
 * C# backend: LeaveSubmitRequest
 * SP: dbo.sp_CreateLeaveRequest
 */
public class LeaveSubmitRequest {

    @SerializedName("personnel_id")
    private int personnelId;

    @SerializedName("leave_type")
    private String leaveType;      // "yillik", "gunluk", "saatlik"

    @SerializedName("start_date")
    private String startDate;       // "dd.MM.yyyy"

    @SerializedName("end_date")
    private String endDate;         // "dd.MM.yyyy"

    @SerializedName("start_time")
    private String startTime;       // "HH:mm" — sadece saatlik izin

    @SerializedName("end_time")
    private String endTime;         // "HH:mm" — sadece saatlik izin

    @SerializedName("reason")
    private String reason;

    public LeaveSubmitRequest(int personnelId, String leaveType,
                              String startDate, String endDate,
                              String startTime, String endTime,
                              String reason) {
        this.personnelId = personnelId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
    }

    // Getters
    public int getPersonnelId() { return personnelId; }
    public String getLeaveType() { return leaveType; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getReason() { return reason; }
}