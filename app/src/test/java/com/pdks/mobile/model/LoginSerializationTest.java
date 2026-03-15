package com.pdks.mobile.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;

import org.junit.Test;

/**
 * Login request/response serialization testleri.
 * JSON formatının API ile uyumlu olduğunu doğrular.
 */
public class LoginSerializationTest {

    private final Gson gson = new Gson();

    // ── LoginRequest serialization ──

    @Test
    public void loginRequest_serializesToCorrectJson() {
        LoginRequest req = new LoginRequest("ABC", "1234", "dev1", "Samsung S24", "patron", "AID_abc");
        String json = gson.toJson(req);

        assertThat(json).contains("\"company_code\":\"ABC\"");
        assertThat(json).contains("\"card_no\":\"1234\"");
        assertThat(json).contains("\"device_id\":\"dev1\"");
        assertThat(json).contains("\"device_model\":\"Samsung S24\"");
        assertThat(json).contains("\"module_type\":\"patron\"");
        assertThat(json).contains("\"mac_address\":\"AID_abc\"");
    }

    @Test
    public void loginRequest_serializesPersonelModule() {
        LoginRequest req = new LoginRequest("XYZ", "5678", "dev2", "Pixel 8", "personel", "AID_xyz");
        String json = gson.toJson(req);
        assertThat(json).contains("\"module_type\":\"personel\"");
    }

    // ── LoginResponse deserialization ──

    @Test
    public void loginResponse_successfulLogin() {
        String json = "{\"success\":true,\"message\":\"Giriş başarılı\","
                + "\"token\":\"jwt-token-123\",\"personnel_id\":42,"
                + "\"personnel_name\":\"Mehmet\",\"is_patron\":false,"
                + "\"department\":\"Yazılım\"}";

        LoginResponse resp = gson.fromJson(json, LoginResponse.class);
        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getMessage()).isEqualTo("Giriş başarılı");
        assertThat(resp.getToken()).isEqualTo("jwt-token-123");
        assertThat(resp.getPersonnelId()).isEqualTo(42);
        assertThat(resp.getPersonnelName()).isEqualTo("Mehmet");
        assertThat(resp.isPatron()).isFalse();
        assertThat(resp.getDepartment()).isEqualTo("Yazılım");
    }

    @Test
    public void loginResponse_failedLogin() {
        String json = "{\"success\":false,\"message\":\"Hatalı kart no\","
                + "\"token\":null,\"personnel_id\":0}";

        LoginResponse resp = gson.fromJson(json, LoginResponse.class);
        assertThat(resp.isSuccess()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Hatalı kart no");
        assertThat(resp.getToken()).isNull();
        assertThat(resp.getPersonnelId()).isEqualTo(0);
    }

    @Test
    public void loginResponse_patronLogin() {
        String json = "{\"success\":true,\"is_patron\":true,\"personnel_id\":100}";
        LoginResponse resp = gson.fromJson(json, LoginResponse.class);
        assertThat(resp.isPatron()).isTrue();
        assertThat(resp.getPersonnelId()).isEqualTo(100);
    }

    // ── CheckInOutRequest serialization ──

    @Test
    public void checkInOutRequest_locationMode() {
        CheckInOutRequest req = new CheckInOutRequest(42, 41.015, 28.979, null, "location", "dev1");
        String json = gson.toJson(req);
        assertThat(json).contains("\"personnel_id\":42");
        assertThat(json).contains("\"latitude\":41.015");
        assertThat(json).contains("\"longitude\":28.979");
        assertThat(json).contains("\"type\":\"location\"");
        assertThat(json).contains("\"device_id\":\"dev1\"");
    }

    @Test
    public void checkInOutRequest_qrMode() {
        CheckInOutRequest req = new CheckInOutRequest(42, 41.015, 28.979, "QR_DATA_123", "qr_scan", "dev1");
        String json = gson.toJson(req);
        assertThat(json).contains("\"qr_code\":\"QR_DATA_123\"");
        assertThat(json).contains("\"type\":\"qr_scan\"");
    }

    // ── CheckInOutResponse deserialization ──

    @Test
    public void checkInOutResponse_success() {
        String json = "{\"success\":true,\"message\":\"Giriş kaydedildi\","
                + "\"action\":\"check_in\",\"time\":\"08:30\"}";
        CheckInOutResponse resp = gson.fromJson(json, CheckInOutResponse.class);
        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getMessage()).isEqualTo("Giriş kaydedildi");
    }

    // ── ApiResponse deserialization ──

    @Test
    public void apiResponse_success() {
        String json = "{\"success\":true,\"message\":\"İşlem başarılı\"}";
        ApiResponse resp = gson.fromJson(json, ApiResponse.class);
        assertThat(resp.isSuccess()).isTrue();
        assertThat(resp.getMessage()).isEqualTo("İşlem başarılı");
    }

    @Test
    public void apiResponse_failure() {
        String json = "{\"success\":false,\"message\":\"Yetkisiz işlem\"}";
        ApiResponse resp = gson.fromJson(json, ApiResponse.class);
        assertThat(resp.isSuccess()).isFalse();
    }
}
