package com.pdks.mobile.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import com.pdks.mobile.constants.OvertimeType;

import org.junit.Test;

/**
 * LateEarlyRecord model testleri.
 */
public class LateEarlyRecordTest {

    private final Gson gson = new Gson();

    private LateEarlyRecord create(String type, int diffMinutes) {
        return gson.fromJson(
                "{\"personnel_name\":\"Test\",\"department\":\"IT\","
                        + "\"type\":\"" + type + "\","
                        + "\"scheduled_time\":\"08:00\",\"actual_time\":\"08:15\","
                        + "\"difference_minutes\":" + diffMinutes + ","
                        + "\"date\":\"15.03.2026\"}",
                LateEarlyRecord.class);
    }

    @Test
    public void typeDisplay_overtime() {
        assertThat(create("overtime", 30).getTypeDisplay()).isEqualTo("Fazla Mesai");
    }

    @Test
    public void typeDisplay_undertime() {
        assertThat(create("undertime", 15).getTypeDisplay()).isEqualTo("Eksik Mesai");
    }

    @Test
    public void typeDisplay_late() {
        assertThat(create("late", 10).getTypeDisplay()).isEqualTo("Geç Geldi");
    }

    @Test
    public void typeDisplay_early() {
        assertThat(create("early", 20).getTypeDisplay()).isEqualTo("Erken Çıktı");
    }

    @Test
    public void deserialization_allFields() {
        LateEarlyRecord r = create("overtime", 45);
        assertThat(r.getPersonnelName()).isEqualTo("Test");
        assertThat(r.getDepartment()).isEqualTo("IT");
        assertThat(r.getType()).isEqualTo("overtime");
        assertThat(r.getScheduledTime()).isEqualTo("08:00");
        assertThat(r.getActualTime()).isEqualTo("08:15");
        assertThat(r.getDifferenceMinutes()).isEqualTo(45);
        assertThat(r.getDate()).isEqualTo("15.03.2026");
    }

    @Test
    public void overtimeConstants_matchApiValues() {
        assertThat(OvertimeType.OVERTIME).isEqualTo("overtime");
        assertThat(OvertimeType.UNDERTIME).isEqualTo("undertime");
    }
}
