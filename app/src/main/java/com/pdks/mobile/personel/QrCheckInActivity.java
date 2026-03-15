package com.pdks.mobile.personel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.tabs.TabLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.pdks.mobile.R;
import com.pdks.mobile.api.ApiService;
import com.pdks.mobile.api.BaseApiCallback;
import com.pdks.mobile.api.RetrofitClient;
import com.pdks.mobile.constants.CheckInType;
import com.pdks.mobile.model.CheckInOutRequest;
import com.pdks.mobile.model.CheckInOutResponse;
import com.pdks.mobile.util.SessionManager;
import com.pdks.mobile.util.ViewUtils;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QrCheckInActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1002;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int QR_VALIDITY_SECONDS = 30;

    // Scan mode views
    private PreviewView cameraPreview;
    private TextView tvScanStatus, tvScanLocationInfo, tvScanResult;
    private View progressScan;

    // Generate mode views
    private View layoutScanMode, layoutGenerateMode;
    private ImageView ivQrCode;
    private TextView tvGenerateLocationInfo, tvQrTimer, tvGenerateResult;
    private View progressGenerate, btnRegenerate;

    // Shared
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;
    private double currentLat = 0, currentLng = 0;
    private boolean locationReady = false;
    private boolean isProcessing = false;
    private CountDownTimer countDownTimer;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_checkin);
        ViewUtils.applyStatusBarPadding(this);

        // Toolbar
        findViewById(R.id.btnToolbarBack).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.tvToolbarTitle)).setText(getString(R.string.title_qr_checkin));

        // Scan views
        layoutScanMode = findViewById(R.id.layoutScanMode);
        cameraPreview = findViewById(R.id.cameraPreview);
        tvScanStatus = findViewById(R.id.tvScanStatus);
        tvScanLocationInfo = findViewById(R.id.tvScanLocationInfo);
        tvScanResult = findViewById(R.id.tvScanResult);
        progressScan = findViewById(R.id.progressScan);

        // Generate views
        layoutGenerateMode = findViewById(R.id.layoutGenerateMode);
        ivQrCode = findViewById(R.id.ivQrCode);
        tvGenerateLocationInfo = findViewById(R.id.tvGenerateLocationInfo);
        tvQrTimer = findViewById(R.id.tvQrTimer);
        tvGenerateResult = findViewById(R.id.tvGenerateResult);
        progressGenerate = findViewById(R.id.progressGenerate);
        btnRegenerate = findViewById(R.id.btnRegenerateQr);

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        cameraExecutor = Executors.newSingleThreadExecutor();

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);

        btnRegenerate.setOnClickListener(v -> generateQrCode());

        // Tabs
        TabLayout tabLayout = findViewById(R.id.tabQrMode);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_qr_scan)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_qr_generate)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showScanMode();
                } else {
                    showGenerateMode();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (allPermissionsGranted()) {
            startCamera();
            requestLocation();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        }
    }

    // ══════════════ MODE SWITCHING ══════════════

    private void showScanMode() {
        layoutScanMode.setVisibility(View.VISIBLE);
        layoutGenerateMode.setVisibility(View.GONE);
        if (countDownTimer != null) countDownTimer.cancel();
        isProcessing = false;
    }

    private void showGenerateMode() {
        layoutScanMode.setVisibility(View.GONE);
        layoutGenerateMode.setVisibility(View.VISIBLE);
        generateQrCode();
    }

    // ══════════════ QR OKUT (SCAN) ══════════════

    private boolean allPermissionsGranted() {
        for (String p : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                analysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                provider.unbindAll();
                provider.bindToLifecycle(this,
                        CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.camera_start_failed), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeImage(ImageProxy imageProxy) {
        if (isProcessing || imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String value = barcode.getRawValue();
                        if (value != null && !value.isEmpty()) {
                            isProcessing = true;
                            runOnUiThread(() -> onQrScanned(value));
                            break;
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void onQrScanned(String qrCode) {
        tvScanStatus.setText(getString(R.string.qr_scanned_processing));
        progressScan.setVisibility(View.VISIBLE);

        if (!locationReady) {
            tvScanStatus.setText(getString(R.string.location_waiting));
            tvScanStatus.postDelayed(() -> {
                if (locationReady) {
                    sendScanCheckIn(qrCode);
                } else {
                    isProcessing = false;
                    progressScan.setVisibility(View.GONE);
                    tvScanStatus.setText(getString(R.string.location_failed_retry));
                }
            }, 3000);
            return;
        }

        sendScanCheckIn(qrCode);
    }

    private void sendScanCheckIn(String qrCode) {
        CheckInOutRequest request = new CheckInOutRequest(
                sessionManager.getPersonnelId(),
                currentLat, currentLng,
                qrCode, CheckInType.QR_SCAN,
                sessionManager.getDeviceId()
        );

        apiService.qrCheckInOut(request).enqueue(
                new BaseApiCallback<CheckInOutResponse>(this) {
                    @Override
                    public void onSuccess(@NonNull CheckInOutResponse data) {
                        tvScanResult.setVisibility(View.VISIBLE);

                        if (data.isSuccess()) {
                            tvScanResult.setText(getString(R.string.checkin_success));
                            tvScanResult.setTextColor(getColor(R.color.status_success));
                            tvScanStatus.setText(getString(R.string.qr_operation_complete));
                        } else {
                            tvScanResult.setText(data.getMessage());
                            tvScanResult.setTextColor(getColor(R.color.status_danger));
                            tvScanResult.postDelayed(() -> isProcessing = false, 3000);
                        }
                    }

                    @Override
                    public void onApiError(int httpCode, @androidx.annotation.Nullable String errorBody) {
                        super.onApiError(httpCode, errorBody);
                        isProcessing = false;
                    }

                    @Override
                    public void onNetworkError(@NonNull Throwable t) {
                        super.onNetworkError(t);
                        isProcessing = false;
                    }

                    @Override
                    public void onFinally() {
                        progressScan.setVisibility(View.GONE);
                    }
                });
    }

    // ══════════════ QR OLUŞTUR (GENERATE) ══════════════

    private void generateQrCode() {
        progressGenerate.setVisibility(View.VISIBLE);
        ivQrCode.setImageBitmap(null);
        tvGenerateResult.setVisibility(View.GONE);
        btnRegenerate.setEnabled(false);

        if (countDownTimer != null) countDownTimer.cancel();

        String cardNo = sessionManager.getCardNo();
        String qrContent = "PDKS_CHECKIN|"
                + sessionManager.getPersonnelId() + "|"
                + cardNo + "|"
                + System.currentTimeMillis() + "|"
                + String.format(Locale.US, "%.6f,%.6f", currentLat, currentLng) + "|"
                + sessionManager.getDeviceId();

        try {
            Bitmap qrBitmap = generateQrBitmap(qrContent, 600);
            ivQrCode.setImageBitmap(qrBitmap);
            progressGenerate.setVisibility(View.GONE);

            startCountdown();

            if (locationReady) {
                tvGenerateLocationInfo.setText(
                        String.format(Locale.US, "Konum: %.6f, %.6f | Kart: %s",
                                currentLat, currentLng, cardNo));
            } else {
                tvGenerateLocationInfo.setText(getString(R.string.location_fetching_card, cardNo));
            }

        } catch (Exception e) {
            progressGenerate.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.qr_generate_failed), Toast.LENGTH_SHORT).show();
            btnRegenerate.setEnabled(true);
        }
    }

    private Bitmap generateQrBitmap(String content, int size) throws Exception {
        // ZXing ile gerçek, taranabilir QR kod üret
        com.google.zxing.qrcode.QRCodeWriter writer = new com.google.zxing.qrcode.QRCodeWriter();

        java.util.Map<com.google.zxing.EncodeHintType, Object> hints = new java.util.HashMap<>();
        hints.put(com.google.zxing.EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(com.google.zxing.EncodeHintType.MARGIN, 1);
        hints.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION,
                com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M);

        com.google.zxing.common.BitMatrix bitMatrix =
                writer.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, size, size, hints);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = bitMatrix.get(x, y)
                        ? android.graphics.Color.BLACK
                        : android.graphics.Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private void startCountdown() {
        tvQrTimer.setText(getString(R.string.qr_timer_validity, QR_VALIDITY_SECONDS));

        countDownTimer = new CountDownTimer(QR_VALIDITY_SECONDS * 1000L, 1000) {
            @Override
            public void onTick(long millisLeft) {
                int seconds = (int) (millisLeft / 1000);
                tvQrTimer.setText(getString(R.string.qr_timer_validity, seconds));

                if (seconds <= 10) {
                    tvQrTimer.setTextColor(getColor(R.color.status_danger));
                } else {
                    tvQrTimer.setTextColor(getColor(R.color.primary));
                }
            }

            @Override
            public void onFinish() {
                tvQrTimer.setText(getString(R.string.qr_timer_expired));
                tvQrTimer.setTextColor(getColor(R.color.status_danger));
                ivQrCode.setAlpha(0.3f);
                btnRegenerate.setEnabled(true);
            }
        };

        countDownTimer.start();
        btnRegenerate.setEnabled(false);
        ivQrCode.setAlpha(1.0f);
    }

    // ══════════════ KONUM ══════════════

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        LocationRequest locReq = new LocationRequest.Builder(
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
                    String info = String.format(Locale.US, "Konum hazır (±%.0fm)", loc.getAccuracy());
                    tvScanLocationInfo.setText(info);
                    tvGenerateLocationInfo.setText(info);
                }
            }
        };

        fusedClient.requestLocationUpdates(locReq, locationCallback, Looper.getMainLooper());
    }

    // ══════════════ İZİNLER ══════════════

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] p, @NonNull int[] r) {
        super.onRequestPermissionsResult(code, p, r);
        if (code == PERMISSION_CODE && allPermissionsGranted()) {
            startCamera();
            requestLocation();
        } else {
            Toast.makeText(this, getString(R.string.camera_location_permission_required), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
        if (countDownTimer != null) countDownTimer.cancel();
    }
}