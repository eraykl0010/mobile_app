package com.pdks.mobile.constants;

/**
 * İzin tipi sabitleri — API'nin beklediği değerler.
 * Spinner sırası: ANNUAL(0), DAILY(1), HOURLY(2)
 */
public final class LeaveType {
    public static final String ANNUAL  = "yillik";
    public static final String DAILY   = "gunluk";
    public static final String HOURLY  = "saatlik";
    public static final String ADVANCE = "avans";

    /** Spinner index → API değeri (LeaveFormFragment'ta kullanılır) */
    public static final String[] VALUES = {ANNUAL, DAILY, HOURLY};

    private LeaveType() {}
}
