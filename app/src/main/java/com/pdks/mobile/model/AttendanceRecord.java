package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;
import com.pdks.mobile.constants.AttendanceStatus;

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
            case AttendanceStatus.NORMAL: return "Normal";
            case AttendanceStatus.LATE:   return "Geç";
            case AttendanceStatus.EARLY:  return "Erken Çıkış";
            case AttendanceStatus.ABSENT: return "Devamsız";
            case AttendanceStatus.LEAVE:  return "İzinli";
            default: return status;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return java.util.Objects.equals(date, that.date)
                && java.util.Objects.equals(dayName, that.dayName)
                && java.util.Objects.equals(checkIn, that.checkIn)
                && java.util.Objects.equals(checkOut, that.checkOut)
                && java.util.Objects.equals(workHours, that.workHours)
                && java.util.Objects.equals(overtimeHours, that.overtimeHours)
                && java.util.Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(date, dayName, checkIn, checkOut, workHours, overtimeHours, status);
    }
}