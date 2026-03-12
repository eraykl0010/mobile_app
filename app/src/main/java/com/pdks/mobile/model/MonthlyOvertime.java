package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class MonthlyOvertime {

    @SerializedName("month")
    private String month;

    @SerializedName("total_work_hours")
    private double totalWorkHours;

    @SerializedName("total_overtime_hours")
    private double totalOvertimeHours;

    @SerializedName("total_work_days")
    private int totalWorkDays;

    @SerializedName("absent_days")
    private int absentDays;

    @SerializedName("late_count")
    private int lateCount;

    @SerializedName("early_leave_count")
    private int earlyLeaveCount;

    // Getters
    public String getMonth() { return month; }
    public double getTotalWorkHours() { return totalWorkHours; }
    public double getTotalOvertimeHours() { return totalOvertimeHours; }
    public int getTotalWorkDays() { return totalWorkDays; }
    public int getAbsentDays() { return absentDays; }
    public int getLateCount() { return lateCount; }
    public int getEarlyLeaveCount() { return earlyLeaveCount; }
}