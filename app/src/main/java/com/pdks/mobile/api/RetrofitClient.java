package com.pdks.mobile.api;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.pdks.mobile.MainActivity;
import com.pdks.mobile.util.NetworkUtils;
import com.pdks.mobile.util.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // ╔══════════════════════════════════════════════╗
    // ║  MOCK MODU: true = sahte veri, false = API   ║
    // ║  Sunucuya bağlanırken false yapın            ║
    // ╚══════════════════════════════════════════════╝
    public static final boolean MOCK_MODE = false;

    private static Retrofit retrofit = null;

    /** 401 redirect'in birden fazla kez tetiklenmesini önler */
    private static volatile boolean isRedirectingToLogin = false;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {

                    // Application context kullan — Activity leak önlenir
                    Context appContext = context.getApplicationContext();
                    SessionManager session = new SessionManager(appContext);

                    // ── Loglama ──
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    // ── B1: İnternet bağlantısı kontrolü ──
                    // İstek göndermeden önce bağlantı yoksa NoConnectivityException fırlatır.
                    Interceptor connectivityInterceptor = chain -> {
                        if (!NetworkUtils.isOnline(appContext)) {
                            throw new NoConnectivityException();
                        }
                        return chain.proceed(chain.request());
                    };

                    // ── Auth header interceptor ──
                    Interceptor authInterceptor = chain -> {
                        Request original = chain.request();
                        Request.Builder builder = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json");

                        String token = session.getToken();
                        if (token != null && !token.isEmpty()) {
                            builder.header("Authorization", "Bearer " + token);
                        }

                        String companyCode = session.getCompanyCode();
                        if (companyCode != null && !companyCode.isEmpty()) {
                            builder.header("X-Company-Code", companyCode);
                        }

                        return chain.proceed(builder.build());
                    };

                    // ── B2: HTTP 401 — Oturum süresi dolmuş, login'e yönlendir ──
                    // Login endpoint'i hariç tutulur (login 401 dönerse normal hata olarak işlenir).
                    Interceptor sessionExpiredInterceptor = chain -> {
                        Response response = chain.proceed(chain.request());

                        if (response.code() == 401
                                && !chain.request().url().encodedPath().contains(ApiConfig.LOGIN)
                                && !isRedirectingToLogin) {

                            isRedirectingToLogin = true;

                            new Handler(Looper.getMainLooper()).post(() -> {
                                session.logoutPatron();
                                resetClient();

                                Intent intent = new Intent(appContext, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("session_expired", true);
                                appContext.startActivity(intent);

                                isRedirectingToLogin = false;
                            });
                        }

                        return response;
                    };

                    // ── OkHttpClient oluştur ──
                    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS);

                    if (MOCK_MODE) {
                        // Mock modda: sadece MockInterceptor + logging
                        clientBuilder.addInterceptor(new MockInterceptor());
                        clientBuilder.addInterceptor(logging);
                    } else {
                        // Gerçek modda: bağlantı kontrolü → auth → logging → 401 kontrolü
                        clientBuilder.addInterceptor(connectivityInterceptor);
                        clientBuilder.addInterceptor(authInterceptor);
                        clientBuilder.addInterceptor(logging);
                        clientBuilder.addInterceptor(sessionExpiredInterceptor);
                    }

                    OkHttpClient client = clientBuilder.build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(ApiConfig.BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }
}