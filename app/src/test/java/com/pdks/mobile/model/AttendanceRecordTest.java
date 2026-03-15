package com.pdks.mobile.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import com.pdks.mobile.constants.AttendanceStatus;

import org.junit.Test;

/**
 * AttendanceRecord model testleri.
 * Gson deserialization, getStatusDisplay(), equals/hashCode.
 */
public class AttendanceRecordTest {

    private final Gson gson = new Gson();

    private AttendanceRecord fromJson(String status) {
        return gson.fromJson(
                "{\"date\":\"15.03.2026\",\"day_name\":\"Pazartesi\","
                        + "\"check_in\":\"08:30\",\"check_out\":\"17:30\","
                        + "\"work_hours\":\"9.0\",\"overtime_hours\":\"1.0\","
                        + "\"status\":\"" + status + "\"}",
                AttendanceRecord.class);
    }

    // ── getStatusDisplay ──

    @Test
    public void statusDisplay_normal() {
        assertThat(fromJson("normal").getStatusDisplay()).isEqualTo("Normal");
    }

    @Test
    public void statusDisplay_late() {
        assertThat(fromJson("late").getStatusDisplay()).isEqualTo("Geç");
    }

    @Test
    public void statusDisplay_early() {
        assertThat(fromJson("early").getStatusDisplay()).isEqualTo("Erken Çıkış");
    }

    @Test
    public void statusDisplay_absent() {
        assertThat(fromJson("absent").getStatusDisplay()).isEqualTo("Devamsız");
    }

    @Test
    public void statusDisplay_leave() {
        assertThat(fromJson("leave").getStatusDisplay()).isEqualTo("İzinli");
    }

    @Test
    public void statusDisplay_unknown_returnsRawValue() {
        assertThat(fromJson("custom_status").getStatusDisplay()).isEqualTo("custom_status");
    }

    // ── Gson deserialization ──

    @Test
    public void deserialization_allFieldsMapped() {
        AttendanceRecord r = fromJson("normal");
        assertThat(r.getDate()).isEqualTo("15.03.2026");
        assertThat(r.getDayName()).isEqualTo("Pazartesi");
        assertThat(r.getCheckIn()).isEqualTo("08:30");
        assertThat(r.getCheckOut()).isEqualTo("17:30");
        assertThat(r.getWorkHours()).isEqualTo("9.0");
        assertThat(r.getOvertimeHours()).isEqualTo("1.0");
        assertThat(r.getStatus()).isEqualTo("normal");
    }

    @Test
    public void deserialization_nullFields_handledGracefully() {
        AttendanceRecord r = gson.fromJson("{\"status\":\"absent\"}", AttendanceRecord.class);
        assertThat(r.getDate()).isNull();
        assertThat(r.getCheckIn()).isNull();
        assertThat(r.getStatusDisplay()).isEqualTo("Devamsız");
    }

    // ── equals & hashCode ──

    @Test
    public void equals_sameData_returnsTrue() {
        AttendanceRecord a = fromJson("normal");
        AttendanceRecord b = fromJson("normal");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void equals_differentStatus_returnsFalse() {
        AttendanceRecord a = fromJson("normal");
        AttendanceRecord b = fromJson("late");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    public void equals_null_returnsFalse() {
        assertThat(fromJson("normal")).isNotEqualTo(null);
    }

    // ── Constants cross-check ──

    @Test
    public void statusConstants_matchExpectedValues() {
        assertThat(AttendanceStatus.NORMAL).isEqualTo("normal");
        assertThat(AttendanceStatus.LATE).isEqualTo("late");
        assertThat(AttendanceStatus.EARLY).isEqualTo("early");
        assertThat(AttendanceStatus.ABSENT).isEqualTo("absent");
        assertThat(AttendanceStatus.LEAVE).isEqualTo("leave");
    }
}
