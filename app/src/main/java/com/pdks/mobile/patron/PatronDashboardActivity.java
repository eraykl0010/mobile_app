package com.pdks.mobile.patron;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pdks.mobile.MainActivity;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        binding.tvWelcome.setText("Hoş geldiniz, " + name);
    }

    private void setupClickListeners() {
        // Çıkış
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

        // Personel Bilgi Kartı
        binding.cardPersonnelInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, PersonnelDetailActivity.class));
        });

        // Onay Menüleri — 4 adet
        binding.cardAnnualLeave.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovalListActivity.class);
            intent.putExtra("type", "yillik");
            startActivity(intent);
        });

        binding.cardDailyLeave.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovalListActivity.class);
            intent.putExtra("type", "gunluk");
            startActivity(intent);
        });

        binding.cardHourlyLeave.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovalListActivity.class);
            intent.putExtra("type", "saatlik");
            startActivity(intent);
        });

        binding.cardAdvance.setOnClickListener(v -> {
            Intent intent = new Intent(this, ApprovalListActivity.class);
            intent.putExtra("type", "avans");
            startActivity(intent);
        });

        // Fazla Mesai / Eksik Mesai
        binding.cardLateEarly.setOnClickListener(v -> {
            startActivity(new Intent(this, LateEarlyListActivity.class));
        });

        // Departman filtre
        binding.spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedDepartmentId = null;
                } else {
                    selectedDepartmentId = departments.get(position - 1).getId();
                }
                loadDashboardSummary(selectedDepartmentId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ── Departmanlar ──

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    departments = response.body();
                    setupDepartmentSpinner();
                }
            }
            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {
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
        apiService.getDashboardSummary(departmentId).enqueue(new Callback<DashboardSummary>() {
            @Override
            public void onResponse(Call<DashboardSummary> call, Response<DashboardSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateSummaryUI(response.body());
                }
            }
            @Override
            public void onFailure(Call<DashboardSummary> call, Throwable t) {
                Toast.makeText(PatronDashboardActivity.this,
                        "Veri yüklenemedi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        apiService.getLateEarlyReport(null).enqueue(new Callback<List<LateEarlyRecord>>() {
            @Override
            public void onResponse(Call<List<LateEarlyRecord>> call,
                                   Response<List<LateEarlyRecord>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    int overtimeCount = 0;
                    int undertimeCount = 0;
                    for (LateEarlyRecord r : resp.body()) {
                        if ("overtime".equals(r.getType())) overtimeCount++;
                        else undertimeCount++;
                    }
                    binding.tvOvertimeCount.setText(String.valueOf(overtimeCount));
                    binding.tvUndertimeCount.setText(String.valueOf(undertimeCount));
                }
            }
            @Override
            public void onFailure(Call<List<LateEarlyRecord>> call, Throwable t) {}
        });
    }

    // ── Onay Sayıları (4 adet) ──

    private void loadApprovalCounts() {
        // Yıllık
        apiService.getPendingLeaveRequests("yillik", "pending")
                .enqueue(new Callback<List<LeaveRequest>>() {
                    @Override
                    public void onResponse(Call<List<LeaveRequest>> c, Response<List<LeaveRequest>> r) {
                        if (r.isSuccessful() && r.body() != null)
                            binding.tvAnnualLeaveCount.setText(String.valueOf(r.body().size()));
                    }
                    @Override public void onFailure(Call<List<LeaveRequest>> c, Throwable t) {}
                });

        // Günlük
        apiService.getPendingLeaveRequests("gunluk", "pending")
                .enqueue(new Callback<List<LeaveRequest>>() {
                    @Override
                    public void onResponse(Call<List<LeaveRequest>> c, Response<List<LeaveRequest>> r) {
                        if (r.isSuccessful() && r.body() != null)
                            binding.tvDailyLeaveCount.setText(String.valueOf(r.body().size()));
                    }
                    @Override public void onFailure(Call<List<LeaveRequest>> c, Throwable t) {}
                });

        // Saatlik
        apiService.getPendingLeaveRequests("saatlik", "pending")
                .enqueue(new Callback<List<LeaveRequest>>() {
                    @Override
                    public void onResponse(Call<List<LeaveRequest>> c, Response<List<LeaveRequest>> r) {
                        if (r.isSuccessful() && r.body() != null)
                            binding.tvHourlyLeaveCount.setText(String.valueOf(r.body().size()));
                    }
                    @Override public void onFailure(Call<List<LeaveRequest>> c, Throwable t) {}
                });

        // Avans
        apiService.getPendingAdvanceRequests("pending")
                .enqueue(new Callback<List<AdvanceRequest>>() {
                    @Override
                    public void onResponse(Call<List<AdvanceRequest>> c, Response<List<AdvanceRequest>> r) {
                        if (r.isSuccessful() && r.body() != null)
                            binding.tvAdvanceCount.setText(String.valueOf(r.body().size()));
                    }
                    @Override public void onFailure(Call<List<AdvanceRequest>> c, Throwable t) {}
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