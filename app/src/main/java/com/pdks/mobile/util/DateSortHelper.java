package com.pdks.mobile.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Tarih string'lerine göre listeleri sıralayan yardımcı sınıf.
 * Format: "dd.MM.yyyy" (ör: "21.02.2026")
 * Varsayılan sıralama: yeniden eskiye (descending)
 *
 * java.time.LocalDate kullanır — immutable ve thread-safe.
 * minSdk 26 olduğu için ek desugaring gerekmez.
 */
public class DateSortHelper {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Herhangi bir listeyi, her elemandan tarih string'i çıkaran
     * bir fonksiyon ile yeniden eskiye sıralar.
     *
     * Kullanım:
     *   DateSortHelper.sortByDate(leaveList, LeaveRequest::getRequestDate);
     *   DateSortHelper.sortByDate(advanceList, AdvanceRequest::getRequestDate);
     *
     * @param list     Sıralanacak liste
     * @param dateFunc Her elemandan tarih string'i döndüren fonksiyon
     */
    public static <T> void sortByDate(List<T> list, DateExtractor<T> dateFunc) {
        if (list == null || list.size() <= 1) return;

        Collections.sort(list, (a, b) -> {
            LocalDate dateA = parseDate(dateFunc.getDate(a));
            LocalDate dateB = parseDate(dateFunc.getDate(b));

            if (dateA == null && dateB == null) return 0;
            if (dateA == null) return 1;  // null'lar sona
            if (dateB == null) return -1;

            // Descending — yeni tarih önce
            return dateB.compareTo(dateA);
        });
    }

    private static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Fonksiyonel interface — her model sınıfından tarih çıkarmak için.
     * Java 8 lambda ile kullanılabilir.
     */
    public interface DateExtractor<T> {
        String getDate(T item);
    }
}