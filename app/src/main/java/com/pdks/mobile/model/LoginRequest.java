package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("company_code")
    private String companyCode;

    @SerializedName("card_no")
    private String cardNo;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("device_model")
    private String deviceModel;

    @SerializedName("module_type")
    private String moduleType; // "patron" veya "personel"

    @SerializedName("mac_address")
    private String macAddress;

    public LoginRequest(String companyCode, String cardNo, String deviceId,
                        String deviceModel, String moduleType, String macAddress) {
        this.companyCode = companyCode;
        this.cardNo = cardNo;
        this.deviceId = deviceId;
        this.deviceModel = deviceModel;
        this.moduleType = moduleType;
        this.macAddress = macAddress;
    }

    // Getter & Setter
    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }

    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getModuleType() { return moduleType; }
    public void setModuleType(String moduleType) { this.moduleType = moduleType; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
}