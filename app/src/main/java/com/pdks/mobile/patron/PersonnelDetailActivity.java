package com.pdks.mobile.patron;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
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
        ((android.widget.TextView) findViewById(R.id.tvToolbarTitle)).setText("Personel Bilgi Kartı");

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
                    Toast.makeText(PersonnelDetailActivity.this,
                            item.getName() + " — cihaz kaydı sıfırlandı",
                            Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PersonnelDetailActivity.this,
                        "Bağlantı hatası: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ══════════════════════════════════════════════════
    //  KUTU FİLTRELEME
    // ══════════════════════════════════════════════════

    private void setupBoxClickListeners() {
        binding.boxActive.setOnClickListener(v -> applyStatusFilter(v, "active", "Aktif Çalışanlar"));
        binding.boxTotal.setOnClickListener(v -> {
            clearFilter();
        });
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
        highlightBox(box, true);

        adapter.filterByStatus(status);

        binding.tvActiveFilter.setText("Filtre: " + label);
        binding.layoutActiveFilter.setVisibility(View.VISIBLE);

        binding.tvListTitle.setText(label + " (" + adapter.getItemCount() + ")");
    }

    private void clearFilter() {
        resetBoxHighlights();
        selectedBox = null;
        adapter.clearStatusFilter();
        binding.layoutActiveFilter.setVisibility(View.GONE);
        binding.tvListTitle.setText("Personel Listesi");
    }

    private void setupClearFilter() {
        binding.btnClearFilter.setOnClickListener(v -> clearFilter());
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

    // ══════════════════════════════════════════════════
    //  VERİ YÜKLEME
    // ══════════════════════════════════════════════════

    private void loadDepartments() {
        apiService.getDepartments().enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> c, Response<List<Department>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    departments = r.body();
                    setupSpinner();
                }
            }
            @Override
            public void onFailure(Call<List<Department>> c, Throwable t) { setupSpinner(); }
        });
    }

    private void setupSpinner() {
        List<String> items = new ArrayList<>();
        items.add("Tüm Departmanlar");
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
                binding.tvListTitle.setText("Personel Listesi");

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

        apiService.getPersonnelList(null).enqueue(new Callback<List<PersonnelInfo>>() {
            @Override
            public void onResponse(Call<List<PersonnelInfo>> c, Response<List<PersonnelInfo>> r) {
                binding.progressDetail.setVisibility(View.GONE);
                Log.d(TAG, "loadPersonnelList → HTTP " + r.code());

                if (r.isSuccessful() && r.body() != null) {
                    List<PersonnelInfo> list = r.body();
                    Log.d(TAG, "loadPersonnelList → " + list.size() + " kayıt geldi");
                    adapter.setItems(list);

                    if (adapter.getItemCount() == 0) {
                        Log.w(TAG, "loadPersonnelList → Liste boş (filtreleme sonrası)");
                    }
                } else if (r.isSuccessful()) {
                    // 200 ama body null
                    Log.e(TAG, "loadPersonnelList → 200 ama body null");
                    Toast.makeText(PersonnelDetailActivity.this,
                            "Personel listesi boş döndü", Toast.LENGTH_LONG).show();
                } else {
                    // HTTP hatası (4xx, 5xx)
                    String errorBody = "";
                    try {
                        if (r.errorBody() != null) {
                            errorBody = r.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = "okunamadı";
                    }
                    Log.e(TAG, "loadPersonnelList → HTTP " + r.code() + " → " + errorBody);
                    Toast.makeText(PersonnelDetailActivity.this,
                            "Personel listesi yüklenemedi (HTTP " + r.code() + ")",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PersonnelInfo>> c, Throwable t) {
                binding.progressDetail.setVisibility(View.GONE);
                Log.e(TAG, "loadPersonnelList → HATA: " + t.getClass().getSimpleName()
                        + " → " + t.getMessage(), t);
                Toast.makeText(PersonnelDetailActivity.this,
                        "Personel listesi hatası: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadSummary(Integer deptId) {
        apiService.getDashboardSummary(deptId).enqueue(new Callback<DashboardSummary>() {
            @Override
            public void onResponse(Call<DashboardSummary> c, Response<DashboardSummary> r) {
                if (r.isSuccessful() && r.body() != null) {
                    DashboardSummary s = r.body();
                    binding.tvDetActive.setText(String.valueOf(s.getActiveCount()));
                    binding.tvDetTotal.setText(String.valueOf(s.getTotalCount()));
                    binding.tvDetLeave.setText(String.valueOf(s.getOnLeaveCount()));
                    binding.tvDetAbsent.setText(String.valueOf(s.getAbsentCount()));
                    binding.tvDetLate.setText(String.valueOf(s.getLateCount()));
                    binding.tvDetEarly.setText(String.valueOf(s.getEarlyLeaveCount()));
                }
            }
            @Override
            public void onFailure(Call<DashboardSummary> c, Throwable t) {}
        });
    }
}