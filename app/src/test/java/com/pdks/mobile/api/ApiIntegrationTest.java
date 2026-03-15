package com.pdks.mobile.api;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import com.pdks.mobile.constants.LeaveType;
import com.pdks.mobile.constants.RequestStatus;
import com.pdks.mobile.model.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API entegrasyon testleri.
 * MockWebServer ile gerçek HTTP isteği yapılır, Retrofit + Gson pipeline'ı
 * uçtan uca test edilir. MockDataProvider'daki JSON'lar kullanılır.
 *
 * Bu testler JVM üzerinde çalışır — Android cihaz gerekmez.
 */
public class ApiIntegrationTest {

    private MockWebServer server;
    private ApiService apiService;

    @Before
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();

        apiService = new Retrofit.Builder()
                .baseUrl(server.url("/mobile/api/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    @After
    public void teardown() throws Exception {
        server.shutdown();
    }

    private void enqueue(String body) {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body));
    }

    // ════════════════════════════════════
    //  AUTH — Login
    // ════════════════════════════════════

    @Test
    public void login_patron_success() throws Exception {
        enqueue(MockDataProvider.loginPatronSuccess());

        LoginRequest req = new LoginRequest("ABC", "100", "dev1", "Samsung", "patron", "AID_x");
        Response<LoginResponse> resp = apiService.login(req).execute();

        assertThat(resp.isSuccessful()).isTrue();
        LoginResponse body = resp.body();
        assertThat(body).isNotNull();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.isPatron()).isTrue();
        assertThat(body.getPersonnelId()).isEqualTo(100);
        assertThat(body.getToken()).isNotEmpty();

