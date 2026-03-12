package com.pdks.mobile.api;

import android.content.Context;

import com.pdks.mobile.util.SessionManager;

import java.io.IOException;
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

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {

                    SessionManager session = new SessionManager(context);

                    // Loglama
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    // Auth header interceptor
                    Interceptor authInterceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
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
                        }
                    };

                    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS);

                    if (MOCK_MODE) {
                        // Mock modda: sadece MockInterceptor + logging
                        clientBuilder.addInterceptor(new MockInterceptor());
                        clientBuilder.addInterceptor(logging);
                    } else {
                        // Gerçek modda: auth + logging
                        clientBuilder.addInterceptor(authInterceptor);
                        clientBuilder.addInterceptor(logging);
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