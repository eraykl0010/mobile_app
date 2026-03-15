package com.pdks.mobile.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdks.mobile.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Tüm API callback'lerinde tekrar eden boilerplate'i ortadan kaldıran
 * merkezi callback sınıfı.
 *
 * Eski kullanım (her yerde tekrar):
 * <pre>
 *   apiService.getData().enqueue(new Callback&lt;X&gt;() {
 *       public void onResponse(...) {
 *           progressBar.setVisibility(View.GONE);
 *           if (resp.isSuccessful() && resp.body() != null) { ... }
 *       }
 *       public void onFailure(...) {
 *           progressBar.setVisibility(View.GONE);
 *           Toast.makeText(...).show(); // veya boş bırakılmış
 *       }
 *   });
 * </pre>
 *
 * Yeni kullanım:
 * <pre>
 *   apiService.getData().enqueue(new BaseApiCallback&lt;X&gt;(context) {
 *       public void onSuccess(X data) {
 *           // Sadece iş mantığı — null/hata kontrolü yapılmış
 *       }
 *   });
 * </pre>
 *
 * İsteğe bağlı override'lar:
 * - onSuccess(T data)       → zorunlu, başarılı yanıt
 * - onApiError(int code)    → HTTP 4xx/5xx hataları (varsayılan: Toast)
 * - onNetworkError(Throwable)→ bağlantı hataları (varsayılan: Toast)
 * - onEmpty()               → body null veya boş liste (varsayılan: hiçbir şey)
 * - onFinally()             → her durumda çalışır (varsayılan: hiçbir şey)
 *                             progressBar gizleme gibi işler için ideal
 */
public abstract class BaseApiCallback<T> implements Callback<T> {

    private static final String TAG = "API";

    @Nullable
    private final Context context;

    /**
     * @param context Toast göstermek için gerekli. null geçilebilir — bu durumda
     *                varsayılan hata Toast'ları gösterilmez, sadece loglanır.
     */
    public BaseApiCallback(@Nullable Context context) {
        this.context = context;
    }

    // ═══════════════════════════════════════════
    //  ALT SINIFIN OVERRIDE EDECEĞİ METODLAR
    // ═══════════════════════════════════════════

    /**
     * Başarılı yanıt — body null değil, HTTP 2xx.
     * Alt sınıf bu metodu override ETMEK ZORUNDA.
     */
    public abstract void onSuccess(@NonNull T data);

    /**
     * HTTP başarılı (2xx) ama body null.
     * Varsayılan: hiçbir şey yapmaz. Liste endpoint'leri için
     * override edilip "boş liste" UI'ı gösterilebilir.
     */
    public void onEmpty() {
        // Alt sınıf isterse override eder
    }

    /**
     * HTTP hata yanıtı (4xx, 5xx).
     * Varsayılan: Toast ile hata mesajı gösterir.
     */
    public void onApiError(int httpCode, @Nullable String errorBody) {
        Log.e(TAG, "HTTP " + httpCode + (errorBody != null ? " → " + errorBody : ""));
        if (context != null) {
            showToast(context.getString(R.string.error_server, httpCode));
        }
    }

    /**
     * Ağ hatası — sunucuya ulaşılamadı.
     * Varsayılan: Toast ile bağlantı hatası gösterir.
     */
    public void onNetworkError(@NonNull Throwable t) {
        Log.e(TAG, "Ağ hatası: " + t.getClass().getSimpleName() + " → " + t.getMessage());
        if (context != null) {
            if (t instanceof NoConnectivityException) {
                showToast(context.getString(R.string.error_no_internet));
            } else {
                showToast(context.getString(R.string.error_connection, t.getMessage()));
            }
        }
    }

    /**
     * Her durumda (başarılı/hata) en son çalışır.
     * ProgressBar gizleme, buton aktif etme gibi işler için ideal.
     *
     * Kullanım:
     * <pre>
     *   new BaseApiCallback&lt;X&gt;(context) {
     *       public void onSuccess(X data) { ... }
     *       public void onFinally() {
     *           progressBar.setVisibility(View.GONE);
     *           btnSubmit.setEnabled(true);
     *       }
     *   }
     * </pre>
     */
    public void onFinally() {
        // Alt sınıf isterse override eder
    }

    // ═══════════════════════════════════════════
    //  RETROFIT CALLBACK İMPLEMENTASYONU
    // ═══════════════════════════════════════════

    @Override
    public final void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        try {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    onSuccess(response.body());
                } else {
                    onEmpty();
                }
            } else {
                String errorBody = null;
                try {
                    if (response.errorBody() != null) {
                        errorBody = response.errorBody().string();
                    }
                } catch (Exception ignored) {}
                onApiError(response.code(), errorBody);
            }
        } finally {
            onFinally();
        }
    }

    @Override
    public final void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        try {
            onNetworkError(t);
        } finally {
            onFinally();
        }
    }

    // ═══════════════════════════════════════════
    //  YARDIMCI
    // ═══════════════════════════════════════════

    private void showToast(String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}