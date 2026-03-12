package com.pdks.mobile.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.databinding.ActivityLoginBinding;
import com.pdks.mobile.model.LoginRequest;
import com.pdks.mobile.model.LoginResponse;
import com.pdks.mobile.patron.PatronDashboardActivity;
import com.pdks.mobile.personel.PersonelDashboardActivity;
import com.pdks.mobile.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager sessionManager;
    private ApiService apiService;
    private String moduleType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        moduleType = getIntent().getStringExtra("module");

        setupUI();
        setupClickListeners();
    }

    private void setupUI() {
        if (SessionManager.MODULE_PATRON.equals(moduleType)) {
            binding.tvLoginSubtitle.setText("Patron Modülü");
        } else {
            binding.tvLoginSubtitle.setText("Personel Modülü");
        }
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String companyCode = binding.etCompanyCode.getText().toString().trim();
        String cardNo = binding.etPassword.getText().toString().trim();

        if (companyCode.isEmpty()) {
            binding.etCompanyCode.setError("Şirket kodu gerekli");
            binding.etCompanyCode.requestFocus();
            return;
        }

        if (cardNo.isEmpty()) {
            binding.etPassword.setError("Personel kart no gerekli");
            binding.etPassword.requestFocus();
            return;
        }

        setLoading(true);

        String macAddress = sessionManager.getMacAddress();

        LoginRequest request = new LoginRequest(
                companyCode,
                cardNo,
                sessionManager.getDeviceId(),
                sessionManager.getDeviceModel(),
                moduleType,
                macAddress
        );

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call,
                                   Response<LoginResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResp = response.body();

                    if (loginResp.isSuccess()) {
                        handleSuccessLogin(loginResp, companyCode, cardNo);
                    } else {
                        String message = loginResp.getMessage();
                        if (message != null && (message.contains("cihaz") || message.contains("device"))) {
                            showDeviceBindingError(message);
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    message, Toast.LENGTH_LONG).show();
                        }
                    }
                } else if (response.code() == 403) {
                    showDeviceBindingError(
                            "Bu cihaz başka bir personele kayıtlıdır veya " +
                                    "bu hesap başka bir cihaza bağlıdır. " +
                                    "Lütfen yöneticinize başvurun.");
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Sunucu hatası: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Bağlantı hatası: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeviceBindingError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Cihaz Kısıtlaması")
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Tamam", null)
                .setCancelable(true)
                .show();
    }

    private void handleSuccessLogin(LoginResponse resp, String companyCode,
                                    String cardNo) {
        if (SessionManager.MODULE_PATRON.equals(moduleType)) {
            String name = RetrofitClient.MOCK_MODE ? "Ahmet Yılmaz (Patron)" :
                    resp.getPersonnelName();
            // ═══ FIX: personnelId artık kaydediliyor ═══
            sessionManager.createPatronSession(
                    companyCode, cardNo, resp.getToken(),
                    resp.getPersonnelId(), name
            );
            Intent intent = new Intent(this, PatronDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            sessionManager.createPersonelSession(
                    companyCode, cardNo, resp.getToken(),
                    resp.getPersonnelId(), resp.getPersonnelName()
            );
            Intent intent = new Intent(this, PersonelDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setLoading(boolean loading) {
        binding.progressLogin.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.btnLogin.setAlpha(loading ? 0.6f : 1.0f);
    }
}