package com.pdks.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class SessionManager {

    private static final String TAG = "SessionManager";

    private static final String PREF_NAME = "pdks_session_encrypted";
    private static final String OLD_PREF_NAME = "pdks_session"; // Eski düz metin pref adı

    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_MODULE_TYPE = "module_type";
    private static final String KEY_COMPANY_CODE = "company_code";
    private static final String KEY_CARD_NO = "card_no";
    private static final String KEY_PERSONNEL_ID = "personnel_id";
    private static final String KEY_PERSONNEL_NAME = "personnel_name";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_IS_PATRON = "is_patron";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_MAC_ADDRESS = "mac_address";

    public static final String MODULE_PATRON = "patron";
    public static final String MODULE_PERSONEL = "personel";

    private final SharedPreferences prefs;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = createEncryptedPrefs();

        // Eski düz metin prefs dosyası varsa temizle (güvenlik için)
        migrateFromOldPrefs();
    }

    /**
     * EncryptedSharedPreferences oluştur.
     * Bazı cihazlarda KeyStore sorunları olabileceğinden,
     * hata durumunda düz metin prefs'e fallback yapar.
     */
    private SharedPreferences createEncryptedPrefs() {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(TAG, "EncryptedSharedPreferences oluşturulamadı, düz metin fallback", e);
            // Fallback — şifreleme başarısız olursa uygulama çökmemeli
            return context.getSharedPreferences(PREF_NAME + "_fallback", Context.MODE_PRIVATE);
        }
    }

    /**
     * Eski düz metin "pdks_session" dosyası varsa sil.
     * Kullanıcı bir sonraki girişte yeni şifreli depoya kaydedilecek.
     */
    private void migrateFromOldPrefs() {
        try {
            SharedPreferences oldPrefs = context.getSharedPreferences(OLD_PREF_NAME, Context.MODE_PRIVATE);
            if (oldPrefs.contains(KEY_IS_LOGGED_IN)) {
                // Eski düz metin veriyi temizle — hassas bilgiler şifresiz kalmasın
                oldPrefs.edit().clear().apply();
                Log.d(TAG, "Eski düz metin oturum verileri temizlendi");
            }
        } catch (Exception e) {
            Log.e(TAG, "Eski prefs temizleme hatası", e);
        }
    }

    // ══════════════ MAC ADRESİ ══════════════

    public String getMacAddress() {
        String savedMac = prefs.getString(KEY_MAC_ADDRESS, null);
        if (savedMac != null && !savedMac.isEmpty() && !"02:00:00:00:00:00".equals(savedMac)) {
            return savedMac;
        }

        String mac = getMacFromNetworkInterface();
        if (mac != null && !mac.isEmpty() && !"02:00:00:00:00:00".equals(mac)) {
            prefs.edit().putString(KEY_MAC_ADDRESS, mac).apply();
            return mac;
        }

        String fallback = "AID_" + getDeviceId();
        prefs.edit().putString(KEY_MAC_ADDRESS, fallback).apply();
        return fallback;
    }

    private String getMacFromNetworkInterface() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = intf.getHardwareAddress();
                if (macBytes == null) return null;

                StringBuilder sb = new StringBuilder();
                for (byte b : macBytes) {
                    sb.append(String.format("%02X:", b));
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, "MAC adresi alınamadı", e);
        }
        return null;
    }

    // ══════════════ PATRON OTURUM YÖNETİMİ ══════════════

    /**
     * Patron girişi kaydet — personnelId dahil
     */
    public void createPatronSession(String companyCode, String cardNo,
                                    String token, int personnelId, String name) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_MODULE_TYPE, MODULE_PATRON)
                .putString(KEY_COMPANY_CODE, companyCode)
                .putString(KEY_CARD_NO, cardNo)
                .putString(KEY_TOKEN, token)
                .putInt(KEY_PERSONNEL_ID, personnelId)
                .putString(KEY_PERSONNEL_NAME, name)
                .putBoolean(KEY_IS_PATRON, true)
                .putString(KEY_MAC_ADDRESS, getMacAddress())
                .apply();
    }

    public boolean isPatronLoggedIn() {
        return isLoggedIn() && MODULE_PATRON.equals(getModuleType());
    }

    public void logoutPatron() {
        if (isPatron()) {
            prefs.edit().clear().apply();
        }
    }

    // ══════════════ PERSONEL OTURUM YÖNETİMİ ══════════════

    public void createPersonelSession(String companyCode, String cardNo,
                                      String token, int personnelId, String name) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_MODULE_TYPE, MODULE_PERSONEL)
                .putString(KEY_COMPANY_CODE, companyCode)
                .putString(KEY_CARD_NO, cardNo)
                .putString(KEY_TOKEN, token)
                .putInt(KEY_PERSONNEL_ID, personnelId)
                .putString(KEY_PERSONNEL_NAME, name)
                .putString(KEY_DEVICE_ID, getDeviceId())
                .putString(KEY_MAC_ADDRESS, getMacAddress())
                .putBoolean(KEY_IS_PATRON, false)
                .apply();
    }

    public boolean isPersonelLocked() {
        return isLoggedIn() && MODULE_PERSONEL.equals(getModuleType());
    }

    // ══════════════ GETTER'LAR ══════════════

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isPatron() {
        return prefs.getBoolean(KEY_IS_PATRON, false);
    }

    public String getModuleType() {
        return prefs.getString(KEY_MODULE_TYPE, "");
    }

    public String getCompanyCode() {
        return prefs.getString(KEY_COMPANY_CODE, "");
    }

    public String getCardNo() {
        return prefs.getString(KEY_CARD_NO, "");
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public int getPersonnelId() {
        return prefs.getInt(KEY_PERSONNEL_ID, -1);
    }

    public String getPersonnelName() {
        return prefs.getString(KEY_PERSONNEL_NAME, "");
    }

    public String getDeviceId() {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    public String getDeviceModel() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    public String getSavedMacAddress() {
        return prefs.getString(KEY_MAC_ADDRESS, "");
    }
}