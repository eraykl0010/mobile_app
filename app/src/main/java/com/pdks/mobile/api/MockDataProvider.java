package com.pdks.mobile.api;

public class MockDataProvider {

    // ==================== AUTH ====================

    public static String loginPatronSuccess() {
        return "{"
                + "\"success\": true,"
                + "\"message\": \"Giriş başarılı\","
                + "\"token\": \"mock-patron-token-abc123\","
                + "\"personnel_id\": 100,"
                + "\"personnel_name\": \"Ahmet Yılmaz\","
                + "\"is_patron\": true,"
                + "\"department\": \"Yönetim\""
                + "}";
    }

    public static String loginPersonelSuccess() {
        return "{"
                + "\"success\": true,"
                + "\"message\": \"Giriş başarılı\","
                + "\"token\": \"mock-personel-token-xyz789\","
                + "\"personnel_id\": 42,"
                + "\"personnel_name\": \"Mehmet Demir\","
                + "\"is_patron\": false,"
                + "\"department\": \"Yazılım Geliştirme\""
                + "}";
    }

    public static String loginFailed() {
        return "{"
                + "\"success\": false,"
                + "\"message\": \"Şirket kodu veya personel kart numarası hatalı\","
                + "\"token\": null,"
                + "\"personnel_id\": 0,"
                + "\"personnel_name\": null,"
                + "\"is_patron\": false,"
                + "\"department\": null"
                + "}";
    }

    // ==================== DEPARTMENTS ====================

    public static String departments() {
        return "["
                + "{\"id\": 1, \"name\": \"Yazılım Geliştirme\"},"
                + "{\"id\": 2, \"name\": \"İnsan Kaynakları\"},"
                + "{\"id\": 3, \"name\": \"Muhasebe\"},"
                + "{\"id\": 4, \"name\": \"Satış & Pazarlama\"},"
                + "{\"id\": 5, \"name\": \"Üretim\"},"
                + "{\"id\": 6, \"name\": \"Lojistik\"}"
                + "]";
    }

    // ==================== DASHBOARD SUMMARY ====================

    public static String dashboardSummaryAll() {
        return "{"
                + "\"active_count\": 87,"
                + "\"total_count\": 124,"
                + "\"on_leave_count\": 14,"
                + "\"absent_count\": 5,"
                + "\"late_count\": 8,"
                + "\"early_leave_count\": 3,"
                + "\"department_name\": \"Tümü\""
                + "}";
    }

    public static String dashboardSummaryDept1() {
        return "{"
                + "\"active_count\": 18,"
                + "\"total_count\": 22,"
                + "\"on_leave_count\": 2,"
                + "\"absent_count\": 1,"
                + "\"late_count\": 3,"
                + "\"early_leave_count\": 1,"
                + "\"department_name\": \"Yazılım Geliştirme\""
                + "}";
    }

    public static String dashboardSummaryDept2() {
        return "{"
                + "\"active_count\": 8,"
                + "\"total_count\": 10,"
                + "\"on_leave_count\": 1,"
                + "\"absent_count\": 0,"
                + "\"late_count\": 1,"
                + "\"early_leave_count\": 0,"
                + "\"department_name\": \"İnsan Kaynakları\""
                + "}";
    }

    public static String dashboardSummaryDept3() {
        return "{"
                + "\"active_count\": 12,"
                + "\"total_count\": 15,"
                + "\"on_leave_count\": 2,"
                + "\"absent_count\": 1,"
                + "\"late_count\": 0,"
                + "\"early_leave_count\": 0,"
                + "\"department_name\": \"Muhasebe\""
                + "}";
    }

    public static String dashboardSummaryDept4() {
        return "{"
                + "\"active_count\": 20,"
                + "\"total_count\": 28,"
                + "\"on_leave_count\": 4,"
                + "\"absent_count\": 1,"
                + "\"late_count\": 2,"
                + "\"early_leave_count\": 1,"
                + "\"department_name\": \"Satış & Pazarlama\""
                + "}";
    }

