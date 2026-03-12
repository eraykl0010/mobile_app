package com.pdks.mobile.personel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.AdvanceSubmitRequest;
import com.pdks.mobile.model.ApiResponse;
import com.pdks.mobile.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvanceFormFragment extends Fragment {

    private EditText etAmount, etReason;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle saved) {
        View view = inflater.inflate(R.layout.fragment_advance_form, container, false);

        etAmount = view.findViewById(R.id.etAmount);
        etReason = view.findViewById(R.id.etAdvanceReason);
        btnSubmit = view.findViewById(R.id.btnSubmitAdvance);
        progressBar = view.findViewById(R.id.progressAdvance);

        sessionManager = new SessionManager(requireContext());

        btnSubmit.setOnClickListener(v -> submitAdvance());

        return view;
    }

    private void submitAdvance() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Tutar girin", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Geçerli bir tutar girin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(requireContext(), "Tutar sıfırdan büyük olmalı", Toast.LENGTH_SHORT).show();
            return;
        }

        String reason = etReason.getText().toString().trim();
        if (reason.isEmpty()) {
            reason = "-"; // Boş bırakılmasın
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        // ═══ TÜM ALANLARI DOLDURARAK REQUEST OLUŞTUR ═══
        int personnelId = sessionManager.getPersonnelId();

        AdvanceSubmitRequest req = new AdvanceSubmitRequest(
                personnelId,
                amount,
                reason
        );

        ApiService api = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        api.submitAdvanceRequest(req).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> resp) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (resp.isSuccessful() && resp.body() != null && resp.body().isSuccess()) {
                    Toast.makeText(requireContext(), "Avans talebi gönderildi", Toast.LENGTH_SHORT).show();
                    etAmount.setText("");
                    etReason.setText("");
                } else {
                    String msg = "Talep gönderilemedi";
                    if (resp.body() != null && resp.body().getMessage() != null) {
                        msg = resp.body().getMessage();
                    }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(requireContext(), "Bağlantı hatası: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}