package com.pdks.mobile.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

/**
 * Ağ bağlantısı kontrol yardımcısı.
 */
public final class NetworkUtils {

    private NetworkUtils() {}

    /**
     * Cihazın internet erişimi olup olmadığını kontrol eder.
     * @return true → internet var, false → yok
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkCapabilities caps = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return caps != null
                && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }
}