    public static String dashboardSummaryDept5() {
        return "{"
                + "\"active_count\": 22,"
                + "\"total_count\": 35,"
                + "\"on_leave_count\": 4,"
                + "\"absent_count\": 2,"
                + "\"late_count\": 2,"
                + "\"early_leave_count\": 1,"
                + "\"department_name\": \"Üretim\""
                + "}";
    }

    public static String dashboardSummaryDept6() {
        return "{"
                + "\"active_count\": 7,"
                + "\"total_count\": 14,"
                + "\"on_leave_count\": 1,"
                + "\"absent_count\": 0,"
                + "\"late_count\": 0,"
                + "\"early_leave_count\": 0,"
                + "\"department_name\": \"Lojistik\""
                + "}";
    }

    // ==================== PENDING LEAVE REQUESTS ====================

    public static String pendingAnnualLeaves() {
        return "["
                + "{"
                + "  \"id\": 1001,"
                + "  \"personnel_name\": \"Zeynep Kaya\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"leave_type\": \"yillik\","
                + "  \"start_date\": \"24.02.2026\","
                + "  \"end_date\": \"28.02.2026\","
                + "  \"start_time\": null,"
                + "  \"end_time\": null,"
                + "  \"reason\": \"Aile ziyareti için Antalya'ya gidiyorum\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"18.02.2026\","
                + "  \"remaining_days\": 14"
                + "},"
                + "{"
                + "  \"id\": 1002,"
                + "  \"personnel_name\": \"Ali Veli Öztürk\","
                + "  \"department\": \"Muhasebe\","
                + "  \"leave_type\": \"yillik\","
                + "  \"start_date\": \"03.03.2026\","
                + "  \"end_date\": \"07.03.2026\","
                + "  \"start_time\": null,"
                + "  \"end_time\": null,"
                + "  \"reason\": \"Tatil planı\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"19.02.2026\","
                + "  \"remaining_days\": 8"
                + "},"
                + "{"
                + "  \"id\": 1003,"
                + "  \"personnel_name\": \"Fatma Şahin\","
                + "  \"department\": \"Satış & Pazarlama\","
                + "  \"leave_type\": \"yillik\","
                + "  \"start_date\": \"10.03.2026\","
                + "  \"end_date\": \"14.03.2026\","
                + "  \"start_time\": null,"
                + "  \"end_time\": null,"
                + "  \"reason\": \"Düğün hazırlıkları\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"20.02.2026\","
                + "  \"remaining_days\": 20"
                + "},"
                + "{"
                + "  \"id\": 1004,"
                + "  \"personnel_name\": \"Burak Çelik\","
                + "  \"department\": \"Üretim\","
                + "  \"leave_type\": \"yillik\","
                + "  \"start_date\": \"02.03.2026\","
                + "  \"end_date\": \"03.03.2026\","
                + "  \"start_time\": null,"
                + "  \"end_time\": null,"
                + "  \"reason\": \"Kişisel işler\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"20.02.2026\","
                + "  \"remaining_days\": 3"
                + "}"
                + "]";
    }

    public static String pendingHourlyLeaves() {
        return "["
                + "{"
                + "  \"id\": 2001,"
                + "  \"personnel_name\": \"Emre Yıldız\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"leave_type\": \"saatlik\","
                + "  \"start_date\": \"21.02.2026\","
                + "  \"end_date\": \"21.02.2026\","
                + "  \"start_time\": \"14:00\","
                + "  \"end_time\": \"16:00\","
                + "  \"reason\": \"Diş randevusu\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"20.02.2026\","
                + "  \"remaining_days\": 11"
                + "},"
                + "{"
                + "  \"id\": 2002,"
                + "  \"personnel_name\": \"Selin Arslan\","
                + "  \"department\": \"İnsan Kaynakları\","
                + "  \"leave_type\": \"saatlik\","
                + "  \"start_date\": \"22.02.2026\","
                + "  \"end_date\": \"22.02.2026\","
                + "  \"start_time\": \"09:00\","
                + "  \"end_time\": \"11:00\","
                + "  \"reason\": \"Banka işlemleri\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"20.02.2026\","
                + "  \"remaining_days\": 16"
                + "},"
                + "{"
                + "  \"id\": 2003,"
                + "  \"personnel_name\": \"Oğuz Kara\","
                + "  \"department\": \"Lojistik\","
                + "  \"leave_type\": \"saatlik\","
                + "  \"start_date\": \"23.02.2026\","
                + "  \"end_date\": \"23.02.2026\","
                + "  \"start_time\": \"15:00\","
                + "  \"end_time\": \"17:30\","
                + "  \"reason\": \"Çocuğu okuldan alma\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"21.02.2026\","
                + "  \"remaining_days\": 9"
                + "}"
                + "]";
    }

