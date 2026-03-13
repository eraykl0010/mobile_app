package com.pdks.mobile.personel;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.pdks.mobile.databinding.ActivityPersonelDashboardBinding;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

public class PersonelDashboardActivity extends AppCompatActivity {

    private ActivityPersonelDashboardBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPersonelDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        setupUI();
        setupClickListeners();
        setupBackPress();
        ViewUtils.applyStatusBarPadding(this);
    }

    private void setupUI() {
        String name = sessionManager.getPersonnelName();
        binding.tvWelcomePersonel.setText("Hoş geldiniz, " + name);
    }

    private void setupClickListeners() {
        binding.cardLocationCheckIn.setOnClickListener(v ->
                startActivity(new Intent(this, LocationCheckInActivity.class)));

        binding.cardQrCheckIn.setOnClickListener(v ->
                startActivity(new Intent(this, QrCheckInActivity.class)));

        binding.cardAttendanceReport.setOnClickListener(v ->
                startActivity(new Intent(this, AttendanceReportActivity.class)));

        binding.cardLeaveRequest.setOnClickListener(v ->
                startActivity(new Intent(this, LeaveRequestActivity.class)));

        binding.cardAdvanceRequest.setOnClickListener(v ->
                startActivity(new Intent(this, AdvanceRequestActivity.class)));

        binding.cardMonthlyOvertime.setOnClickListener(v ->
                startActivity(new Intent(this, MonthlyOvertimeActivity.class)));
    }

    /**
     * Personel modülünde geri tuşu uygulamayı kapatmamalı,
     * sadece arka plana atmalı. Cihaz kilitli modda çalışıyor.
     */
    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                moveTaskToBack(true);
            }
        });
    }
}