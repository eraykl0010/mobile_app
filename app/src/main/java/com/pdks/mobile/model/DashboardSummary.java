package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class DashboardSummary {

    @SerializedName("active_count")
    private int activeCount;

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("on_leave_count")
    private int onLeaveCount;

    @SerializedName("absent_count")
    private int absentCount;

    @SerializedName("late_count")
    private int lateCount;

    @SerializedName("early_leave_count")
    private int earlyLeaveCount;

    @SerializedName("department_name")
    private String departmentName;

    // Getters
    public int getActiveCount() { return activeCount; }
    public int getTotalCount() { return totalCount; }
    public int getOnLeaveCount() { return onLeaveCount; }
    public int getAbsentCount() { return absentCount; }
    public int getLateCount() { return lateCount; }
    public int getEarlyLeaveCount() { return earlyLeaveCount; }
    public String getDepartmentName() { return departmentName; }
}