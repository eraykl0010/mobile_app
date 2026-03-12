package com.pdks.mobile.patron;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.databinding.ActivityLateEarlyListBinding;
import com.pdks.mobile.model.LateEarlyRecord;
import com.pdks.mobile.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LateEarlyListActivity extends AppCompatActivity {

    private ActivityLateEarlyListBinding binding;
    private LateEarlyAdapter adapter;

    private List<LateEarlyRecord> allRecords = new ArrayList<>();
    private String currentFilter = "overtime"; // "overtime" veya "undertime"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLateEarlyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewUtils.applyStatusBarPadding(this);

        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((android.widget.TextView) findViewById(R.id.tvToolbarTitle))
                .setText("Fazla Mesai / Eksik Mesai");

        adapter = new LateEarlyAdapter();
        binding.rvLateEarly.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLateEarly.setAdapter(adapter);

        // 2 Tab
        binding.tabLateEarly.addTab(binding.tabLateEarly.newTab().setText("Fazla Mesai"));
        binding.tabLateEarly.addTab(binding.tabLateEarly.newTab().setText("Eksik Mesai"));

        binding.tabLateEarly.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = tab.getPosition() == 0 ? "overtime" : "undertime";
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
        apiService.getLateEarlyReport(null).enqueue(new Callback<List<LateEarlyRecord>>() {
            @Override
            public void onResponse(Call<List<LateEarlyRecord>> call,
                                   Response<List<LateEarlyRecord>> resp) {
                binding.progressLateEarly.setVisibility(View.GONE);
                if (resp.isSuccessful() && resp.body() != null) {
                    allRecords = resp.body();
                    filterAndShow();
                }
            }

            @Override
            public void onFailure(Call<List<LateEarlyRecord>> call, Throwable t) {
                binding.progressLateEarly.setVisibility(View.GONE);
                Toast.makeText(LateEarlyListActivity.this,
                        "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    "overtime".equals(currentFilter)
                            ? "Dün fazla mesai yapan personel yok"
                            : "Dün eksik mesaisi olan personel yok");
            binding.rvLateEarly.setVisibility(View.GONE);
        } else {
            binding.tvEmptyLateEarly.setVisibility(View.GONE);
            binding.rvLateEarly.setVisibility(View.VISIBLE);
            adapter.setItems(filtered);
        }

        // Tab sayıları
        int overtimeCount = 0, undertimeCount = 0;
        for (LateEarlyRecord r : allRecords) {
            if ("overtime".equals(r.getType())) overtimeCount++;
            else undertimeCount++;
        }

        TabLayout.Tab tab0 = binding.tabLateEarly.getTabAt(0);
        TabLayout.Tab tab1 = binding.tabLateEarly.getTabAt(1);
        if (tab0 != null) tab0.setText("Fazla Mesai (" + overtimeCount + ")");
        if (tab1 != null) tab1.setText("Eksik Mesai (" + undertimeCount + ")");
    }
}