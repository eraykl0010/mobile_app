package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class CheckInOutRequest {

    @SerializedName("personnel_id")
    private int personnelId;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("qr_code")
    private String qrCode;

    @SerializedName("type")
    private String type; // "location" veya "qr_location"

    @SerializedName("device_id")
    private String deviceId;

    public CheckInOutRequest(int personnelId, double latitude, double longitude,
                             String qrCode, String type, String deviceId) {
        this.personnelId = personnelId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.qrCode = qrCode;
        this.type = type;
        this.deviceId = deviceId;
    }

    // Getters & Setters
    public int getPersonnelId() { return personnelId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getQrCode() { return qrCode; }
    public String getType() { return type; }
    public String getDeviceId() { return deviceId; }
}