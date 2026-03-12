package com.pdks.mobile.model;

import com.google.gson.annotations.SerializedName;

public class ApprovalRequest {

    @SerializedName("request_id")
    private String requestId;    // leave: "5", advance: "EVR000023"

    @SerializedName("request_type")
    private String requestType;  // "leave" veya "advance"

    @SerializedName("action")
    private String action;       // "approve" veya "reject"

    @SerializedName("note")
    private String note;

    public ApprovalRequest(String requestId, String requestType, String action, String note) {
        this.requestId = requestId;
        this.requestType = requestType;
        this.action = action;
        this.note = note;
    }

    public String getRequestId() { return requestId; }
    public String getRequestType() { return requestType; }
    public String getAction() { return action; }
    public String getNote() { return note; }
}