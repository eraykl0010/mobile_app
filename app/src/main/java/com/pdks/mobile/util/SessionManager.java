package com.pdks.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class SessionManager {

    private static final String PREF_NAME = "pdks_session";
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
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ══════════════ MAC ADRESİ ══════════════

    public String getMacAddress() {
        String savedMac = prefs.getString(KEY_MAC_ADDRESS, null);
        if (savedMac != null && !savedMac.isEmpty() && !"02:00:00:00:00:00".equals(savedMac)) {
            return savedMac;
        }

        String mac = getMacFromNetworkInterface();
        if (mac != null && !mac.isEmpty() && !"02:00:00:00:00:00".equals(mac)) {
            editor.putString(KEY_MAC_ADDRESS, mac);
            editor.apply();
            return mac;
        }

        String fallback = "AID_" + getDeviceId();
        editor.putString(KEY_MAC_ADDRESS, fallback);
        editor.apply();
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
            e.printStackTrace();
        }
        return null;
    }

    // ══════════════ PATRON OTURUM YÖNETİMİ ══════════════

    /**
     * Patron girişi kaydet — personnelId dahil
     */
    public void createPatronSession(String companyCode, String cardNo,
                                    String token, int personnelId, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_MODULE_TYPE, MODULE_PATRON);
        editor.putString(KEY_COMPANY_CODE, companyCode);
        editor.putString(KEY_CARD_NO, cardNo);
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_PERSONNEL_ID, personnelId);
        editor.putString(KEY_PERSONNEL_NAME, name);
        editor.putBoolean(KEY_IS_PATRON, true);
        editor.putString(KEY_MAC_ADDRESS, getMacAddress());
        editor.apply();
    }

    public boolean isPatronLoggedIn() {
        return isLoggedIn() && MODULE_PATRON.equals(getModuleType());
    }

    public void logoutPatron() {
        if (isPatron()) {
            editor.clear();
            editor.apply();
        }
    }

    // ══════════════ PERSONEL OTURUM YÖNETİMİ ══════════════

    public void createPersonelSession(String companyCode, String cardNo,
                                      String token, int personnelId, String name) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_MODULE_TYPE, MODULE_PERSONEL);
        editor.putString(KEY_COMPANY_CODE, companyCode);
        editor.putString(KEY_CARD_NO, cardNo);
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_PERSONNEL_ID, personnelId);
        editor.putString(KEY_PERSONNEL_NAME, name);
        editor.putString(KEY_DEVICE_ID, getDeviceId());
        editor.putString(KEY_MAC_ADDRESS, getMacAddress());
        editor.putBoolean(KEY_IS_PATRON, false);
        editor.apply();
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