    // ==================== PENDING ADVANCE REQUESTS ====================

    public static String pendingAdvances() {
        return "["
                + "{"
                + "  \"id\": 3001,"
                + "  \"personnel_name\": \"Hakan Korkmaz\","
                + "  \"department\": \"Üretim\","
                + "  \"amount\": 5000.00,"
                + "  \"reason\": \"Ev tadilatı için acil nakit ihtiyacı\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"19.02.2026\""
                + "},"
                + "{"
                + "  \"id\": 3002,"
                + "  \"personnel_name\": \"Ayşe Güneş\","
                + "  \"department\": \"Satış & Pazarlama\","
                + "  \"amount\": 3000.00,"
                + "  \"reason\": \"Araç tamiri\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"id\": 3003,"
                + "  \"personnel_name\": \"Murat Aydın\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"amount\": 7500.00,"
                + "  \"reason\": \"Eğitim masrafları\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"21.02.2026\""
                + "},"
                + "{"
                + "  \"id\": 3004,"
                + "  \"personnel_name\": \"Deniz Polat\","
                + "  \"department\": \"Muhasebe\","
                + "  \"amount\": 2000.00,"
                + "  \"reason\": \"Sağlık harcamaları\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"21.02.2026\""
                + "},"
                + "{"
                + "  \"id\": 3005,"
                + "  \"personnel_name\": \"Cem Yalçın\","
                + "  \"department\": \"Lojistik\","
                + "  \"amount\": 4500.00,"
                + "  \"reason\": \"Taşınma masrafları\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"20.02.2026\""
                + "}"
                + "]";
    }

    // ==================== LATE / EARLY REPORT ====================

    public static String lateEarlyReport() {
        return "["
                + "{"
                + "  \"personnel_name\": \"Murat Aydın\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"type\": \"overtime\","
                + "  \"scheduled_time\": \"18:00\","
                + "  \"actual_time\": \"19:30\","
                + "  \"difference_minutes\": 90,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Volkan Taş\","
                + "  \"department\": \"Üretim\","
                + "  \"type\": \"overtime\","
                + "  \"scheduled_time\": \"17:00\","
                + "  \"actual_time\": \"18:45\","
                + "  \"difference_minutes\": 105,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Deniz Polat\","
                + "  \"department\": \"Muhasebe\","
                + "  \"type\": \"overtime\","
                + "  \"scheduled_time\": \"18:00\","
                + "  \"actual_time\": \"19:00\","
                + "  \"difference_minutes\": 60,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Cem Yalçın\","
                + "  \"department\": \"Lojistik\","
                + "  \"type\": \"overtime\","
                + "  \"scheduled_time\": \"17:00\","
                + "  \"actual_time\": \"17:40\","
                + "  \"difference_minutes\": 40,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Emre Yıldız\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"type\": \"undertime\","
                + "  \"scheduled_time\": \"08:30\","
                + "  \"actual_time\": \"08:52\","
                + "  \"difference_minutes\": 22,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Selin Arslan\","
                + "  \"department\": \"İnsan Kaynakları\","
                + "  \"type\": \"undertime\","
                + "  \"scheduled_time\": \"09:00\","
                + "  \"actual_time\": \"09:15\","
                + "  \"difference_minutes\": 15,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Burak Çelik\","
                + "  \"department\": \"Üretim\","
                + "  \"type\": \"undertime\","
                + "  \"scheduled_time\": \"07:00\","
                + "  \"actual_time\": \"07:35\","
                + "  \"difference_minutes\": 35,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Zeynep Kaya\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"type\": \"undertime\","
                + "  \"scheduled_time\": \"18:00\","
                + "  \"actual_time\": \"17:10\","
                + "  \"difference_minutes\": 50,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Hakan Korkmaz\","
                + "  \"department\": \"Üretim\","
                + "  \"type\": \"undertime\","
                + "  \"scheduled_time\": \"17:00\","
                + "  \"actual_time\": \"16:30\","
                + "  \"difference_minutes\": 30,"
                + "  \"date\": \"20.02.2026\""
                + "},"
                + "{"
                + "  \"personnel_name\": \"Ali Veli Öztürk\","
                + "  \"department\": \"Muhasebe\","
                + "  \"type\": \"undertime\","
                + "  \"scheduled_time\": \"09:00\","
                + "  \"actual_time\": \"09:08\","
                + "  \"difference_minutes\": 8,"
                + "  \"date\": \"20.02.2026\""
                + "}"
                + "]";
    }