        // İsteğin doğru endpoint'e gittiğini doğrula
        RecordedRequest recorded = server.takeRequest();
        assertThat(recorded.getPath()).contains("login");
        assertThat(recorded.getMethod()).isEqualTo("POST");
    }

    @Test
    public void login_personel_success() throws Exception {
        enqueue(MockDataProvider.loginPersonelSuccess());

        LoginRequest req = new LoginRequest("ABC", "42", "dev2", "Pixel", "personel", "AID_y");
        Response<LoginResponse> resp = apiService.login(req).execute();

        assertThat(resp.body().isPatron()).isFalse();
        assertThat(resp.body().getPersonnelId()).isEqualTo(42);
        assertThat(resp.body().getPersonnelName()).isEqualTo("Mehmet Demir");
    }

    @Test
    public void login_failed() throws Exception {
        enqueue(MockDataProvider.loginFailed());

        LoginRequest req = new LoginRequest("XXX", "0000", "dev", "Phone", "personel", "AID_z");
        Response<LoginResponse> resp = apiService.login(req).execute();

        assertThat(resp.body().isSuccess()).isFalse();
        assertThat(resp.body().getMessage()).isNotEmpty();
    }

    @Test
    public void login_http403_deviceBinding() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(403).setBody("{}"));

        LoginRequest req = new LoginRequest("ABC", "42", "dev", "Phone", "personel", "AID");
        Response<LoginResponse> resp = apiService.login(req).execute();

        assertThat(resp.code()).isEqualTo(403);
        assertThat(resp.isSuccessful()).isFalse();
    }

    // ════════════════════════════════════
    //  PATRON — Dashboard
    // ════════════════════════════════════

    @Test
    public void dashboardSummary_allDepartments() throws Exception {
        enqueue(MockDataProvider.dashboardSummaryAll());

        Response<DashboardSummary> resp = apiService.getDashboardSummary(null).execute();

        assertThat(resp.isSuccessful()).isTrue();
        DashboardSummary s = resp.body();
        assertThat(s).isNotNull();
        assertThat(s.getTotalCount()).isGreaterThan(0);
        assertThat(s.getActiveCount()).isAtLeast(0);
    }

    @Test
    public void dashboardSummary_filteredByDepartment() throws Exception {
        enqueue(MockDataProvider.dashboardSummaryDept1());

        Response<DashboardSummary> resp = apiService.getDashboardSummary(1).execute();
        assertThat(resp.isSuccessful()).isTrue();

        // İsteğin query param içerdiğini doğrula
        RecordedRequest recorded = server.takeRequest();
        assertThat(recorded.getPath()).contains("department_id=1");
    }

    @Test
    public void departments_returnsList() throws Exception {
        enqueue(MockDataProvider.departments());

        Response<List<Department>> resp = apiService.getDepartments().execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();
        assertThat(resp.body().get(0).getName()).isNotEmpty();
    }

    @Test
    public void personnelList_returnsList() throws Exception {
        enqueue(MockDataProvider.personnelList());

        Response<List<PersonnelInfo>> resp = apiService.getPersonnelList(null).execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();

        PersonnelInfo first = resp.body().get(0);
        assertThat(first.getName()).isNotEmpty();
        assertThat(first.getStatus()).isNotEmpty();
    }

    // ════════════════════════════════════
    //  PATRON — İzin Onayları
    // ════════════════════════════════════

    @Test
    public void pendingLeaveRequests_annual() throws Exception {
        enqueue(MockDataProvider.pendingAnnualLeaves());

        Response<List<LeaveRequest>> resp =
                apiService.getPendingLeaveRequests(LeaveType.ANNUAL, RequestStatus.PENDING).execute();

        assertThat(resp.isSuccessful()).isTrue();
        List<LeaveRequest> list = resp.body();
        assertThat(list).isNotEmpty();

        // Her talep beklenen tipe sahip olmalı
        for (LeaveRequest r : list) {
            assertThat(r.getPersonnelName()).isNotEmpty();
            assertThat(r.getStartDate()).isNotEmpty();
        }

        // Query param kontrolü
        RecordedRequest recorded = server.takeRequest();
        assertThat(recorded.getPath()).contains("type=yillik");
        assertThat(recorded.getPath()).contains("status=pending");
    }

    @Test
    public void pendingLeaveRequests_hourly() throws Exception {
        enqueue(MockDataProvider.pendingHourlyLeaves());

        Response<List<LeaveRequest>> resp =
                apiService.getPendingLeaveRequests(LeaveType.HOURLY, RequestStatus.PENDING).execute();

        assertThat(resp.body()).isNotEmpty();
        // Saatlik izinlerde saat bilgisi olmalı
        LeaveRequest first = resp.body().get(0);
        assertThat(first.getStartTime()).isNotNull();
    }

    @Test
    public void pendingAdvanceRequests() throws Exception {
        enqueue(MockDataProvider.pendingAdvances());

        Response<List<AdvanceRequest>> resp =
                apiService.getPendingAdvanceRequests(RequestStatus.PENDING).execute();

        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();

        AdvanceRequest first = resp.body().get(0);
        assertThat(first.getAmount()).isGreaterThan(0);
        assertThat(first.getPersonnelName()).isNotEmpty();
    }

    @Test
    public void approveRequest_success() throws Exception {
        enqueue(MockDataProvider.approveSuccess());

        ApprovalRequest req = new ApprovalRequest("5", "leave", "approve", null);
        Response<ApiResponse> resp = apiService.approveRequest(req).execute();

        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body().isSuccess()).isTrue();
    }

    // ════════════════════════════════════
    //  PATRON — Fazla/Eksik Mesai
    // ════════════════════════════════════

    @Test
    public void lateEarlyReport_returnsMixedTypes() throws Exception {
        enqueue(MockDataProvider.lateEarlyReport());

        Response<List<LateEarlyRecord>> resp = apiService.getLateEarlyReport(null).execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();

        // En az bir overtime ve bir undertime olmalı
        boolean hasOvertime = false, hasUndertime = false;
        for (LateEarlyRecord r : resp.body()) {
            if ("overtime".equals(r.getType())) hasOvertime = true;
            if ("undertime".equals(r.getType())) hasUndertime = true;
        }
        assertThat(hasOvertime).isTrue();
        assertThat(hasUndertime).isTrue();
    }

    // ════════════════════════════════════
    //  PERSONEL — Raporlar
    // ════════════════════════════════════

    @Test
    public void dailyReport() throws Exception {
        enqueue(MockDataProvider.dailyReport());

        Response<List<AttendanceRecord>> resp = apiService.getDailyReport(42).execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();

        AttendanceRecord first = resp.body().get(0);
        assertThat(first.getDate()).isNotEmpty();
        assertThat(first.getStatusDisplay()).isNotEmpty();
    }

    @Test
    public void weeklyReport() throws Exception {
        enqueue(MockDataProvider.weeklyReport());

        Response<List<AttendanceRecord>> resp = apiService.getWeeklyReport(42).execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();
    }

    @Test
    public void monthlyOvertime() throws Exception {
        enqueue(MockDataProvider.monthlyOvertime("2026-03"));

        Response<MonthlyOvertime> resp = apiService.getMonthlyOvertime(42, "2026-03").execute();
        assertThat(resp.isSuccessful()).isTrue();

        MonthlyOvertime data = resp.body();
        assertThat(data).isNotNull();
        assertThat(data.getTotalWorkHours()).isAtLeast(0);
        assertThat(data.getTotalWorkDays()).isAtLeast(0);
    }

    // ════════════════════════════════════
    //  PERSONEL — İzin / Avans Talepleri
    // ════════════════════════════════════

    @Test
    public void submitLeaveRequest() throws Exception {
        enqueue(MockDataProvider.submitSuccess());

        LeaveSubmitRequest req = new LeaveSubmitRequest(
                42, "yillik", "01.03.2026", "05.03.2026", null, null, "Tatil");
        Response<ApiResponse> resp = apiService.submitLeaveRequest(req).execute();

        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body().isSuccess()).isTrue();

        // POST body doğrulama
        RecordedRequest recorded = server.takeRequest();
        String body = recorded.getBody().readUtf8();
        assertThat(body).contains("\"personnel_id\":42");
        assertThat(body).contains("\"leave_type\":\"yillik\"");
    }

    @Test
    public void submitAdvanceRequest() throws Exception {
        enqueue(MockDataProvider.submitSuccess());

        AdvanceSubmitRequest req = new AdvanceSubmitRequest(42, 5000.0, "Acil ihtiyaç");
        Response<ApiResponse> resp = apiService.submitAdvanceRequest(req).execute();

        assertThat(resp.isSuccessful()).isTrue();

        RecordedRequest recorded = server.takeRequest();
        String body = recorded.getBody().readUtf8();
        assertThat(body).contains("\"amount\":5000");
    }

    @Test
    public void leaveHistory() throws Exception {
        enqueue(MockDataProvider.leaveHistory());

        Response<List<LeaveRequest>> resp = apiService.getLeaveHistory(42).execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();

        // Farklı durumlar olmalı (pending, approved, rejected)
        boolean hasPending = false, hasApproved = false;
        for (LeaveRequest r : resp.body()) {
            if (RequestStatus.PENDING.equals(r.getStatus())) hasPending = true;
            if (RequestStatus.APPROVED.equals(r.getStatus())) hasApproved = true;
        }
        assertThat(hasPending || hasApproved).isTrue();
    }

    @Test
    public void advanceHistory() throws Exception {
        enqueue(MockDataProvider.advanceHistory());

        Response<List<AdvanceRequest>> resp = apiService.getAdvanceHistory(42).execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isNotEmpty();
    }

    // ════════════════════════════════════
    //  PERSONEL — Check-in / Check-out
    // ════════════════════════════════════

    @Test
    public void checkInOut_location_success() throws Exception {
        enqueue(MockDataProvider.checkInSuccess());

        CheckInOutRequest req = new CheckInOutRequest(42, 41.015, 28.979, null, "location", "dev1");
        Response<CheckInOutResponse> resp = apiService.checkInOut(req).execute();

        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body().isSuccess()).isTrue();
    }

    @Test
    public void checkInOut_qr_success() throws Exception {
        enqueue(MockDataProvider.checkInSuccess());

        CheckInOutRequest req = new CheckInOutRequest(42, 41.015, 28.979, "QR_DATA", "qr_scan", "dev1");
        Response<CheckInOutResponse> resp = apiService.qrCheckInOut(req).execute();

        assertThat(resp.isSuccessful()).isTrue();
    }

    // ════════════════════════════════════
    //  HATA DURUMLARI
    // ════════════════════════════════════

    @Test
    public void serverError_500() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        Response<DashboardSummary> resp = apiService.getDashboardSummary(null).execute();
        assertThat(resp.isSuccessful()).isFalse();
        assertThat(resp.code()).isEqualTo(500);
    }

    @Test
    public void serverError_401_unauthorized() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(401).setBody("{}"));

        Response<List<Department>> resp = apiService.getDepartments().execute();
        assertThat(resp.isSuccessful()).isFalse();
        assertThat(resp.code()).isEqualTo(401);
    }

    @Test
    public void emptyResponse_handledGracefully() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        Response<List<LeaveRequest>> resp =
                apiService.getPendingLeaveRequests("yillik", "pending").execute();
        assertThat(resp.isSuccessful()).isTrue();
        assertThat(resp.body()).isEmpty();
    }

    // ════════════════════════════════════
    //  API CONFIG — URL yapısı doğrulama
    // ════════════════════════════════════

    @Test
    public void apiConfig_baseUrl_endsWithSlash() {
        assertThat(ApiConfig.BASE_URL).endsWith("/");
    }

    @Test
    public void apiConfig_endpoints_notStartWithSlash() {
        // Retrofit base URL + endpoint birleştirmesinde başta / olmamalı
        assertThat(ApiConfig.LOGIN).doesNotMatch("^/.*");
        assertThat(ApiConfig.DASHBOARD_SUMMARY).doesNotMatch("^/.*");
        assertThat(ApiConfig.CHECK_IN_OUT).doesNotMatch("^/.*");
    }
}
