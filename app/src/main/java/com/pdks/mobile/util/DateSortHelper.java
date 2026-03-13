package com.pdks.mobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Tarih string'lerine göre listeleri sıralayan yardımcı sınıf.
 * Format: "dd.MM.yyyy" (ör: "21.02.2026")
 * Varsayılan sıralama: yeniden eskiye (descending)
 */
public class DateSortHelper {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

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
            Date dateA = parseDate(dateFunc.getDate(a));
            Date dateB = parseDate(dateFunc.getDate(b));

            if (dateA == null && dateB == null) return 0;
            if (dateA == null) return 1;  // null'lar sona
            if (dateB == null) return -1;

            // Descending — yeni tarih önce
            return dateB.compareTo(dateA);
        });
    }

    private static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
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