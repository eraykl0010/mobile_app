package com.pdks.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pdks.mobile.databinding.ActivityMainBinding;
import com.pdks.mobile.login.LoginActivity;
import com.pdks.mobile.patron.PatronDashboardActivity;
import com.pdks.mobile.personel.PersonelDashboardActivity;
import com.pdks.mobile.util.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        // Personel zaten giriş yapmışsa direkt personel modülüne yönlendir
        if (sessionManager.isPersonelLocked()) {
            navigateToPersonelDashboard();
            return;
        }

        // Patron daha önce giriş yapmışsa direkt patron modülüne yönlendir
        // (Bir kere giriş yaptıktan sonra tekrar şifre sormaz)
        if (sessionManager.isPatronLoggedIn()) {
            navigateToPatronDashboard();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // B2: 401 interceptor tarafından yönlendirilmişse bilgi ver
        if (getIntent().getBooleanExtra("session_expired", false)) {
            Toast.makeText(this, getString(R.string.error_session_expired),
                    Toast.LENGTH_LONG).show();
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Patron Modülüne Git
        binding.cardPatron.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("module", SessionManager.MODULE_PATRON);
            startActivity(intent);
        });

        // Personel Modülüne Git
        binding.cardPersonel.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("module", SessionManager.MODULE_PERSONEL);
            startActivity(intent);
        });
    }

    private void navigateToPersonelDashboard() {
        Intent intent = new Intent(this, PersonelDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToPatronDashboard() {
        Intent intent = new Intent(this, PatronDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}