    // ==================== PERSONEL — DAILY REPORT ====================

    public static String dailyReport() {
        return "["
                + "{"
                + "  \"date\": \"21.02.2026\","
                + "  \"day_name\": \"Cumartesi\","
                + "  \"check_in\": \"08:28\","
                + "  \"check_out\": null,"
                + "  \"work_hours\": \"-\","
                + "  \"overtime_hours\": \"-\","
                + "  \"status\": \"normal\""
                + "}"
                + "]";
    }

    // ==================== PERSONEL — WEEKLY REPORT ====================

    public static String weeklyReport() {
        return "["
                + "{"
                + "  \"date\": \"21.02.2026\","
                + "  \"day_name\": \"Cumartesi\","
                + "  \"check_in\": \"08:28\","
                + "  \"check_out\": null,"
                + "  \"work_hours\": \"-\","
                + "  \"overtime_hours\": \"0\","
                + "  \"status\": \"normal\""
                + "},"
                + "{"
                + "  \"date\": \"20.02.2026\","
                + "  \"day_name\": \"Cuma\","
                + "  \"check_in\": \"08:25\","
                + "  \"check_out\": \"17:35\","
                + "  \"work_hours\": \"9s 10dk\","
                + "  \"overtime_hours\": \"1s 10dk\","
                + "  \"status\": \"normal\""
                + "},"
                + "{"
                + "  \"date\": \"19.02.2026\","
                + "  \"day_name\": \"Perşembe\","
                + "  \"check_in\": \"08:45\","
                + "  \"check_out\": \"17:05\","
                + "  \"work_hours\": \"8s 20dk\","
                + "  \"overtime_hours\": \"20dk\","
                + "  \"status\": \"late\""
                + "},"
                + "{"
                + "  \"date\": \"18.02.2026\","
                + "  \"day_name\": \"Çarşamba\","
                + "  \"check_in\": \"08:30\","
                + "  \"check_out\": \"18:15\","
                + "  \"work_hours\": \"9s 45dk\","
                + "  \"overtime_hours\": \"1s 45dk\","
                + "  \"status\": \"normal\""
                + "},"
                + "{"
                + "  \"date\": \"17.02.2026\","
                + "  \"day_name\": \"Salı\","
                + "  \"check_in\": null,"
                + "  \"check_out\": null,"
                + "  \"work_hours\": \"0\","
                + "  \"overtime_hours\": \"0\","
                + "  \"status\": \"leave\""
                + "},"
                + "{"
                + "  \"date\": \"16.02.2026\","
                + "  \"day_name\": \"Pazartesi\","
                + "  \"check_in\": \"08:32\","
                + "  \"check_out\": \"16:50\","
                + "  \"work_hours\": \"8s 18dk\","
                + "  \"overtime_hours\": \"0\","
                + "  \"status\": \"early\""
                + "},"
                + "{"
                + "  \"date\": \"15.02.2026\","
                + "  \"day_name\": \"Pazar\","
                + "  \"check_in\": null,"
                + "  \"check_out\": null,"
                + "  \"work_hours\": \"0\","
                + "  \"overtime_hours\": \"0\","
                + "  \"status\": \"absent\""
                + "}"
                + "]";
    }

    // ==================== PERSONEL — MONTHLY OVERTIME ====================

