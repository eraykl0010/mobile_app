package com.pdks.mobile.util;

import android.location.Location;
import android.os.Build;

/**
 * Konum yardımcı fonksiyonları.
 */
public final class LocationUtils {

    private LocationUtils() {}

    /**
     * Konumun sahte (mock) olup olmadığını kontrol eder.
     * Android 12+ (API 31) → location.isMock()
     * Android 10-11 (API 26-30) → location.isFromMockProvider()
     *
     * @return true → sahte konum tespit edildi
     */
    public static boolean isMockLocation(Location location) {
        if (location == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return location.isMock();
        }
        return location.isFromMockProvider();
    }
}
