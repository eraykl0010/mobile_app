package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

/**
 * Avans talebi GÖNDERME modeli.
 * AdvanceRequest sınıfı sunucudan GELEN avans verilerini temsil eder,
 * bu sınıf ise sunucuya GÖNDERİLEN talebi temsil eder.
 *
 * C# backend: AdvanceSubmitRequest
 * SP: dbo.sp_CreateAdvanceRequest
 */
public class AdvanceSubmitRequest {

    @SerializedName("personnel_id")
    private int personnelId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("reason")
    private String reason;

    public AdvanceSubmitRequest(int personnelId, double amount, String reason) {
        this.personnelId = personnelId;
        this.amount = amount;
        this.reason = reason;
    }

    // Getters
    public int getPersonnelId() { return personnelId; }
    public double getAmount() { return amount; }
    public String getReason() { return reason; }
}