package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class ResetDeviceRequest {

    @SerializedName("personnel_id")
    private int personnelId;

    @SerializedName("reset_by")
    private int resetBy;

    public ResetDeviceRequest(int personnelId, int resetBy) {
        this.personnelId = personnelId;
        this.resetBy = resetBy;
    }

    public int getPersonnelId() { return personnelId; }
    public int getResetBy() { return resetBy; }
}