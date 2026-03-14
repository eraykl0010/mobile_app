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

    private static final int DASHBOARD_REFRESH_CALL_COUNT = 6;

    private ActivityPatronDashboardBinding binding;
    private SessionManager sessionManager;
    private ApiService apiService;

    private List<Department> departments = new ArrayList<>();
    private Integer selectedDepartmentId = null;
    private int selectedDepartmentSpinnerPosition = 0;

    private int pendingRefreshCalls = 0;
    private boolean isFirstResume = true;

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
        setupSwipeRefresh();

        loadDepartments();
        refreshDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstResume) {
            isFirstResume = false;
            return;
        }

        refreshDashboard();
    }

    private void setupUI() {
        String name = sessionManager.getPersonnelName();
        binding.tvWelcome.setText("Hoş geldiniz, " + name);
    }

    private void setupClickListeners() {
        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Çıkış")
                    .setMessage("Oturumu kapatmak istediğinize emin misiniz?")
                    .setPositiveButton("Evet", (d, w) -> {
                        sessionManager.logoutPatron();
                        RetrofitClient.resetClient();

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("İptal", null)
                    .show();
        });

        binding.cardPersonnelInfo.setOnClickListener(
                v -> startActivity(new Intent(this, PersonnelDetailActivity.class))
        );

        binding.cardAnnualLeave.setOnClickListener(v -> openApproval("yillik"));
        binding.cardDailyLeave.setOnClickListener(v -> openApproval("gunluk"));
        binding.cardHourlyLeave.setOnClickListener(v -> openApproval("saatlik"));
        binding.cardAdvance.setOnClickListener(v -> openApproval("avans"));

        binding.cardLateEarly.setOnClickListener(
                v -> startActivity(new Intent(this, LateEarlyListActivity.class))
        );
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshPatron.setColorSchemeResources(R.color.primary);

        binding.swipeRefreshPatron.setOnChildScrollUpCallback(
                (parent, child) -> binding.scrollPatronContent.canScrollVertically(-1)
        );

        binding.swipeRefreshPatron.setOnRefreshListener(this::refreshDashboard);
    }

    private void openApproval(String type) {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void refreshDashboard() {
        beginRefresh(DASHBOARD_REFRESH_CALL_COUNT);

        loadDashboardSummary(selectedDepartmentId, true);
        loadOvertimeSummary(true);
        loadAnnualLeaveCount(true);
        loadDailyLeaveCount(true);
        loadHourlyLeaveCount(true);
        loadAdvanceCount(true);

        loadDepartments();
    }

    private void beginRefresh(int requestCount) {
        pendingRefreshCalls = requestCount;
        binding.swipeRefreshPatron.setRefreshing(true);
    }

    private void finishRefreshRequest() {
        if (pendingRefreshCalls > 0) {
            pendingRefreshCalls--;
        }

        if (pendingRefreshCalls == 0) {
            binding.swipeRefreshPatron.setRefreshing(false);
        }
    }

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new BaseApiCallback<List<Department>>(this) {
            @Override
            public void onSuccess(@NonNull List<Department> data) {
                departments = data;
                setupDepartmentSpinner();
            }

            @Override
            public void onNetworkError(@NonNull Throwable t) {
                setupDepartmentSpinner();
            }
        });
    }

    private void setupDepartmentSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Tüm Departmanlar");

        for (Department d : departments) {
            items.add(d.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerDepartment.setOnItemSelectedListener(null);
        binding.spinnerDepartment.setAdapter(adapter);

        if (selectedDepartmentSpinnerPosition > departments.size()) {
            selectedDepartmentSpinnerPosition = 0;
            selectedDepartmentId = null;
        }

        binding.spinnerDepartment.setSelection(selectedDepartmentSpinnerPosition, false);
        attachDepartmentSpinnerListener();
    }

    private void attachDepartmentSpinnerListener() {
        binding.spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartmentSpinnerPosition = position;
                selectedDepartmentId = (position == 0)
                        ? null
                        : departments.get(position - 1).getId();

                loadDashboardSummary(selectedDepartmentId, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadDashboardSummary(Integer departmentId, boolean trackRefresh) {
        apiService.getDashboardSummary(departmentId).enqueue(
                new BaseApiCallback<DashboardSummary>(this) {
                    @Override
                    public void onSuccess(@NonNull DashboardSummary data) {
                        updateSummaryUI(data);
                    }

                    @Override
                    public void onFinally() {
                        if (trackRefresh) {
                            finishRefreshRequest();
                        }
                    }
                }
        );
    }

    private void updateSummaryUI(DashboardSummary summary) {
        binding.tvActiveCount.setText(String.valueOf(summary.getActiveCount()));
        binding.tvTotalCount.setText(String.valueOf(summary.getTotalCount()));
        binding.tvOnLeaveCount.setText(String.valueOf(summary.getOnLeaveCount()));
        binding.tvAbsentCount.setText(String.valueOf(summary.getAbsentCount()));
        binding.tvLateCount.setText(String.valueOf(summary.getLateCount()));
        binding.tvEarlyLeaveCount.setText(String.valueOf(summary.getEarlyLeaveCount()));
    }

    private void loadOvertimeSummary(boolean trackRefresh) {
        apiService.getLateEarlyReport(null).enqueue(
                new BaseApiCallback<List<LateEarlyRecord>>(this) {
                    @Override
                    public void onSuccess(@NonNull List<LateEarlyRecord> data) {
                        int overtimeCount = 0;
                        int undertimeCount = 0;

                        for (LateEarlyRecord r : data) {
                            if ("overtime".equals(r.getType())) {
                                overtimeCount++;
                            } else {
                                undertimeCount++;
                            }
                        }

                        binding.tvOvertimeCount.setText(String.valueOf(overtimeCount));
                        binding.tvUndertimeCount.setText(String.valueOf(undertimeCount));
                    }

                    @Override
                    public void onFinally() {
                        if (trackRefresh) {
                            finishRefreshRequest();
                        }
                    }
                }
        );
    }

    private void loadAnnualLeaveCount(boolean trackRefresh) {
        apiService.getPendingLeaveRequests("yillik", "pending").enqueue(
                new BaseApiCallback<List<LeaveRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        binding.tvAnnualLeaveCount.setText(String.valueOf(data.size()));
                    }

                    @Override
                    public void onFinally() {
                        if (trackRefresh) {
                            finishRefreshRequest();
                        }
                    }
                }
        );
    }

    private void loadDailyLeaveCount(boolean trackRefresh) {
        apiService.getPendingLeaveRequests("gunluk", "pending").enqueue(
                new BaseApiCallback<List<LeaveRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        binding.tvDailyLeaveCount.setText(String.valueOf(data.size()));
                    }

                    @Override
                    public void onFinally() {
                        if (trackRefresh) {
                            finishRefreshRequest();
                        }
                    }
                }
        );
    }

    private void loadHourlyLeaveCount(boolean trackRefresh) {
        apiService.getPendingLeaveRequests("saatlik", "pending").enqueue(
                new BaseApiCallback<List<LeaveRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<LeaveRequest> data) {
                        binding.tvHourlyLeaveCount.setText(String.valueOf(data.size()));
                    }

                    @Override
                    public void onFinally() {
                        if (trackRefresh) {
                            finishRefreshRequest();
                        }
                    }
                }
        );
    }

    private void loadAdvanceCount(boolean trackRefresh) {
        apiService.getPendingAdvanceRequests("pending").enqueue(
                new BaseApiCallback<List<AdvanceRequest>>(null) {
                    @Override
                    public void onSuccess(@NonNull List<AdvanceRequest> data) {
                        binding.tvAdvanceCount.setText(String.valueOf(data.size()));
                    }

                    @Override
                    public void onFinally() {
                        if (trackRefresh) {
                            finishRefreshRequest();
                        }
                    }
                }
        );
    }
}