    public static String monthlyOvertime(String month) {
        switch (month) {
            case "2026-02":
                return "{"
                        + "\"month\": \"Şubat 2026\","
                        + "\"total_work_hours\": 136.5,"
                        + "\"total_overtime_hours\": 12.5,"
                        + "\"total_work_days\": 17,"
                        + "\"absent_days\": 1,"
                        + "\"late_count\": 3,"
                        + "\"early_leave_count\": 1"
                        + "}";
            case "2026-01":
                return "{"
                        + "\"month\": \"Ocak 2026\","
                        + "\"total_work_hours\": 176.0,"
                        + "\"total_overtime_hours\": 8.0,"
                        + "\"total_work_days\": 22,"
                        + "\"absent_days\": 0,"
                        + "\"late_count\": 2,"
                        + "\"early_leave_count\": 0"
                        + "}";
            case "2025-12":
                return "{"
                        + "\"month\": \"Aralık 2025\","
                        + "\"total_work_hours\": 168.0,"
                        + "\"total_overtime_hours\": 16.0,"
                        + "\"total_work_days\": 21,"
                        + "\"absent_days\": 1,"
                        + "\"late_count\": 1,"
                        + "\"early_leave_count\": 2"
                        + "}";
            default:
                return "{"
                        + "\"month\": \"" + month + "\","
                        + "\"total_work_hours\": 160.0,"
                        + "\"total_overtime_hours\": 5.0,"
                        + "\"total_work_days\": 20,"
                        + "\"absent_days\": 0,"
                        + "\"late_count\": 1,"
                        + "\"early_leave_count\": 1"
                        + "}";
        }
    }

    // ==================== PERSONEL — LEAVE HISTORY ====================

    public static String leaveHistory() {
        return "["
                + "{"
                + "  \"id\": 5001,"
                + "  \"personnel_name\": \"Mehmet Demir\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"leave_type\": \"yillik\","
                + "  \"start_date\": \"17.02.2026\","
                + "  \"end_date\": \"17.02.2026\","
                + "  \"start_time\": null,"
                + "  \"end_time\": null,"
                + "  \"reason\": \"Kişisel işler\","
                + "  \"status\": \"approved\","
                + "  \"request_date\": \"14.02.2026\","
                + "  \"remaining_days\": 11"
                + "},"
                + "{"
                + "  \"id\": 5002,"
                + "  \"personnel_name\": \"Mehmet Demir\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"leave_type\": \"saatlik\","
                + "  \"start_date\": \"10.02.2026\","
                + "  \"end_date\": \"10.02.2026\","
                + "  \"start_time\": \"14:00\","
                + "  \"end_time\": \"16:30\","
                + "  \"reason\": \"Hastane randevusu\","
                + "  \"status\": \"approved\","
                + "  \"request_date\": \"08.02.2026\","
                + "  \"remaining_days\": 11"
                + "},"
                + "{"
                + "  \"id\": 5003,"
                + "  \"personnel_name\": \"Mehmet Demir\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"leave_type\": \"yillik\","
                + "  \"start_date\": \"05.03.2026\","
                + "  \"end_date\": \"07.03.2026\","
                + "  \"start_time\": null,"
                + "  \"end_time\": null,"
                + "  \"reason\": \"Aile ziyareti\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"20.02.2026\","
                + "  \"remaining_days\": 11"
                + "},"
                + "{"
                + "  \"id\": 5004,"
                + "  \"personnel_name\": \"Mehmet Demir\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"leave_type\": \"yillik\","
                + "  \"start_date\": \"20.01.2026\","
                + "  \"end_date\": \"24.01.2026\","
                + "  \"start_time\": null,"
                + "  \"end_time\": null,"
                + "  \"reason\": \"Kayak tatili\","
                + "  \"status\": \"rejected\","
                + "  \"request_date\": \"10.01.2026\","
                + "  \"remaining_days\": 14"
                + "}"
                + "]";
    }

    // ==================== PERSONEL — ADVANCE HISTORY ====================

