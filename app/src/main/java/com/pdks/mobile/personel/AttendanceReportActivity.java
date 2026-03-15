package com.pdks.mobile.personel;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.AttendanceRecord;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

import java.util.List;

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
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.title_attendance_report));

        tabLayout = findViewById(R.id.tabLayout);
        rvAttendance = findViewById(R.id.rvAttendance);
        progressBar = findViewById(R.id.progressReport);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        personnelId = new SessionManager(this).getPersonnelId();

        adapter = new AttendanceAdapter();
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        rvAttendance.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_daily)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_weekly)));

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
        apiService.getDailyReport(personnelId).enqueue(newAttendanceCallback());
    }

    private void loadWeeklyReport() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getWeeklyReport(personnelId).enqueue(newAttendanceCallback());
    }

    private BaseApiCallback<List<AttendanceRecord>> newAttendanceCallback() {
        return new BaseApiCallback<List<AttendanceRecord>>(this) {
            @Override
            public void onSuccess(@NonNull List<AttendanceRecord> data) {
                adapter.setItems(data);
            }

            @Override
            public void onFinally() {
                progressBar.setVisibility(View.GONE);
            }
        };
    }
}