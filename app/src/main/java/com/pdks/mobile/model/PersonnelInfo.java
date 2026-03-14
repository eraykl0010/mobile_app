package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class PersonnelInfo {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("department")
    private String department;

    @SerializedName("check_in")
    private String checkIn;

    @SerializedName("check_out")
    private String checkOut;

    @SerializedName("status")
    private String status; // "active","on_leave","absent","late","early"

    // ═══ is_patron: SQL BIT → bazı driver'larda boolean (true/false),
    //     bazılarında int (0/1) dönebilir. Object kullanarak her ikisini de yakalarız.
    //     Bu şekilde Gson hiçbir durumda crash olmaz. ═══
    @SerializedName("is_patron")
    private Object isPatronRaw;

    public PersonnelInfo(int id, String name, String department,
                         String checkIn, String checkOut, String status) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public String getStatus() { return status != null ? status : ""; }

    /**
     * is_patron alanı sunucudan farklı tipler halinde gelebilir:
     *   - boolean: true / false
     *   - int/double: 1 / 0  (Dapper BIT → int dönüşü)
     *   - null veya eksik
     * Hepsini güvenli şekilde yakalar.
     */
    public boolean isPatron() {
        if (isPatronRaw instanceof Boolean) {
            return (Boolean) isPatronRaw;
        }
        if (isPatronRaw instanceof Number) {
            return ((Number) isPatronRaw).intValue() == 1;
        }
        return false; // null, eksik, veya beklenmeyen tip → patron değil
    }

    public String getStatusDisplay() {
        if (status == null) return "-";
        switch (status) {
            case "active":   return "Aktif";
            case "on_leave": return "İzinli";
            case "absent":   return "Devamsız";
            case "late":     return "Geç";
            case "early":    return "Erken Çıkış";
            default:         return status;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonnelInfo that = (PersonnelInfo) o;
        return id == that.id
                && java.util.Objects.equals(name, that.name)
                && java.util.Objects.equals(department, that.department)
                && java.util.Objects.equals(checkIn, that.checkIn)
                && java.util.Objects.equals(checkOut, that.checkOut)
                && java.util.Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, department, checkIn, checkOut, status);
    }
}