    public static String advanceHistory() {
        return "["
                + "{"
                + "  \"id\": 6001,"
                + "  \"personnel_name\": \"Mehmet Demir\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"amount\": 3000.00,"
                + "  \"reason\": \"Bilgisayar tamiri\","
                + "  \"status\": \"approved\","
                + "  \"request_date\": \"05.02.2026\""
                + "},"
                + "{"
                + "  \"id\": 6002,"
                + "  \"personnel_name\": \"Mehmet Demir\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"amount\": 5000.00,"
                + "  \"reason\": \"Ev kirası desteği\","
                + "  \"status\": \"pending\","
                + "  \"request_date\": \"18.02.2026\""
                + "},"
                + "{"
                + "  \"id\": 6003,"
                + "  \"personnel_name\": \"Mehmet Demir\","
                + "  \"department\": \"Yazılım Geliştirme\","
                + "  \"amount\": 10000.00,"
                + "  \"reason\": \"Araba alım desteği\","
                + "  \"status\": \"rejected\","
                + "  \"request_date\": \"15.01.2026\""
                + "}"
                + "]";
    }

    // ==================== CHECK IN/OUT ====================

    public static String checkInSuccess() {
        return "{"
                + "\"success\": true,"
                + "\"message\": \"Giriş kaydınız alınmıştır\","
                + "\"action\": \"check_in\","
                + "\"time\": \"08:30\""
                + "}";
    }

    public static String checkOutSuccess() {
        return "{"
                + "\"success\": true,"
                + "\"message\": \"Çıkış kaydınız alınmıştır\","
                + "\"action\": \"check_out\","
                + "\"time\": \"17:35\""
                + "}";
    }

    // ==================== GENERIC ====================

    public static String approveSuccess() {
        return "{\"success\": true, \"message\": \"İşlem başarıyla tamamlandı\"}";
    }

    public static String submitSuccess() {
        return "{\"success\": true, \"message\": \"Talebiniz başarıyla gönderildi\"}";
    }

    public static String personnelList() {
        return "["
                + "{\"id\":1,\"name\":\"Zeynep Kaya\",\"department\":\"Yazılım Geliştirme\",\"check_in\":\"08:25\",\"check_out\":null,\"status\":\"active\"},"
                + "{\"id\":2,\"name\":\"Emre Yıldız\",\"department\":\"Yazılım Geliştirme\",\"check_in\":\"08:52\",\"check_out\":null,\"status\":\"late\"},"
                + "{\"id\":3,\"name\":\"Murat Aydın\",\"department\":\"Yazılım Geliştirme\",\"check_in\":\"08:30\",\"check_out\":null,\"status\":\"active\"},"
                + "{\"id\":4,\"name\":\"Selin Arslan\",\"department\":\"İnsan Kaynakları\",\"check_in\":\"09:15\",\"check_out\":null,\"status\":\"late\"},"
                + "{\"id\":5,\"name\":\"Ali Veli Öztürk\",\"department\":\"Muhasebe\",\"check_in\":\"09:08\",\"check_out\":null,\"status\":\"late\"},"
                + "{\"id\":6,\"name\":\"Fatma Şahin\",\"department\":\"Satış & Pazarlama\",\"check_in\":\"08:28\",\"check_out\":null,\"status\":\"active\"},"
                + "{\"id\":7,\"name\":\"Burak Çelik\",\"department\":\"Üretim\",\"check_in\":\"07:35\",\"check_out\":null,\"status\":\"late\"},"
                + "{\"id\":8,\"name\":\"Hakan Korkmaz\",\"department\":\"Üretim\",\"check_in\":\"07:00\",\"check_out\":\"16:30\",\"status\":\"early\"},"
                + "{\"id\":9,\"name\":\"Ayşe Güneş\",\"department\":\"Satış & Pazarlama\",\"check_in\":\"08:30\",\"check_out\":\"17:20\",\"status\":\"early\"},"
                + "{\"id\":10,\"name\":\"Oğuz Kara\",\"department\":\"Lojistik\",\"check_in\":\"08:20\",\"check_out\":null,\"status\":\"late\"},"
                + "{\"id\":11,\"name\":\"Deniz Polat\",\"department\":\"Muhasebe\",\"check_in\":\"08:30\",\"check_out\":null,\"status\":\"active\"},"
                + "{\"id\":12,\"name\":\"Cem Yalçın\",\"department\":\"Lojistik\",\"check_in\":\"08:00\",\"check_out\":null,\"status\":\"active\"},"
                + "{\"id\":13,\"name\":\"Elif Demir\",\"department\":\"İnsan Kaynakları\",\"check_in\":null,\"check_out\":null,\"status\":\"on_leave\"},"
                + "{\"id\":14,\"name\":\"Serkan Acar\",\"department\":\"Üretim\",\"check_in\":null,\"check_out\":null,\"status\":\"on_leave\"},"
                + "{\"id\":15,\"name\":\"Gül Erdem\",\"department\":\"Satış & Pazarlama\",\"check_in\":null,\"check_out\":null,\"status\":\"absent\"},"
                + "{\"id\":16,\"name\":\"Tolga Şen\",\"department\":\"Yazılım Geliştirme\",\"check_in\":null,\"check_out\":null,\"status\":\"absent\"},"
                + "{\"id\":17,\"name\":\"Merve Koç\",\"department\":\"Muhasebe\",\"check_in\":\"08:30\",\"check_out\":null,\"status\":\"active\"},"
                + "{\"id\":18,\"name\":\"Volkan Taş\",\"department\":\"Üretim\",\"check_in\":\"07:02\",\"check_out\":null,\"status\":\"active\"},"
                + "{\"id\":19,\"name\":\"Pınar Özkan\",\"department\":\"Lojistik\",\"check_in\":null,\"check_out\":null,\"status\":\"on_leave\"},"
                + "{\"id\":20,\"name\":\"Ufuk Kılıç\",\"department\":\"Satış & Pazarlama\",\"check_in\":\"08:35\",\"check_out\":null,\"status\":\"active\"}"
                + "]";
    }

