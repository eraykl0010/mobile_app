package com.pdks.mobile.api;

public class ApiConfig {

    // ══════════════════════════════════════════════════════════════
    // BASE URL — Ngrok üzerinden internete açık API adresi
    // ══════════════════════════════════════════════════════════════
    public static final String BASE_URL = "https://onlinepdks.com.tr/mobile/api/";

    // ══════════════════════════════════════════════════════════════
    // AUTH — Giriş & Cihaz Yönetimi
    // ══════════════════════════════════════════════════════════════
    // POST /api/login
    //   Body: { company_code, card_no, device_id, device_model, module_type, mac_address }
    //   Döner: { success, message, token, personnel_id, personnel_name, is_patron, department }
    //   SP: dbo.sp_Login
    public static final String LOGIN = "login";

    // POST /api/auth/reset-device
    //   Body: { personnel_id, reset_by }
    //   Döner: { success, message }
    //   SP: dbo.sp_ResetDevice
    public static final String RESET_DEVICE = "auth/reset-device";

    // ══════════════════════════════════════════════════════════════
    // PATRON — Dashboard & Yönetim
    // ══════════════════════════════════════════════════════════════
    // GET /api/patron/dashboard-summary?department_id=X
    //   Döner: { active_count, total_count, on_leave_count, absent_count, late_count, early_leave_count, department_name }
    //   SP: dbo.sp_GetDashboardSummary
    public static final String DASHBOARD_SUMMARY = "patron/dashboard-summary";

    // GET /api/patron/personnel-list?department_id=X
    //   Döner: [{ id, name, department, check_in, check_out, status }]
    //   SP: dbo.sp_GetPersonnelList
    public static final String PERSONNEL_LIST = "patron/personnel-list";

    // GET /api/departments
    //   Döner: [{ id, name }]
    //   SP: dbo.sp_GetDepartments
    public static final String DEPARTMENT_LIST = "departments";

    // GET /api/patron/pending-leave-requests?type=yillik&status=pending
    //   Döner: [{ id, personnel_name, department, leave_type, start_date, end_date, ... }]
    //   SP: dbo.sp_GetLeaveRequests
    public static final String PENDING_LEAVE_REQUESTS = "patron/pending-leave-requests";

    // GET /api/patron/pending-advance-requests?status=pending
    //   Döner: [{ id, personnel_name, department, amount, reason, status, request_date }]
    //   SP: dbo.sp_GetAdvanceRequests
    public static final String PENDING_ADVANCE_REQUESTS = "patron/pending-advance-requests";

    // POST /api/patron/approve-request
    //   Body: { request_id, request_type, action, note }
    //   Döner: { success, message }
    //   SP: dbo.sp_ProcessApproval
    public static final String APPROVE_REQUEST = "patron/approve-request";

    // POST /api/patron/reject-request
    //   Body: { request_id, request_type, action, note }
    //   Döner: { success, message }
    //   SP: dbo.sp_ProcessApproval
    public static final String REJECT_REQUEST = "patron/reject-request";

    // GET /api/patron/late-early-report?date=2026-02-26
    //   Döner: [{ personnel_name, department, type, scheduled_time, actual_time, difference_minutes, date }]
    //   SP: dbo.sp_GetOvertimeReport
    public static final String LATE_EARLY_REPORT = "patron/late-early-report";

    // ══════════════════════════════════════════════════════════════
    // PERSONEL — Raporlar & Talepler
    // ══════════════════════════════════════════════════════════════
    // GET /api/personel/daily-report?personnel_id=42
    //   Döner: [{ date, day_name, check_in, check_out, work_hours, overtime_hours, status }]
    //   SP: dbo.sp_GetDailyReport
    public static final String DAILY_REPORT = "personel/daily-report";

    // GET /api/personel/weekly-report?personnel_id=42
    //   Döner: [{ date, day_name, check_in, check_out, work_hours, overtime_hours, status }]
    //   SP: dbo.sp_GetWeeklyReport
    public static final String WEEKLY_REPORT = "personel/weekly-report";

    // GET /api/personel/monthly-overtime?personnel_id=42&month=2026-02
    //   Döner: { month, total_work_hours, total_overtime_hours, total_work_days, absent_days, late_count, early_leave_count }
    //   SP: dbo.sp_GetMonthlyOvertime
    public static final String MONTHLY_OVERTIME = "personel/monthly-overtime";

    // POST /api/personel/submit-leave-request
    //   Body: { personnel_id, leave_type, start_date, end_date, start_time, end_time, reason }
    //   Döner: { success, message }
    //   SP: dbo.sp_CreateLeaveRequest
    public static final String LEAVE_REQUEST = "personel/submit-leave-request";

    // GET /api/personel/leave-history?personnel_id=42
    //   Döner: [{ id, personnel_name, department, leave_type, start_date, end_date, ..., remaining_days }]
    //   SP: dbo.sp_GetLeaveHistory
    public static final String LEAVE_HISTORY = "personel/leave-history";

    // POST /api/personel/advance-request
    //   Body: { personnel_id, amount, reason }
    //   Döner: { success, message }
    //   SP: dbo.sp_CreateAdvanceRequest
    public static final String ADVANCE_REQUEST = "personel/advance-request";

    // GET /api/personel/advance-history?personnel_id=42
    //   Döner: [{ id, personnel_name, department, amount, reason, status, request_date }]
    //   SP: dbo.sp_GetAdvanceHistory
    public static final String ADVANCE_HISTORY = "personel/advance-history";

    // POST /api/personel/check-in-out
    //   Body: { personnel_id, latitude, longitude, qr_code, type, device_id }
    //   Döner: { success, message, action, time }
    //   SP: dbo.sp_CheckInOut
    public static final String CHECK_IN_OUT = "personel/check-in-out";

    // POST /api/personel/qr-check-in-out
    //   Body: { personnel_id, latitude, longitude, qr_code, type, device_id }
    //   Döner: { success, message, action, time }
    //   SP: dbo.sp_QrCheckInOut
    public static final String QR_CHECK_IN_OUT = "personel/qr-check-in-out";
}