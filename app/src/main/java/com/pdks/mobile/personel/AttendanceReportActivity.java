package com.pdks.mobile.personel;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.AttendanceRecord;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceReportActivity extends AppCompatActivity {

    private RecyclerView rvAttendance;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private AttendanceAdapter adapter;
    private ApiService apiService;
    private int personnelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_report);

        // Toolbar
        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText("Giriş-Çıkış Raporu");

        tabLayout = findViewById(R.id.tabLayout);
        rvAttendance = findViewById(R.id.rvAttendance);
        progressBar = findViewById(R.id.progressReport);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        personnelId = new SessionManager(this).getPersonnelId();

        adapter = new AttendanceAdapter();
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        rvAttendance.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Günlük"));
        tabLayout.addTab(tabLayout.newTab().setText("Haftalık"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadDailyReport();
                } else {
                    loadWeeklyReport();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadDailyReport();
        ViewUtils.applyStatusBarPadding(this);
    }

    private void loadDailyReport() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getDailyReport(personnelId).enqueue(new AttendanceCallback());
    }

    private void loadWeeklyReport() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getWeeklyReport(personnelId).enqueue(new AttendanceCallback());
    }

    private class AttendanceCallback implements Callback<List<AttendanceRecord>> {
        @Override
        public void onResponse(Call<List<AttendanceRecord>> call, Response<List<AttendanceRecord>> resp) {
            progressBar.setVisibility(View.GONE);
            if (resp.isSuccessful() && resp.body() != null) {
                adapter.setItems(resp.body());
            }
        }

        @Override
        public void onFailure(Call<List<AttendanceRecord>> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(AttendanceReportActivity.this,
                    "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}