package com.pdks.mobile.model;

import static com.google.common.truth.Truth.assertThat;

import com.pdks.mobile.constants.PersonnelStatus;

import org.junit.Test;

/**
 * PersonnelInfo model testleri.
 * Durum display, constructor, null safety, equals/hashCode.
 */
public class PersonnelInfoTest {

    private PersonnelInfo create(String status) {
        return new PersonnelInfo(1, "Test User", "Yazılım", "08:30", "17:30", status);
    }

    // ── getStatusDisplay ──

    @Test
    public void statusDisplay_active() {
        assertThat(create(PersonnelStatus.ACTIVE).getStatusDisplay()).isEqualTo("Aktif");
    }

    @Test
    public void statusDisplay_onLeave() {
        assertThat(create(PersonnelStatus.ON_LEAVE).getStatusDisplay()).isEqualTo("İzinli");
    }

    @Test
    public void statusDisplay_absent() {
        assertThat(create(PersonnelStatus.ABSENT).getStatusDisplay()).isEqualTo("Devamsız");
    }

    @Test
    public void statusDisplay_late() {
        assertThat(create(PersonnelStatus.LATE).getStatusDisplay()).isEqualTo("Geç");
    }

    @Test
    public void statusDisplay_early() {
        assertThat(create(PersonnelStatus.EARLY).getStatusDisplay()).isEqualTo("Erken Çıkış");
    }

    @Test
    public void statusDisplay_unknown_returnsRaw() {
        assertThat(create("bilinmeyen").getStatusDisplay()).isEqualTo("bilinmeyen");
    }

    // ── Null safety ──

    @Test
    public void getStatus_nullStatus_returnsEmptyString() {
        PersonnelInfo info = new PersonnelInfo(1, "Test", "Dept", null, null, null);
        assertThat(info.getStatus()).isEmpty();
    }

    // ── equals / hashCode ──

    @Test
    public void equals_sameData_returnsTrue() {
        PersonnelInfo a = create("active");
        PersonnelInfo b = create("active");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void equals_differentId_returnsFalse() {
        PersonnelInfo a = new PersonnelInfo(1, "A", "D", null, null, "active");
        PersonnelInfo b = new PersonnelInfo(2, "A", "D", null, null, "active");
        assertThat(a).isNotEqualTo(b);
    }

    // ── Constants cross-check ──

    @Test
    public void personnelStatusConstants_matchApiValues() {
        assertThat(PersonnelStatus.ACTIVE).isEqualTo("active");
        assertThat(PersonnelStatus.LATE).isEqualTo("late");
        assertThat(PersonnelStatus.EARLY).isEqualTo("early");
        assertThat(PersonnelStatus.ON_LEAVE).isEqualTo("on_leave");
        assertThat(PersonnelStatus.ABSENT).isEqualTo("absent");
        assertThat(PersonnelStatus.NO_RECORD).isEqualTo("no_record");
    }
}
