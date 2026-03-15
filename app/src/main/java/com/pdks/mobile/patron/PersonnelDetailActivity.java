package com.pdks.mobile.patron;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.constants.PersonnelStatus;
import com.pdks.mobile.databinding.ActivityPersonnelDetailBinding;
import com.pdks.mobile.model.ApiResponse;
import com.pdks.mobile.model.DashboardSummary;
import com.pdks.mobile.model.Department;
import com.pdks.mobile.model.PersonnelInfo;
import com.pdks.mobile.model.ResetDeviceRequest;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class PersonnelDetailActivity extends AppCompatActivity {

    private static final String TAG = "PersonnelDetail";

    private ActivityPersonnelDetailBinding binding;
    private ApiService apiService;
    private SessionManager sessionManager;
    private PersonnelListAdapter adapter;
    private List<Department> departments = new ArrayList<>();

    // Hangi kutu seçili
    private View selectedBox = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPersonnelDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewUtils.applyStatusBarPadding(this);

        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((android.widget.TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.title_personnel_detail));

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        sessionManager = new SessionManager(this);

        adapter = new PersonnelListAdapter();
        binding.rvPersonnelList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPersonnelList.setAdapter(adapter);

        // ── Personele uzun basınca seçenek menüsü ──
        adapter.setOnItemLongClickListener((item, position) -> showPersonnelOptionsDialog(item));

        setupBoxClickListeners();
        setupClearFilter();
        loadDepartments();
        loadPersonnelList();
        loadSummary(null);
    }

    // ══════════════════════════════════════════════════
    //  CİHAZ SIFIRLAMA — Personele uzun basınca açılır
    // ══════════════════════════════════════════════════

    private void showPersonnelOptionsDialog(PersonnelInfo item) {
        String[] options = {getString(R.string.device_reset_option)};

        new AlertDialog.Builder(this)
                .setTitle(item.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        confirmResetDevice(item);
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void confirmResetDevice(PersonnelInfo item) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.device_reset_title))
                .setMessage(getString(R.string.device_reset_message, item.getName()))
                .setPositiveButton(getString(R.string.device_reset_button), (d, w) -> executeResetDevice(item))
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show();
    }

    private void executeResetDevice(PersonnelInfo item) {
        int patronId = sessionManager.getPersonnelId();
        ResetDeviceRequest req = new ResetDeviceRequest(item.getId(), patronId);

        apiService.resetDevice(req).enqueue(new BaseApiCallback<ApiResponse>(this) {
            @Override
            public void onSuccess(@NonNull ApiResponse data) {
                if (data.isSuccess()) {
                    Toast.makeText(PersonnelDetailActivity.this,
                            getString(R.string.device_reset_success, item.getName()),
                            Toast.LENGTH_SHORT).show();
                } else {
                    String msg = data.getMessage() != null ? data.getMessage() : getString(R.string.device_reset_failed);
                    Toast.makeText(PersonnelDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // ══════════════════════════════════════════════════
    //  KUTU FİLTRELEME
    // ══════════════════════════════════════════════════

    private void setupBoxClickListeners() {
        binding.boxActive.setOnClickListener(v -> applyStatusFilter(v, PersonnelStatus.ACTIVE, getString(R.string.filter_active)));
        binding.boxTotal.setOnClickListener(v -> {
            clearFilter();
        });
        binding.boxLeave.setOnClickListener(v -> applyStatusFilter(v, PersonnelStatus.ON_LEAVE, getString(R.string.filter_on_leave)));
        binding.boxAbsent.setOnClickListener(v -> applyStatusFilter(v, PersonnelStatus.ABSENT, getString(R.string.filter_absent)));
        binding.boxLate.setOnClickListener(v -> applyStatusFilter(v, PersonnelStatus.LATE, getString(R.string.filter_late)));
        binding.boxEarly.setOnClickListener(v -> applyStatusFilter(v, PersonnelStatus.EARLY, getString(R.string.filter_early)));
    }

    private void applyStatusFilter(View box, String status, String label) {
        if (selectedBox == box && status.equals(adapter.getCurrentStatus())) {
            clearFilter();
            return;
        }

        resetBoxHighlights();

        selectedBox = box;
        highlightBox(box, true);

        adapter.filterByStatus(status);

        binding.tvActiveFilter.setText(getString(R.string.filter_label, label));
        binding.layoutActiveFilter.setVisibility(View.VISIBLE);

        binding.tvListTitle.setText(label + " (" + adapter.getItemCount() + ")");
    }

    private void clearFilter() {
        resetBoxHighlights();
        selectedBox = null;
        adapter.clearStatusFilter();
        binding.layoutActiveFilter.setVisibility(View.GONE);
        binding.tvListTitle.setText(getString(R.string.personnel_list_title));
    }

    private void setupClearFilter() {
        binding.btnClearFilter.setOnClickListener(v -> clearFilter());
    }

    private void highlightBox(View box, boolean selected) {
        if (selected) {
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dpToPx(16));
            bg.setColor(ContextCompat.getColor(PersonnelDetailActivity.this, R.color.primary_very_light));
            bg.setStroke(dpToPx(2), ContextCompat.getColor(PersonnelDetailActivity.this, R.color.primary));
            box.setBackground(bg);
        } else {
            box.setBackgroundResource(R.drawable.bg_card_rounded);
        }
    }

    private void resetBoxHighlights() {
        binding.boxActive.setBackgroundResource(R.drawable.bg_card_rounded);
        binding.boxTotal.setBackgroundResource(R.drawable.bg_card_rounded);
        binding.boxLeave.setBackgroundResource(R.drawable.bg_card_rounded);
        binding.boxAbsent.setBackgroundResource(R.drawable.bg_card_rounded);
        binding.boxLate.setBackgroundResource(R.drawable.bg_card_rounded);
        binding.boxEarly.setBackgroundResource(R.drawable.bg_card_rounded);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    // ══════════════════════════════════════════════════
    //  VERİ YÜKLEME
    // ══════════════════════════════════════════════════

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new BaseApiCallback<List<Department>>(this) {
            @Override
            public void onSuccess(@NonNull List<Department> data) {
                departments = data;
                setupSpinner();
            }

            @Override
            public void onNetworkError(@NonNull Throwable t) {
                // Departman yüklenemese bile spinner boş açılsın
                setupSpinner();
            }
        });
    }

    private void setupSpinner() {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.all_departments));
        for (Department d : departments) items.add(d.getName());

        ArrayAdapter<String> a = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDetailDept.setAdapter(a);

        binding.spinnerDetailDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                selectedBox = null;
                resetBoxHighlights();
                binding.layoutActiveFilter.setVisibility(View.GONE);
                binding.tvListTitle.setText(getString(R.string.personnel_list_title));

                if (pos == 0) {
                    adapter.filterByDepartment(null);
                    loadSummary(null);
                } else {
                    String deptName = departments.get(pos - 1).getName();
                    adapter.filterByDepartment(deptName);
                    loadSummary(departments.get(pos - 1).getId());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void loadPersonnelList() {
        binding.progressDetail.setVisibility(View.VISIBLE);
        Log.d(TAG, "loadPersonnelList → istek gönderiliyor...");

        apiService.getPersonnelList(null).enqueue(new BaseApiCallback<List<PersonnelInfo>>(this) {
            @Override
            public void onSuccess(@NonNull List<PersonnelInfo> data) {
                Log.d(TAG, "loadPersonnelList → " + data.size() + " kayıt geldi");
                adapter.setItems(data);

                if (adapter.getItemCount() == 0) {
                    Log.w(TAG, "loadPersonnelList → Liste boş (filtreleme sonrası)");
                }
            }

            @Override
            public void onEmpty() {
                Log.e(TAG, "loadPersonnelList → 200 ama body null");
                Toast.makeText(PersonnelDetailActivity.this,
                        getString(R.string.personnel_list_empty), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onApiError(int httpCode, @androidx.annotation.Nullable String errorBody) {
                Log.e(TAG, "loadPersonnelList → HTTP " + httpCode
                        + (errorBody != null ? " → " + errorBody : ""));
                Toast.makeText(PersonnelDetailActivity.this,
                        getString(R.string.personnel_list_error, httpCode),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNetworkError(@NonNull Throwable t) {
                Log.e(TAG, "loadPersonnelList → HATA: " + t.getClass().getSimpleName()
                        + " → " + t.getMessage(), t);
                Toast.makeText(PersonnelDetailActivity.this,
                        getString(R.string.personnel_list_network_error, t.getMessage()),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinally() {
                binding.progressDetail.setVisibility(View.GONE);
            }
        });
    }

    private void loadSummary(Integer deptId) {
        apiService.getDashboardSummary(deptId).enqueue(
                new BaseApiCallback<DashboardSummary>(null) {
                    @Override
                    public void onSuccess(@NonNull DashboardSummary data) {
                        binding.tvDetActive.setText(String.valueOf(data.getActiveCount()));
                        binding.tvDetTotal.setText(String.valueOf(data.getTotalCount()));
                        binding.tvDetLeave.setText(String.valueOf(data.getOnLeaveCount()));
                        binding.tvDetAbsent.setText(String.valueOf(data.getAbsentCount()));
                        binding.tvDetLate.setText(String.valueOf(data.getLateCount()));
                        binding.tvDetEarly.setText(String.valueOf(data.getEarlyLeaveCount()));
                    }
                });
    }
}