    public static String approvedAnnualLeaves() {
        return "["
                + "{\"id\":7001,\"personnel_name\":\"Elif Demir\",\"department\":\"İnsan Kaynakları\","
                + "\"leave_type\":\"yillik\",\"start_date\":\"10.02.2026\",\"end_date\":\"14.02.2026\","
                + "\"start_time\":null,\"end_time\":null,\"reason\":\"Tatil\","
                + "\"status\":\"approved\",\"request_date\":\"05.02.2026\",\"remaining_days\":10},"
                + "{\"id\":7002,\"personnel_name\":\"Serkan Acar\",\"department\":\"Üretim\","
                + "\"leave_type\":\"yillik\",\"start_date\":\"17.02.2026\",\"end_date\":\"18.02.2026\","
                + "\"start_time\":null,\"end_time\":null,\"reason\":\"Kişisel\","
                + "\"status\":\"approved\",\"request_date\":\"12.02.2026\",\"remaining_days\":7}"
                + "]";
    }

    public static String rejectedAnnualLeaves() {
        return "["
                + "{\"id\":8001,\"personnel_name\":\"Tolga Şen\",\"department\":\"Yazılım Geliştirme\","
                + "\"leave_type\":\"yillik\",\"start_date\":\"01.02.2026\",\"end_date\":\"10.02.2026\","
                + "\"start_time\":null,\"end_time\":null,\"reason\":\"Uzun tatil talebi\","
                + "\"status\":\"rejected\",\"request_date\":\"25.01.2026\",\"remaining_days\":5}"
                + "]";
    }

    public static String approvedHourlyLeaves() {
        return "["
                + "{\"id\":7101,\"personnel_name\":\"Merve Koç\",\"department\":\"Muhasebe\","
                + "\"leave_type\":\"saatlik\",\"start_date\":\"19.02.2026\",\"end_date\":\"19.02.2026\","
                + "\"start_time\":\"10:00\",\"end_time\":\"12:00\",\"reason\":\"Doktor randevusu\","
                + "\"status\":\"approved\",\"request_date\":\"18.02.2026\",\"remaining_days\":13},"
                + "{\"id\":7102,\"personnel_name\":\"Volkan Taş\",\"department\":\"Üretim\","
                + "\"leave_type\":\"saatlik\",\"start_date\":\"18.02.2026\",\"end_date\":\"18.02.2026\","
                + "\"start_time\":\"15:00\",\"end_time\":\"17:00\",\"reason\":\"Araba servisi\","
                + "\"status\":\"approved\",\"request_date\":\"17.02.2026\",\"remaining_days\":8}"
                + "]";
    }

