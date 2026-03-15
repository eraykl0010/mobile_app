package com.pdks.mobile.patron;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.constants.OvertimeType;
import com.pdks.mobile.databinding.ActivityLateEarlyListBinding;
import com.pdks.mobile.model.LateEarlyRecord;
import com.pdks.mobile.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class LateEarlyListActivity extends AppCompatActivity {

    private ActivityLateEarlyListBinding binding;
    private LateEarlyAdapter adapter;

    private List<LateEarlyRecord> allRecords = new ArrayList<>();
    private String currentFilter = OvertimeType.OVERTIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLateEarlyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewUtils.applyStatusBarPadding(this);

        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((android.widget.TextView) findViewById(R.id.tvToolbarTitle))
                .setText(getString(R.string.title_late_early));

        adapter = new LateEarlyAdapter();
        binding.rvLateEarly.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLateEarly.setAdapter(adapter);

        // 2 Tab
        binding.tabLateEarly.addTab(binding.tabLateEarly.newTab().setText(getString(R.string.tab_overtime)));
        binding.tabLateEarly.addTab(binding.tabLateEarly.newTab().setText(getString(R.string.tab_undertime)));

        binding.tabLateEarly.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = tab.getPosition() == 0 ? OvertimeType.OVERTIME : OvertimeType.UNDERTIME;
                filterAndShow();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadData();
    }

    private void loadData() {
        binding.progressLateEarly.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getLateEarlyReport(null).enqueue(
                new BaseApiCallback<List<LateEarlyRecord>>(this) {
                    @Override
                    public void onSuccess(@NonNull List<LateEarlyRecord> data) {
                        allRecords = data;
                        filterAndShow();
                    }

                    @Override
                    public void onFinally() {
                        binding.progressLateEarly.setVisibility(View.GONE);
                    }
                });
    }

    private void filterAndShow() {
        List<LateEarlyRecord> filtered = new ArrayList<>();
        for (LateEarlyRecord r : allRecords) {
            if (currentFilter.equals(r.getType())) {
                filtered.add(r);
            }
        }

        if (filtered.isEmpty()) {
            binding.tvEmptyLateEarly.setVisibility(View.VISIBLE);
            binding.tvEmptyLateEarly.setText(
                    OvertimeType.OVERTIME.equals(currentFilter)
                            ? getString(R.string.empty_overtime)
                            : getString(R.string.empty_undertime));
            binding.rvLateEarly.setVisibility(View.GONE);
        } else {
            binding.tvEmptyLateEarly.setVisibility(View.GONE);
            binding.rvLateEarly.setVisibility(View.VISIBLE);
            adapter.setItems(filtered);
        }

        // Tab sayıları
        int overtimeCount = 0, undertimeCount = 0;
        for (LateEarlyRecord r : allRecords) {
            if (OvertimeType.OVERTIME.equals(r.getType())) overtimeCount++;
            else undertimeCount++;
        }

        TabLayout.Tab tab0 = binding.tabLateEarly.getTabAt(0);
        TabLayout.Tab tab1 = binding.tabLateEarly.getTabAt(1);
        if (tab0 != null) tab0.setText(getString(R.string.tab_overtime_count, overtimeCount));
        if (tab1 != null) tab1.setText(getString(R.string.tab_undertime_count, undertimeCount));
    }
}