package com.pdks.mobile.api;

import com.pdks.mobile.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // ==================== AUTH ====================

    @POST(ApiConfig.LOGIN)
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST(ApiConfig.RESET_DEVICE)
    Call<ApiResponse> resetDevice(@Body ResetDeviceRequest request);

    // ==================== PATRON ====================

    @GET(ApiConfig.DASHBOARD_SUMMARY)
    Call<DashboardSummary> getDashboardSummary(
            @Query("department_id") Integer departmentId
    );

    @GET(ApiConfig.PERSONNEL_LIST)
    Call<List<PersonnelInfo>> getPersonnelList(
            @Query("department_id") Integer departmentId
    );

    @GET(ApiConfig.DEPARTMENT_LIST)
    Call<List<Department>> getDepartments();

    @GET(ApiConfig.PENDING_LEAVE_REQUESTS)
    Call<List<LeaveRequest>> getPendingLeaveRequests(
            @Query("type") String leaveType,
            @Query("status") String status
    );

    @GET(ApiConfig.PENDING_ADVANCE_REQUESTS)
    Call<List<AdvanceRequest>> getPendingAdvanceRequests(
            @Query("status") String status
    );

    @POST(ApiConfig.APPROVE_REQUEST)
    Call<ApiResponse> approveRequest(@Body ApprovalRequest request);

    @POST(ApiConfig.REJECT_REQUEST)
    Call<ApiResponse> rejectRequest(@Body ApprovalRequest request);

    @GET(ApiConfig.LATE_EARLY_REPORT)
    Call<List<LateEarlyRecord>> getLateEarlyReport(
            @Query("date") String date // yyyy-MM-dd — null ise dün
    );

    // ==================== PERSONEL ====================

    @GET(ApiConfig.DAILY_REPORT)
    Call<List<AttendanceRecord>> getDailyReport(
            @Query("personnel_id") int personnelId
    );

    @GET(ApiConfig.WEEKLY_REPORT)
    Call<List<AttendanceRecord>> getWeeklyReport(
            @Query("personnel_id") int personnelId
    );

    @GET(ApiConfig.MONTHLY_OVERTIME)
    Call<MonthlyOvertime> getMonthlyOvertime(
            @Query("personnel_id") int personnelId,
            @Query("month") String month // yyyy-MM
    );

    @POST(ApiConfig.LEAVE_REQUEST)
    Call<ApiResponse> submitLeaveRequest(@Body LeaveSubmitRequest request);

    @GET(ApiConfig.LEAVE_HISTORY)
    Call<List<LeaveRequest>> getLeaveHistory(
            @Query("personnel_id") int personnelId
    );

    @POST(ApiConfig.ADVANCE_REQUEST)
    Call<ApiResponse> submitAdvanceRequest(@Body AdvanceSubmitRequest request);

    @GET(ApiConfig.ADVANCE_HISTORY)
    Call<List<AdvanceRequest>> getAdvanceHistory(
            @Query("personnel_id") int personnelId
    );

    @POST(ApiConfig.CHECK_IN_OUT)
    Call<CheckInOutResponse> checkInOut(@Body CheckInOutRequest request);

    @POST(ApiConfig.QR_CHECK_IN_OUT)
    Call<CheckInOutResponse> qrCheckInOut(@Body CheckInOutRequest request);
}