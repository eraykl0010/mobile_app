package com.pdks.mobile.api;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class MockInterceptor implements Interceptor {

    private static final String TAG = "MockAPI";
    private static final MediaType JSON = MediaType.parse("application/json");

    // check_in/check_out toggle — her istekte değişir
    private boolean nextIsCheckOut = false;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String path = request.url().encodedPath();
        String query = request.url().query();
        String method = request.method();

        // POST body'yi oku — login patron/personel ayrımı için gerekli
        String body = readRequestBody(request);

        Log.d(TAG, method + " " + path + (query != null ? "?" + query : ""));

        // 300ms yapay gecikme — gerçek ağ hissi
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}

        String responseBody = routeRequest(path, query, method, body);

        return new Response.Builder()
                .code(200)
                .message("OK (Mock)")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(JSON, responseBody))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * POST body'yi string olarak oku.
     * Mock modda body'yi analiz etmek için gerekli (ör: login module_type ayrımı).
     */
    private String readRequestBody(Request request) {
        try {
            if (request.body() == null) return "";
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            Log.w(TAG, "Request body okunamadı: " + e.getMessage());
            return "";
        }
    }

    /**
     * Endpoint yönlendirme — path.endsWith() ile güvenli eşleşme.
     *
     * path.contains() yerine path.endsWith() kullanılmasının sebebi:
     * contains("login") → "patron/login-report" gibi gelecekteki bir endpoint'i de yakalar
     * endsWith("login") → sadece tam olarak "login" ile biten path'i yakalar
     */
    private String routeRequest(String path, String query, String method, String body) {

        // ──────── AUTH ────────
        if (path.endsWith(ApiConfig.LOGIN)) {
            // Body'den module_type belirle → patron mu personel mi?
            if (body.contains("\"patron\"")) {
                return MockDataProvider.loginPatronSuccess();
            } else if (body.contains("\"0000\"")) {
                // card_no "0000" → hatalı giriş testi
                return MockDataProvider.loginFailed();
            } else {
                return MockDataProvider.loginPersonelSuccess();
            }
        }

        if (path.endsWith(ApiConfig.RESET_DEVICE)) {
            return MockDataProvider.approveSuccess();
        }

        // ──────── DEPARTMENTS ────────
        if (path.endsWith(ApiConfig.DEPARTMENT_LIST)) {
            return MockDataProvider.departments();
        }

        // ──────── DASHBOARD SUMMARY ────────
        if (path.endsWith(ApiConfig.DASHBOARD_SUMMARY)) {
            if (query != null && query.contains("department_id=")) {
                String deptId = extractParam(query, "department_id");
                switch (deptId) {
                    case "1": return MockDataProvider.dashboardSummaryDept1();
                    case "2": return MockDataProvider.dashboardSummaryDept2();
                    case "3": return MockDataProvider.dashboardSummaryDept3();
                    case "4": return MockDataProvider.dashboardSummaryDept4();
                    case "5": return MockDataProvider.dashboardSummaryDept5();
                    case "6": return MockDataProvider.dashboardSummaryDept6();
                    default:  return MockDataProvider.dashboardSummaryAll();
                }
            }
            return MockDataProvider.dashboardSummaryAll();
        }

        // ──────── PERSONNEL LIST ────────
        // Not: Bu blok PENDING_LEAVE_REQUESTS'ten önce olmalı
        // çünkü endsWith ile çakışma riski yok ama okunabilirlik için
        // patron endpoint'leri bir arada tutuyoruz
        if (path.endsWith(ApiConfig.PERSONNEL_LIST)) {
            return MockDataProvider.personnelList();
        }

        // ──────── LEAVE REQUESTS (with status filter) ────────
        if (path.endsWith(ApiConfig.PENDING_LEAVE_REQUESTS)) {
            String status = extractParam(query != null ? query : "", "status");
            String type = extractParam(query != null ? query : "", "type");

            if ("gunluk".equals(type)) {
                if ("approved".equals(status)) return MockDataProvider.approvedDailyLeaves();
                else if ("rejected".equals(status)) return MockDataProvider.rejectedDailyLeaves();
                else return MockDataProvider.pendingDailyLeaves();
            } else if ("saatlik".equals(type)) {
                if ("approved".equals(status)) return MockDataProvider.approvedHourlyLeaves();
                else if ("rejected".equals(status)) return MockDataProvider.rejectedHourlyLeaves();
                else return MockDataProvider.pendingHourlyLeaves();
            } else {
                if ("approved".equals(status)) return MockDataProvider.approvedAnnualLeaves();
                else if ("rejected".equals(status)) return MockDataProvider.rejectedAnnualLeaves();
                else return MockDataProvider.pendingAnnualLeaves();
            }
        }

        // ──────── ADVANCE REQUESTS (with status filter) ────────
        if (path.endsWith(ApiConfig.PENDING_ADVANCE_REQUESTS)) {
            String status = extractParam(query != null ? query : "", "status");
            if ("approved".equals(status)) {
                return MockDataProvider.approvedAdvances();
            } else if ("rejected".equals(status)) {
                return MockDataProvider.rejectedAdvances();
            } else {
                return MockDataProvider.pendingAdvances();
            }
        }

        // ──────── APPROVE / REJECT ────────
        if (path.endsWith(ApiConfig.APPROVE_REQUEST) || path.endsWith(ApiConfig.REJECT_REQUEST)) {
            return MockDataProvider.approveSuccess();
        }

        // ──────── LATE-EARLY REPORT ────────
        if (path.endsWith(ApiConfig.LATE_EARLY_REPORT)) {
            return MockDataProvider.lateEarlyReport();
        }

        // ──────── DAILY REPORT ────────
        if (path.endsWith(ApiConfig.DAILY_REPORT)) {
            return MockDataProvider.dailyReport();
        }

        // ──────── WEEKLY REPORT ────────
        if (path.endsWith(ApiConfig.WEEKLY_REPORT)) {
            return MockDataProvider.weeklyReport();
        }

        // ──────── MONTHLY OVERTIME ────────
        if (path.endsWith(ApiConfig.MONTHLY_OVERTIME)) {
            String month = "2026-02";
            if (query != null && query.contains("month=")) {
                month = extractParam(query, "month");
            }
            return MockDataProvider.monthlyOvertime(month);
        }

        // ──────── LEAVE REQUEST (POST) ────────
        if (path.endsWith(ApiConfig.LEAVE_REQUEST) && "POST".equals(method)) {
            return MockDataProvider.submitSuccess();
        }

        // ──────── LEAVE HISTORY ────────
        if (path.endsWith(ApiConfig.LEAVE_HISTORY)) {
            return MockDataProvider.leaveHistory();
        }

        // ──────── ADVANCE REQUEST (POST) ────────
        if (path.endsWith(ApiConfig.ADVANCE_REQUEST) && "POST".equals(method)) {
            return MockDataProvider.submitSuccess();
        }

        // ──────── ADVANCE HISTORY ────────
        if (path.endsWith(ApiConfig.ADVANCE_HISTORY)) {
            return MockDataProvider.advanceHistory();
        }

        // ──────── CHECK IN/OUT ────────
        if (path.endsWith(ApiConfig.CHECK_IN_OUT) || path.endsWith(ApiConfig.QR_CHECK_IN_OUT)) {
            nextIsCheckOut = !nextIsCheckOut;
            if (nextIsCheckOut) {
                return MockDataProvider.checkOutSuccess();
            } else {
                return MockDataProvider.checkInSuccess();
            }
        }

        // ──────── DEFAULT ────────
        Log.w(TAG, "Eşleşmeyen endpoint: " + path);
        return "{\"success\": true, \"message\": \"Mock default response\"}";
    }


    private String extractParam(String query, String key) {
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equals(key)) {
                return kv[1];
            }
        }
        return "";
    }
}