package com.pdks.mobile.personel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.constants.LeaveType;
import com.pdks.mobile.model.ApiResponse;
import com.pdks.mobile.model.LeaveSubmitRequest;
import com.pdks.mobile.util.SessionManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class LeaveFormFragment extends Fragment {

    private Spinner spinnerType;
    private EditText etStartDate, etEndDate, etStartTime, etEndTime, etReason;
    private LinearLayout layoutTimeFields;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // İzin tip indexleri (spinner sırası ile aynı olmalı)
    private static final int TYPE_YILLIK  = 0;
    private static final int TYPE_GUNLUK  = 1;
    private static final int TYPE_SAATLIK = 2;

    // Spinner index → API değeri
    private static final String[] TYPE_VALUES = LeaveType.VALUES;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle saved) {
        View view = inflater.inflate(R.layout.fragment_leave_form, container, false);

        spinnerType = view.findViewById(R.id.spinnerLeaveType);
        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        etStartTime = view.findViewById(R.id.etStartTime);
        etEndTime = view.findViewById(R.id.etEndTime);
        etReason = view.findViewById(R.id.etLeaveReason);
        layoutTimeFields = view.findViewById(R.id.layoutTimeFields);
        btnSubmit = view.findViewById(R.id.btnSubmitLeave);
        progressBar = view.findViewById(R.id.progressLeave);

        sessionManager = new SessionManager(requireContext());

        setupSpinner();
        setupDatePickers();
        btnSubmit.setOnClickListener(v -> submitRequest());

        return view;
    }

    private void setupSpinner() {
        String[] types = {
                getString(R.string.leave_type_annual),
                getString(R.string.leave_type_daily),
                getString(R.string.leave_type_hourly)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> p, View v, int pos, long id) {
                boolean isSaatlik = (pos == TYPE_SAATLIK);
                boolean isGunluk = (pos == TYPE_GUNLUK);

                // Saatlik izin seçilince saat alanları görünür
                layoutTimeFields.setVisibility(isSaatlik ? View.VISIBLE : View.GONE);

                // Günlük izin: bitiş tarihi = başlangıç tarihi, bitiş pasif
                if (isGunluk) {
                    etEndDate.setEnabled(false);
                    String start = etStartDate.getText().toString().trim();
                    if (!start.isEmpty()) {
                        etEndDate.setText(start);
                    } else {
                        etEndDate.setText("");
                    }
                } else if (isSaatlik) {
                    // Saatlik izin: bitiş tarihi = başlangıç tarihi
                    etEndDate.setEnabled(false);
                    String start = etStartDate.getText().toString().trim();
                    if (!start.isEmpty()) {
                        etEndDate.setText(start);
                    } else {
                        etEndDate.setText("");
                    }
                } else {
                    // Yıllık izin: bitiş tarihi aktif
                    etEndDate.setEnabled(true);
                }

                // Saatlik değilse saat alanlarını temizle
                if (!isSaatlik) {
                    etStartTime.setText("");
                    etEndTime.setText("");
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> p) {}
        });
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> {
            if (etEndDate.isEnabled()) showDatePicker(etEndDate);
        });

        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime));
    }

    private void showDatePicker(EditText target) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (dp, y, m, d) -> {
            // DatePicker ayları 0-tabanlı, LocalDate 1-tabanlı
            String picked = LocalDate.of(y, m + 1, d).format(DATE_FORMAT);
            target.setText(picked);

            // Günlük veya saatlik izin seçiliyse: bitiş tarihi otomatik
            int type = spinnerType.getSelectedItemPosition();
            if ((type == TYPE_GUNLUK || type == TYPE_SAATLIK) && target == etStartDate) {
                etEndDate.setText(picked);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker(EditText target) {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(requireContext(), (tp, h, m) -> {
            target.setText(LocalTime.of(h, m).format(TIME_FORMAT));
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
    }

    private void submitRequest() {
        String startDate = etStartDate.getText().toString().trim();
        if (startDate.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.error_start_date_required), Toast.LENGTH_SHORT).show();
            return;
        }

        int typeIndex = spinnerType.getSelectedItemPosition();
        boolean isSaatlik = (typeIndex == TYPE_SAATLIK);
        boolean isGunluk = (typeIndex == TYPE_GUNLUK);

        // Günlük / saatlik izin: bitiş = başlangıç
        if (isGunluk || isSaatlik) {
            etEndDate.setText(startDate);
        }

        String endDate = etEndDate.getText().toString().trim();

        // Yıllık izin: bitiş tarihi zorunlu
        if (typeIndex == TYPE_YILLIK && endDate.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.error_end_date_required), Toast.LENGTH_SHORT).show();
            return;
        }

        // Saatlik izin: saat aralığı zorunlu
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        if (isSaatlik && (startTime.isEmpty() || endTime.isEmpty())) {
            Toast.makeText(requireContext(), getString(R.string.error_time_range_required), Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = etReason.getText().toString().trim();
        if (reason.isEmpty()) {
            reason = "-"; // Boş bırakılmasın, SP crash etmesin
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        // ═══ TÜM ALANLARI DOLDURARAK REQUEST OLUŞTUR ═══
        int personnelId = sessionManager.getPersonnelId();
        String leaveType = TYPE_VALUES[typeIndex];

        LeaveSubmitRequest req = new LeaveSubmitRequest(
                personnelId,
                leaveType,
                startDate,
                endDate,
                isSaatlik ? startTime : null,   // Saatlik değilse null
                isSaatlik ? endTime : null,
                reason
        );

        ApiService api = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        api.submitLeaveRequest(req).enqueue(
                new BaseApiCallback<ApiResponse>(getContext()) {
                    @Override
                    public void onSuccess(@NonNull ApiResponse data) {
                        if (data.isSuccess()) {
                            Toast.makeText(requireContext(),
                                    getString(R.string.leave_request_sent), Toast.LENGTH_SHORT).show();
                            clearForm();
                        } else {
                            String msg = data.getMessage() != null
                                    ? data.getMessage() : getString(R.string.error_submit_failed);
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFinally() {
                        progressBar.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                    }
                });
    }

    private void clearForm() {
        etStartDate.setText("");
        etEndDate.setText("");
        etStartTime.setText("");
        etEndTime.setText("");
        etReason.setText("");
        spinnerType.setSelection(TYPE_YILLIK);
    }
}