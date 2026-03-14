package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class LateEarlyRecord {

    @SerializedName("personnel_name")
    private String personnelName;

    @SerializedName("department")
    private String department;

    @SerializedName("type")
    private String type; // "late" veya "early"

    @SerializedName("scheduled_time")
    private String scheduledTime;

    @SerializedName("actual_time")
    private String actualTime;

    @SerializedName("difference_minutes")
    private int differenceMinutes;

    @SerializedName("date")
    private String date;

    // Getters
    public String getPersonnelName() { return personnelName; }
    public String getDepartment() { return department; }
    public String getType() { return type; }
    public String getScheduledTime() { return scheduledTime; }
    public String getActualTime() { return actualTime; }
    public int getDifferenceMinutes() { return differenceMinutes; }
    public String getDate() { return date; }
    public String getTypeDisplay() {
        switch (type) {
            case "overtime":  return "Fazla Mesai";
            case "undertime": return "Eksik Mesai";
            case "late":      return "Geç Geldi";
            case "early":     return "Erken Çıktı";
            default:          return type;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LateEarlyRecord that = (LateEarlyRecord) o;
        return differenceMinutes == that.differenceMinutes
                && java.util.Objects.equals(personnelName, that.personnelName)
                && java.util.Objects.equals(department, that.department)
                && java.util.Objects.equals(type, that.type)
                && java.util.Objects.equals(scheduledTime, that.scheduledTime)
                && java.util.Objects.equals(actualTime, that.actualTime)
                && java.util.Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(personnelName, department, type,
                scheduledTime, actualTime, differenceMinutes, date);
    }
}