package com.pdks.mobile.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import com.pdks.mobile.constants.LeaveType;
import com.pdks.mobile.constants.RequestStatus;

import org.junit.Test;

/**
 * LeaveRequest model testleri.
 * İzin tipi display, durum yönetimi, Gson serialization, equals/hashCode.
 */
public class LeaveRequestTest {

    private final Gson gson = new Gson();

    private LeaveRequest create(String leaveType, String status) {
        return gson.fromJson(
                "{\"id\":\"5\",\"personnel_name\":\"Ali Veli\","
                        + "\"department\":\"Yazılım\",\"leave_type\":\"" + leaveType + "\","
                        + "\"start_date\":\"01.03.2026\",\"end_date\":\"05.03.2026\","
                        + "\"start_time\":\"09:00\",\"end_time\":\"12:00\","
                        + "\"reason\":\"Tatil\",\"status\":\"" + status + "\","
                        + "\"request_date\":\"28.02.2026\",\"remaining_days\":10.0}",
                LeaveRequest.class);
    }

    // ── getLeaveTypeDisplay ──

    @Test
    public void leaveTypeDisplay_annual() {
        assertThat(create(LeaveType.ANNUAL, "pending").getLeaveTypeDisplay())
                .isEqualTo("Yıllık İzin");
    }

    @Test
    public void leaveTypeDisplay_daily() {
        assertThat(create(LeaveType.DAILY, "pending").getLeaveTypeDisplay())
                .isEqualTo("Günlük İzin");
    }

    @Test
    public void leaveTypeDisplay_hourly() {
        assertThat(create(LeaveType.HOURLY, "pending").getLeaveTypeDisplay())
                .isEqualTo("Saatlik İzin");
    }

    @Test
    public void leaveTypeDisplay_unknown_returnsRaw() {
        assertThat(create("mazeret", "pending").getLeaveTypeDisplay())
                .isEqualTo("mazeret");
    }

    // ── Getter'lar ──

    @Test
    public void getters_allFieldsMapped() {
        LeaveRequest r = create(LeaveType.HOURLY, RequestStatus.APPROVED);
        assertThat(r.getId()).isEqualTo("5");
        assertThat(r.getPersonnelName()).isEqualTo("Ali Veli");
        assertThat(r.getDepartment()).isEqualTo("Yazılım");
        assertThat(r.getStartDate()).isEqualTo("01.03.2026");
        assertThat(r.getEndDate()).isEqualTo("05.03.2026");
        assertThat(r.getStartTime()).isEqualTo("09:00");
        assertThat(r.getEndTime()).isEqualTo("12:00");
        assertThat(r.getReason()).isEqualTo("Tatil");
        assertThat(r.getStatus()).isEqualTo(RequestStatus.APPROVED);
        assertThat(r.getRequestDate()).isEqualTo("28.02.2026");
        assertThat(r.getRemainingDays()).isEqualTo(10.0);
    }

    // ── equals / hashCode ──

    @Test
    public void equals_sameData_returnsTrue() {
        LeaveRequest a = create(LeaveType.ANNUAL, RequestStatus.PENDING);
        LeaveRequest b = create(LeaveType.ANNUAL, RequestStatus.PENDING);
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    public void equals_differentType_returnsFalse() {
        LeaveRequest a = create(LeaveType.ANNUAL, RequestStatus.PENDING);
        LeaveRequest b = create(LeaveType.DAILY, RequestStatus.PENDING);
        assertThat(a).isNotEqualTo(b);
    }

    // ── Constants cross-check ──

    @Test
    public void leaveTypeConstants_matchApiValues() {
        assertThat(LeaveType.ANNUAL).isEqualTo("yillik");
        assertThat(LeaveType.DAILY).isEqualTo("gunluk");
        assertThat(LeaveType.HOURLY).isEqualTo("saatlik");
        assertThat(LeaveType.ADVANCE).isEqualTo("avans");
        assertThat(LeaveType.VALUES).asList()
                .containsExactly("yillik", "gunluk", "saatlik").inOrder();
    }

    @Test
    public void requestStatusConstants_matchApiValues() {
        assertThat(RequestStatus.PENDING).isEqualTo("pending");
        assertThat(RequestStatus.APPROVED).isEqualTo("approved");
        assertThat(RequestStatus.REJECTED).isEqualTo("rejected");
    }
}
