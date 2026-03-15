package com.pdks.mobile;

import com.pdks.mobile.api.ApiIntegrationTest;
import com.pdks.mobile.constants.ConstantsValidationTest;
import com.pdks.mobile.model.AttendanceRecordTest;
import com.pdks.mobile.model.LateEarlyRecordTest;
import com.pdks.mobile.model.LeaveRequestTest;
import com.pdks.mobile.model.LoginSerializationTest;
import com.pdks.mobile.model.PersonnelInfoTest;
import com.pdks.mobile.util.DateSortHelperTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  OnlinePDKS — Otonom Test Suite                              ║
 * ║                                                              ║
 * ║  Tüm testleri tek komutla çalıştırır:                       ║
 * ║  ./gradlew test                                              ║
 * ║  veya sadece suite:                                          ║
 * ║  ./gradlew test --tests "com.pdks.mobile.AllTestsSuite"      ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * Test Katmanları:
 *
 * 1. MODEL TESTLERİ (4 sınıf)
 *    - AttendanceRecordTest  → getStatusDisplay(), equals, hashCode, Gson
 *    - LeaveRequestTest      → getLeaveTypeDisplay(), equals, Gson
 *    - PersonnelInfoTest     → getStatusDisplay(), null safety
 *    - LateEarlyRecordTest   → getTypeDisplay(), Gson
 *
 * 2. SERİALİZATİON TESTLERİ (1 sınıf)
 *    - LoginSerializationTest → LoginRequest/Response, CheckInOut, ApiResponse JSON
 *
 * 3. UTİL TESTLERİ (1 sınıf)
 *    - DateSortHelperTest    → sıralama, null handling, edge case'ler
 *
 * 4. CONSTANTS TESTLERİ (1 sınıf)
 *    - ConstantsValidationTest → 8 sabit sınıfı: null/boş, lowercase, unique, tutarlılık
 *
 * 5. API ENTEGRASYON TESTLERİ (1 sınıf)
 *    - ApiIntegrationTest    → MockWebServer ile 20+ endpoint testi:
 *      login (patron/personel/hatalı/403), dashboard, departmanlar,
 *      personel listesi, izin talepleri (3 tip × 3 durum), avans,
 *      onay/red, mesai raporu, günlük/haftalık/aylık rapor,
 *      check-in/out (konum/QR), hata durumları (500, 401, boş yanıt)
 *
 * Toplam: ~80 test metodu
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Katman 1: Model
        AttendanceRecordTest.class,
        LeaveRequestTest.class,
        PersonnelInfoTest.class,
        LateEarlyRecordTest.class,

        // Katman 2: Serialization
        LoginSerializationTest.class,

        // Katman 3: Utility
        DateSortHelperTest.class,

        // Katman 4: Constants
        ConstantsValidationTest.class,

        // Katman 5: API Integration
        ApiIntegrationTest.class
})
public class AllTestsSuite {
    // Suite runner — test metodu içermez, sadece sınıfları toplar.
}
