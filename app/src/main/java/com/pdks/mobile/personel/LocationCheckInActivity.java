package com.pdks.mobile.personel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.model.CheckInOutRequest;
import com.pdks.mobile.model.CheckInOutResponse;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationCheckInActivity extends AppCompatActivity {

    private static final int LOC_PERMISSION_CODE = 1001;

    private TextView tvStatus, tvLocationInfo, tvCurrentTime, tvResult;
    private View progressBar;
    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;
    private double currentLat = 0, currentLng = 0;
    private boolean locationReady = false;
    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private SessionManager sessionManager;
    private ApiService apiService;
    private Button btnCheckInOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_checkin);

        // Toolbar
        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText("Konum ile Giriş/Çıkış");

        tvStatus = findViewById(R.id.tvCheckStatus);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvResult = findViewById(R.id.tvResult);
        btnCheckInOut = findViewById(R.id.btnCheckInOut);
        progressBar = findViewById(R.id.progressCheckIn);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        btnCheckInOut.setOnClickListener(v -> performCheckInOut());

        startClock();
        requestLocationUpdates();
        ViewUtils.applyStatusBarPadding(this);
    }

    private void startClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Runnable tick = new Runnable() {
            @Override
            public void run() {
                tvCurrentTime.setText(sdf.format(new Date()));
                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(tick);
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERMISSION_CODE);
            return;
        }

        LocationRequest locRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(1000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location loc = result.getLastLocation();
                if (loc != null) {
                    currentLat = loc.getLatitude();
                    currentLng = loc.getLongitude();
                    locationReady = true;

                    tvStatus.setText("Konum hazır");
                    tvLocationInfo.setText(String.format(Locale.US,
                            "%.6f, %.6f (±%.0fm)", currentLat, currentLng, loc.getAccuracy()));
                    btnCheckInOut.setEnabled(true);
                }
            }
        };

        fusedClient.requestLocationUpdates(locRequest, locationCallback, Looper.getMainLooper());
    }

    private void performCheckInOut() {
        if (!locationReady) {
            Toast.makeText(this, "Konum henüz alınamadı", Toast.LENGTH_SHORT).show();
            return;
        }

        // Çift tıklama koruması — buton hemen devre dışı
        btnCheckInOut.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        CheckInOutRequest request = new CheckInOutRequest(
                sessionManager.getPersonnelId(),
                currentLat, currentLng,
                null, "location",
                sessionManager.getDeviceId()
        );

        apiService.checkInOut(request).enqueue(
                new BaseApiCallback<CheckInOutResponse>(this) {
                    @Override
                    public void onSuccess(@NonNull CheckInOutResponse data) {
                        if (data.isSuccess()) {
                            tvResult.setVisibility(View.VISIBLE);
                            tvResult.setText("Giriş / Çıkış Kaydı Gönderildi.");
                            tvResult.setTextColor(getColor(R.color.status_success));
                            // Başarılı işlem sonrası buton 3 sn kilitli kalsın — mükerrer kayıt önlenir
                            btnCheckInOut.postDelayed(() -> btnCheckInOut.setEnabled(true), 3000);
                        } else {
                            tvResult.setVisibility(View.VISIBLE);
                            tvResult.setText(data.getMessage());
                            tvResult.setTextColor(getColor(R.color.status_danger));
                            btnCheckInOut.setEnabled(true);
                        }
                    }

                    @Override
                    public void onApiError(int httpCode, @androidx.annotation.Nullable String errorBody) {
                        super.onApiError(httpCode, errorBody);
                        btnCheckInOut.setEnabled(true);
                    }

                    @Override
                    public void onNetworkError(@NonNull Throwable t) {
                        super.onNetworkError(t);
                        btnCheckInOut.setEnabled(true);
                    }

                    @Override
                    public void onFinally() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] perms, @NonNull int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == LOC_PERMISSION_CODE && results.length > 0
                && results[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            Toast.makeText(this, "Konum izni gerekli", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);
        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }
}