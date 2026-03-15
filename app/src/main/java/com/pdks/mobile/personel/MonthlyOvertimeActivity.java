package com.pdks.mobile.personel;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.MonthlyOvertime;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

import androidx.annotation.NonNull;

import com.pdks.mobile.api.BaseApiCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MonthlyOvertimeActivity extends AppCompatActivity {

    private Spinner spinnerMonth;
    private ProgressBar progressBar;
    private MaterialCardView cardSummary;
    private TextView tvTotalWork, tvOvertime, tvWorkDays, tvAbsent, tvLate, tvEarly;
    private ApiService apiService;
    private int personnelId;
    private List<String> monthValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_overtime);

        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.title_monthly_overtime));

        spinnerMonth = findViewById(R.id.spinnerMonth);
        progressBar = findViewById(R.id.progressOvertime);
        cardSummary = findViewById(R.id.cardOvertimeSummary);

        tvTotalWork = findViewById(R.id.tvTotalWorkHours);
        tvOvertime = findViewById(R.id.tvOvertimeHours);
        tvWorkDays = findViewById(R.id.tvWorkDays);
        tvAbsent = findViewById(R.id.tvAbsentDays);
        tvLate = findViewById(R.id.tvMonthLateCount);
        tvEarly = findViewById(R.id.tvMonthEarlyCount);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        personnelId = new SessionManager(this).getPersonnelId();

        setupMonthSpinner();
        ViewUtils.applyStatusBarPadding(this);
    }

    private void setupMonthSpinner() {
        List<String> monthLabels = new ArrayList<>();
        SimpleDateFormat labelFormat = new SimpleDateFormat("MMMM yyyy", new Locale("tr"));
        SimpleDateFormat valueFormat = new SimpleDateFormat("yyyy-MM", Locale.US);

        Calendar cal = Calendar.getInstance();
        // Son 6 ay
        for (int i = 0; i < 6; i++) {
            monthLabels.add(labelFormat.format(cal.getTime()));
            monthValues.add(valueFormat.format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, monthLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        spinnerMonth.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                loadData(monthValues.get(pos));
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
    }

    private void loadData(String month) {
        progressBar.setVisibility(View.VISIBLE);
        cardSummary.setVisibility(View.GONE);

        apiService.getMonthlyOvertime(personnelId, month).enqueue(
                new BaseApiCallback<MonthlyOvertime>(this) {
                    @Override
                    public void onSuccess(@NonNull MonthlyOvertime data) {
                        cardSummary.setVisibility(View.VISIBLE);

                        tvTotalWork.setText(String.format(Locale.US, "%.1f", data.getTotalWorkHours()));
                        tvOvertime.setText(String.format(Locale.US, "%.1f", data.getTotalOvertimeHours()));
                        tvWorkDays.setText(String.valueOf(data.getTotalWorkDays()));
                        tvAbsent.setText(String.valueOf(data.getAbsentDays()));
                        tvLate.setText(String.valueOf(data.getLateCount()));
                        tvEarly.setText(String.valueOf(data.getEarlyLeaveCount()));
                    }

                    @Override
                    public void onFinally() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}