package com.pdks.mobile.patron;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pdks.mobile.MainActivity;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.constants.LeaveType;
import com.pdks.mobile.constants.OvertimeType;
import com.pdks.mobile.constants.RequestStatus;
import com.pdks.mobile.databinding.ActivityPatronDashboardBinding;
import com.pdks.mobile.model.AdvanceRequest;
import com.pdks.mobile.model.DashboardSummary;
import com.pdks.mobile.model.Department;
import com.pdks.mobile.model.LateEarlyRecord;
import com.pdks.mobile.model.LeaveRequest;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class PatronDashboardActivity extends AppCompatActivity {

    private ActivityPatronDashboardBinding binding;
    private SessionManager sessionManager;
    private ApiService apiService;
    private List<Department> departments = new ArrayList<>();
    private Integer selectedDepartmentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPatronDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewUtils.applyStatusBarPadding(this);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        setupUI();
        setupClickListeners();
        loadDepartments();
        loadDashboardSummary(null);
        loadOvertimeSummary();
        loadApprovalCounts();
    }

    private void setupUI() {
        String name = sessionManager.getPersonnelName();
        binding.tvWelcome.setText(getString(R.string.welcome_message, name));
    }

    private void setupClickListeners() {
        // Çıkış
        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logout_title))
                    .setMessage(getString(R.string.logout_message))
                    .setPositiveButton(getString(R.string.btn_yes), (d, w) -> {
                        sessionManager.logoutPatron();
                        RetrofitClient.resetClient();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton(getString(R.string.btn_cancel), null)
                    .show();
        });

        // Personel Bilgi Kartı
        binding.cardPersonnelInfo.setOnClickListener(v ->
                startActivity(new Intent(this, PersonnelDetailActivity.class)));

        // Onay Menüleri — 4 adet
        binding.cardAnnualLeave.setOnClickListener(v -> openApproval(LeaveType.ANNUAL));
        binding.cardDailyLeave.setOnClickListener(v -> openApproval(LeaveType.DAILY));
        binding.cardHourlyLeave.setOnClickListener(v -> openApproval(LeaveType.HOURLY));
        binding.cardAdvance.setOnClickListener(v -> openApproval(LeaveType.ADVANCE));

        // Fazla Mesai / Eksik Mesai
        binding.cardLateEarly.setOnClickListener(v ->
                startActivity(new Intent(this, LateEarlyListActivity.class)));

        // Departman filtre
        binding.spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartmentId = (position == 0) ? null : departments.get(position - 1).getId();
                loadDashboardSummary(selectedDepartmentId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void openApproval(String type) {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    // ── Departmanlar ──

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new BaseApiCallback<List<Department>>(this) {
            @Override
            public void onSuccess(@NonNull List<Department> data) {
                departments = data;
                setupDepartmentSpinner();
            }

            @Override
            public void onNetworkError(@NonNull Throwable t) {
                // Departman yüklenemese bile spinner boş açılsın
                setupDepartmentSpinner();
            }
        });
    }

    private void setupDepartmentSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Tüm Departmanlar");
        for (Department d : departments) items.add(d.getName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDepartment.setAdapter(adapter);
    }

    // ── Dashboard Özet ──

    private void loadDashboardSummary(Integer departmentId) {
        apiService.getDashboardSummary(departmentId).enqueue(
                new BaseApiCallback<DashboardSummary>(this) {
                    @Override
                    public void onSuccess(@NonNull DashboardSummary data) {
                        updateSummaryUI(data);
                    }
                });
    }

    private void updateSummaryUI(DashboardSummary summary) {
        binding.tvActiveCount.setText(String.valueOf(summary.getActiveCount()));
        binding.tvTotalCount.setText(String.valueOf(summary.getTotalCount()));
        binding.tvOnLeaveCount.setText(String.valueOf(summary.getOnLeaveCount()));
        binding.tvAbsentCount.setText(String.valueOf(summary.getAbsentCount()));
        binding.tvLateCount.setText(String.valueOf(summary.getLateCount()));
        binding.tvEarlyLeaveCount.setText(String.valueOf(summary.getEarlyLeaveCount()));
    }

    // ── Fazla / Eksik Mesai Sayıları ──

    private void loadOvertimeSummary() {
        apiService.getLateEarlyReport(null).enqueue(
                new BaseApiCallback<List<LateEarlyRecord>>(this) {
                    @Override
                    public void onSuccess(@NonNull List<LateEarlyRecord> data) {
                        int overtimeCount = 0;
                        int undertimeCount = 0;
                        for (LateEarlyRecord r : data) {
                            if (OvertimeType.OVERTIME.equals(r.getType())) overtimeCount++;
                            else undertimeCount++;
                        }
                        binding.tvOvertimeCount.setText(String.valueOf(overtimeCount));
                        binding.tvUndertimeCount.setText(String.valueOf(undertimeCount));
                    }
                });
    }

    // ── Onay Sayıları (4 adet) ──

    private void loadApprovalCounts() {
        // Yıllık
        apiService.getPendingLeaveRequests(LeaveType.ANNUAL, RequestStatus.PENDING).enqueue(
                new BaseApiCallback<List<LeaveRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        binding.tvAnnualLeaveCount.setText(String.valueOf(data.size()));
                    }
                });

        // Günlük
        apiService.getPendingLeaveRequests(LeaveType.DAILY, RequestStatus.PENDING).enqueue(
                new BaseApiCallback<List<LeaveRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        binding.tvDailyLeaveCount.setText(String.valueOf(data.size()));
                    }
                });

        // Saatlik
        apiService.getPendingLeaveRequests(LeaveType.HOURLY, RequestStatus.PENDING).enqueue(
                new BaseApiCallback<List<LeaveRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        binding.tvHourlyLeaveCount.setText(String.valueOf(data.size()));
                    }
                });

        // Avans
        apiService.getPendingAdvanceRequests(RequestStatus.PENDING).enqueue(
                new BaseApiCallback<List<AdvanceRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<AdvanceRequest> data) {
                        binding.tvAdvanceCount.setText(String.valueOf(data.size()));
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardSummary(selectedDepartmentId);
        loadOvertimeSummary();
        loadApprovalCounts();
    }
}