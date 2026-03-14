package com.pdks.mobile.patron;

import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonnelDetailActivity extends AppCompatActivity {

    private static final String TAG = "PersonnelDetail";
    private static final int DETAIL_REFRESH_CALL_COUNT = 2;

    private ActivityPersonnelDetailBinding binding;
    private ApiService apiService;
    private SessionManager sessionManager;
    private PersonnelListAdapter adapter;

    private List<Department> departments = new ArrayList<>();
    private View selectedBox = null;

    private Integer selectedDepartmentId = null;
    private int selectedDepartmentSpinnerPosition = 0;

    private String activeStatusFilterLabel = null;
    private int pendingRefreshCalls = 0;
    private boolean isFirstResume = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonnelDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewUtils.applyStatusBarPadding(this);

        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((android.widget.TextView) findViewById(R.id.tvToolbarTitle)).setText("Personel Bilgi Kartı");

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        sessionManager = new SessionManager(this);

        adapter = new PersonnelListAdapter();
        binding.rvPersonnelList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPersonnelList.setAdapter(adapter);

        adapter.setOnItemLongClickListener((item, position) -> showPersonnelOptionsDialog(item));

        setupSwipeRefresh();
        setupBoxClickListeners();
        setupClearFilter();

        loadDepartments();
        refreshContent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstResume) {
            isFirstResume = false;
            return;
        }

        refreshContent();
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshPersonnelDetail.setColorSchemeResources(R.color.primary);

        binding.swipeRefreshPersonnelDetail.setOnChildScrollUpCallback(
                (parent, child) -> binding.rvPersonnelList.canScrollVertically(-1)
        );

        binding.swipeRefreshPersonnelDetail.setOnRefreshListener(() -> {
            loadDepartments();
            refreshContent();
        });
    }

    private void beginRefresh(int requestCount) {
        pendingRefreshCalls = requestCount;
        binding.swipeRefreshPersonnelDetail.setRefreshing(true);
    }

    private void finishRefreshRequest() {
        if (pendingRefreshCalls > 0) {
            pendingRefreshCalls--;
        }

        if (pendingRefreshCalls == 0) {
            binding.swipeRefreshPersonnelDetail.setRefreshing(false);
        }
    }

    private void refreshContent() {
        beginRefresh(DETAIL_REFRESH_CALL_COUNT);
        loadPersonnelList(true);
        loadSummary(selectedDepartmentId, true);
    }

    private void showPersonnelOptionsDialog(PersonnelInfo item) {
        String[] options = {"Cihaz Kaydını Sıfırla"};

        new AlertDialog.Builder(this)
                .setTitle(item.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        confirmResetDevice(item);
                    }
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    private void confirmResetDevice(PersonnelInfo item) {
        new AlertDialog.Builder(this)
                .setTitle("Cihaz Kaydını Sıfırla")
                .setMessage(item.getName() + " adlı personelin cihaz kaydı silinecek.\n\n"
                        + "Personel bir sonraki girişte yeni cihazına otomatik kaydedilecektir.\n\n"
                        + "Devam etmek istiyor musunuz?")
                .setPositiveButton("Sıfırla", (d, w) -> executeResetDevice(item))
                .setNegativeButton("İptal", null)
                .show();
    }

    private void executeResetDevice(PersonnelInfo item) {
        int patronId = sessionManager.getPersonnelId();
        ResetDeviceRequest req = new ResetDeviceRequest(item.getId(), patronId);

        apiService.resetDevice(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> resp) {
                if (resp.isSuccessful() && resp.body() != null && resp.body().isSuccess()) {
                    Toast.makeText(
                            PersonnelDetailActivity.this,
                            item.getName() + " — cihaz kaydı sıfırlandı",
                            Toast.LENGTH_SHORT
                    ).show();

                    refreshContent();
                } else {
                    String msg = "İşlem başarısız";
                    if (resp.body() != null && resp.body().getMessage() != null) {
                        msg = resp.body().getMessage();
                    }
                    Toast.makeText(PersonnelDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(
                        PersonnelDetailActivity.this,
                        "Bağlantı hatası: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void setupBoxClickListeners() {
        binding.boxActive.setOnClickListener(v -> applyStatusFilter(v, "active", "Aktif Çalışanlar"));
        binding.boxTotal.setOnClickListener(v -> clearFilter());
        binding.boxLeave.setOnClickListener(v -> applyStatusFilter(v, "on_leave", "İzinli Personel"));
        binding.boxAbsent.setOnClickListener(v -> applyStatusFilter(v, "absent", "Devamsız Personel"));
        binding.boxLate.setOnClickListener(v -> applyStatusFilter(v, "late", "Geç Gelen Personel"));
        binding.boxEarly.setOnClickListener(v -> applyStatusFilter(v, "early", "Erken Çıkan Personel"));
    }

    private void applyStatusFilter(View box, String status, String label) {
        if (selectedBox == box && status.equals(adapter.getCurrentStatus())) {
            clearFilter();
            return;
        }

        resetBoxHighlights();
        selectedBox = box;
        activeStatusFilterLabel = label;

        highlightBox(box, true);
        adapter.filterByStatus(status);

        binding.tvActiveFilter.setText("Filtre: " + label);
        binding.layoutActiveFilter.setVisibility(View.VISIBLE);

        updateListTitle();
    }

    private void clearFilter() {
        selectedBox = null;
        activeStatusFilterLabel = null;

        resetBoxHighlights();
        adapter.clearStatusFilter();

        binding.layoutActiveFilter.setVisibility(View.GONE);
        updateListTitle();
    }

    private void clearFilterUiOnly() {
        selectedBox = null;
        activeStatusFilterLabel = null;

        resetBoxHighlights();
        binding.layoutActiveFilter.setVisibility(View.GONE);
    }

    private void setupClearFilter() {
        binding.btnClearFilter.setOnClickListener(v -> clearFilter());
    }

    private void updateListTitle() {
        if (activeStatusFilterLabel != null && !activeStatusFilterLabel.isEmpty()) {
            binding.tvListTitle.setText(activeStatusFilterLabel + " (" + adapter.getItemCount() + ")");
        } else {
            binding.tvListTitle.setText("Personel Listesi");
        }
    }

    private void highlightBox(View box, boolean selected) {
        if (selected) {
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dpToPx(16));
            bg.setColor(Color.parseColor("#FFF3E0"));
            bg.setStroke(dpToPx(2), Color.parseColor("#FF6D00"));
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

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new BaseApiCallback<List<Department>>(this) {
            @Override
            public void onSuccess(@NonNull List<Department> data) {
                departments = data;
                setupSpinner();
            }

            @Override
            public void onNetworkError(@NonNull Throwable t) {
                setupSpinner();
            }
        });
    }

    private void setupSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Tüm Departmanlar");

        for (Department d : departments) {
            items.add(d.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerDetailDept.setOnItemSelectedListener(null);
        binding.spinnerDetailDept.setAdapter(spinnerAdapter);

        if (selectedDepartmentSpinnerPosition > departments.size()) {
            selectedDepartmentSpinnerPosition = 0;
            selectedDepartmentId = null;
            adapter.filterByDepartment(null);
        }

        binding.spinnerDetailDept.setSelection(selectedDepartmentSpinnerPosition, false);
        attachDepartmentSpinnerListener();
    }

    private void attachDepartmentSpinnerListener() {
        binding.spinnerDetailDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedDepartmentSpinnerPosition = pos;

                clearFilterUiOnly();

                if (pos == 0) {
                    selectedDepartmentId = null;
                    adapter.filterByDepartment(null);
                    loadSummary(null, false);
                } else {
                    Department selectedDepartment = departments.get(pos - 1);
                    selectedDepartmentId = selectedDepartment.getId();
                    adapter.filterByDepartment(selectedDepartment.getName());
                    loadSummary(selectedDepartmentId, false);
                }

                updateListTitle();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadPersonnelList(boolean trackRefresh) {
        if (!binding.swipeRefreshPersonnelDetail.isRefreshing()) {
            binding.progressDetail.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "loadPersonnelList → istek gönderiliyor...");

        apiService.getPersonnelList(null).enqueue(new BaseApiCallback<List<PersonnelInfo>>(this) {
            @Override
            public void onSuccess(@NonNull List<PersonnelInfo> data) {
                Log.d(TAG, "loadPersonnelList → " + data.size() + " kayıt geldi");
                adapter.setItems(data);
                updateListTitle();

                if (adapter.getItemCount() == 0) {
                    Log.w(TAG, "loadPersonnelList → Liste boş (filtreleme sonrası)");
                }
            }

            @Override
            public void onEmpty() {
                Log.e(TAG, "loadPersonnelList → 200 ama body null");
                adapter.setItems(new ArrayList<>());
                updateListTitle();

                Toast.makeText(
                        PersonnelDetailActivity.this,
                        "Personel listesi boş döndü",
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onApiError(int httpCode, String errorBody) {
                Log.e(TAG, "loadPersonnelList → HTTP " + httpCode + " → " + errorBody);
                Toast.makeText(
                        PersonnelDetailActivity.this,
                        "Personel listesi yüklenemedi (HTTP " + httpCode + ")",
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onNetworkError(@NonNull Throwable t) {
                Log.e(TAG, "loadPersonnelList → HATA: "
                        + t.getClass().getSimpleName()
                        + " → "
                        + t.getMessage(), t);

                Toast.makeText(
                        PersonnelDetailActivity.this,
                        "Personel listesi hatası: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onFinally() {
                binding.progressDetail.setVisibility(View.GONE);

                if (trackRefresh) {
                    finishRefreshRequest();
                }
            }
        });
    }

    private void loadSummary(Integer deptId, boolean trackRefresh) {
        apiService.getDashboardSummary(deptId).enqueue(new BaseApiCallback<DashboardSummary>(this) {
            @Override
            public void onSuccess(@NonNull DashboardSummary s) {
                binding.tvDetActive.setText(String.valueOf(s.getActiveCount()));
                binding.tvDetTotal.setText(String.valueOf(s.getTotalCount()));
                binding.tvDetLeave.setText(String.valueOf(s.getOnLeaveCount()));
                binding.tvDetAbsent.setText(String.valueOf(s.getAbsentCount()));
                binding.tvDetLate.setText(String.valueOf(s.getLateCount()));
                binding.tvDetEarly.setText(String.valueOf(s.getEarlyLeaveCount()));
            }

            @Override
            public void onFinally() {
                if (trackRefresh) {
                    finishRefreshRequest();
                }
            }
        });
    }
}