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

        Log.d(TAG, method + " " + path + (query != null ? "?" + query : ""));

        // 300ms yapay gecikme — gerçek ağ hissi
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {}

        String responseBody = routeRequest(path, query, method);

        return new Response.Builder()
                .code(200)
                .message("OK (Mock)")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(JSON, responseBody))
                .addHeader("Content-Type", "application/json")
                .build();
    }

    private String routeRequest(String path, String query, String method) {

        // ──────── AUTH ────────
        if (path.contains(ApiConfig.LOGIN)) {
            // Basit kural: card_no "0000" ile biterse hata, yoksa başarılı
            // POST body'yi okuyamadığımız için query veya path'e bakmıyoruz
            // Patron/Personel ayrımı header'dan veya body'den gelecekti
            // Mock'ta basitçe: path'e göre patron login dönelim
            // LoginActivity moduleType'ı body'de gönderiyor
            // Mock'ta ikisini de başarılı döndürüyoruz — session manager modülü ayırt edecek
            return MockDataProvider.loginPersonelSuccess();
        }

        // ──────── DEPARTMENTS ────────
        if (path.contains(ApiConfig.DEPARTMENT_LIST)) {
            return MockDataProvider.departments();
        }

        // ──────── DASHBOARD SUMMARY ────────
        if (path.contains(ApiConfig.DASHBOARD_SUMMARY)) {
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

// ──────── LEAVE REQUESTS (with status filter) ────────
        if (path.contains(ApiConfig.PENDING_LEAVE_REQUESTS)) {
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
        if (path.contains(ApiConfig.PENDING_ADVANCE_REQUESTS)) {
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
        if (path.contains(ApiConfig.APPROVE_REQUEST) || path.contains(ApiConfig.REJECT_REQUEST)) {
            return MockDataProvider.approveSuccess();
        }

        // ──────── LATE-EARLY REPORT ────────
        if (path.contains(ApiConfig.LATE_EARLY_REPORT)) {
            return MockDataProvider.lateEarlyReport();
        }

        // ──────── DAILY REPORT ────────
        if (path.contains(ApiConfig.DAILY_REPORT)) {
            return MockDataProvider.dailyReport();
        }

        // ──────── WEEKLY REPORT ────────
        if (path.contains(ApiConfig.WEEKLY_REPORT)) {
            return MockDataProvider.weeklyReport();
        }

        // ──────── MONTHLY OVERTIME ────────
        if (path.contains(ApiConfig.MONTHLY_OVERTIME)) {
            String month = "2026-02";
            if (query != null && query.contains("month=")) {
                month = extractParam(query, "month");
            }
            return MockDataProvider.monthlyOvertime(month);
        }

        // ──────── LEAVE REQUEST (POST) ────────
        if (path.contains(ApiConfig.LEAVE_REQUEST) && "POST".equals(method)) {
            return MockDataProvider.submitSuccess();
        }

        // ──────── LEAVE HISTORY ────────
        if (path.contains(ApiConfig.LEAVE_HISTORY)) {
            return MockDataProvider.leaveHistory();
        }

        // ──────── ADVANCE REQUEST (POST) ────────
        if (path.contains(ApiConfig.ADVANCE_REQUEST) && "POST".equals(method)) {
            return MockDataProvider.submitSuccess();
        }

        // ──────── ADVANCE HISTORY ────────
        if (path.contains(ApiConfig.ADVANCE_HISTORY)) {
            return MockDataProvider.advanceHistory();
        }

        // ──────── CHECK IN/OUT ────────
        if (path.contains(ApiConfig.CHECK_IN_OUT) || path.contains(ApiConfig.QR_CHECK_IN_OUT)) {
            nextIsCheckOut = !nextIsCheckOut;
            if (nextIsCheckOut) {
                return MockDataProvider.checkOutSuccess();
            } else {
                return MockDataProvider.checkInSuccess();
            }
        }
        // ──────── PERSONNEL LIST ────────
        if (path.contains(ApiConfig.PERSONNEL_LIST)) {
            return MockDataProvider.personnelList();
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