    public static String rejectedHourlyLeaves() {
        return "["
                + "{\"id\":8101,\"personnel_name\":\"Ufuk Kılıç\",\"department\":\"Satış & Pazarlama\","
                + "\"leave_type\":\"saatlik\",\"start_date\":\"15.02.2026\",\"end_date\":\"15.02.2026\","
                + "\"start_time\":\"09:00\",\"end_time\":\"13:00\",\"reason\":\"4 saat fazla\","
                + "\"status\":\"rejected\",\"request_date\":\"14.02.2026\",\"remaining_days\":12}"
                + "]";
    }

    public static String approvedAdvances() {
        return "["
                + "{\"id\":7201,\"personnel_name\":\"Pınar Özkan\",\"department\":\"Lojistik\","
                + "\"amount\":2500.00,\"reason\":\"Ev eşyası\","
                + "\"status\":\"approved\",\"request_date\":\"10.02.2026\"},"
                + "{\"id\":7202,\"personnel_name\":\"Gül Erdem\",\"department\":\"Satış & Pazarlama\","
                + "\"amount\":4000.00,\"reason\":\"Sağlık harcaması\","
                + "\"status\":\"approved\",\"request_date\":\"08.02.2026\"}"
                + "]";
    }

    public static String rejectedAdvances() {
        return "["
                + "{\"id\":8201,\"personnel_name\":\"Tolga Şen\",\"department\":\"Yazılım Geliştirme\","
                + "\"amount\":15000.00,\"reason\":\"Araba alımı — limit aşımı\","
                + "\"status\":\"rejected\",\"request_date\":\"05.02.2026\"}"
                + "]";
    }

// ==================== GÜNLÜK İZİN ====================

    public static String pendingDailyLeaves() {
        return "["
                + "{\"id\":4001,\"personnel_name\":\"Volkan Taş\",\"department\":\"Üretim\","
                + "\"leave_type\":\"gunluk\",\"start_date\":\"24.02.2026\",\"end_date\":\"24.02.2026\","
                + "\"start_time\":null,\"end_time\":null,\"reason\":\"Kişisel iş\","
                + "\"status\":\"pending\",\"request_date\":\"21.02.2026\",\"remaining_days\":0},"
                + "{\"id\":4002,\"personnel_name\":\"Merve Koç\",\"department\":\"Muhasebe\","
                + "\"leave_type\":\"gunluk\",\"start_date\":\"25.02.2026\",\"end_date\":\"25.02.2026\","
                + "\"start_time\":null,\"end_time\":null,\"reason\":\"Hastane kontrolü\","
                + "\"status\":\"pending\",\"request_date\":\"21.02.2026\",\"remaining_days\":0}"
                + "]";
    }

    public static String approvedDailyLeaves() {
        return "["
                + "{\"id\":7301,\"personnel_name\":\"Ufuk Kılıç\",\"department\":\"Satış & Pazarlama\","
                + "\"leave_type\":\"gunluk\",\"start_date\":\"20.02.2026\",\"end_date\":\"20.02.2026\","
                + "\"start_time\":null,\"end_time\":null,\"reason\":\"Aile işi\","
                + "\"status\":\"approved\",\"request_date\":\"19.02.2026\",\"remaining_days\":0}"
                + "]";
    }

    public static String rejectedDailyLeaves() {
        return "["
                + "{\"id\":8301,\"personnel_name\":\"Cem Yalçın\",\"department\":\"Lojistik\","
                + "\"leave_type\":\"gunluk\",\"start_date\":\"18.02.2026\",\"end_date\":\"18.02.2026\","
                + "\"start_time\":null,\"end_time\":null,\"reason\":\"Mazeret belirtilmedi\","
                + "\"status\":\"rejected\",\"request_date\":\"17.02.2026\",\"remaining_days\":0}"
                + "]";
    }
}