package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class AttendanceRecord {

    @SerializedName("date")
    private String date;

    @SerializedName("day_name")
    private String dayName;

    @SerializedName("check_in")
    private String checkIn;

    @SerializedName("check_out")
    private String checkOut;

    @SerializedName("work_hours")
    private String workHours;

    @SerializedName("overtime_hours")
    private String overtimeHours;

    @SerializedName("status")
    private String status; // "normal", "late", "early", "absent", "leave"

    // Getters
    public String getDate() { return date; }
    public String getDayName() { return dayName; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public String getWorkHours() { return workHours; }
    public String getOvertimeHours() { return overtimeHours; }
    public String getStatus() { return status; }

    public String getStatusDisplay() {
        switch (status) {
            case "normal": return "Normal";
            case "late": return "Geç";
            case "early": return "Erken Çıkış";
            case "absent": return "Devamsız";
            case "leave": return "İzinli";
            default: return status;
        }
    }
}