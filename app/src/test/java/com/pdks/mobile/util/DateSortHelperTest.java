package com.pdks.mobile.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DateSortHelper testleri.
 * Sıralama doğruluğu, null handling, edge case'ler.
 */
public class DateSortHelperTest {

    /** Basit tarih tutacak test nesnesi */
    private static class Item {
        final String date;
        Item(String date) { this.date = date; }
    }

    private List<String> sortAndGetDates(String... dates) {
        List<Item> items = new ArrayList<>();
        for (String d : dates) items.add(new Item(d));
        DateSortHelper.sortByDate(items, item -> item.date);
        List<String> result = new ArrayList<>();
        for (Item item : items) result.add(item.date);
        return result;
    }

    // ── Temel sıralama ──

    @Test
    public void sortByDate_descendingOrder() {
        List<String> sorted = sortAndGetDates("01.01.2026", "15.03.2026", "10.02.2026");
        assertThat(sorted).containsExactly("15.03.2026", "10.02.2026", "01.01.2026").inOrder();
    }

    @Test
    public void sortByDate_alreadySorted_noChange() {
        List<String> sorted = sortAndGetDates("15.03.2026", "10.02.2026", "01.01.2026");
        assertThat(sorted).containsExactly("15.03.2026", "10.02.2026", "01.01.2026").inOrder();
    }

    @Test
    public void sortByDate_reverseSorted() {
        List<String> sorted = sortAndGetDates("01.01.2025", "01.06.2025", "01.12.2025");
        assertThat(sorted).containsExactly("01.12.2025", "01.06.2025", "01.01.2025").inOrder();
    }

    @Test
    public void sortByDate_sameDates_stableOrder() {
        List<String> sorted = sortAndGetDates("15.03.2026", "15.03.2026");
        assertThat(sorted).hasSize(2);
        assertThat(sorted.get(0)).isEqualTo("15.03.2026");
    }

    @Test
    public void sortByDate_crossYearBoundary() {
        List<String> sorted = sortAndGetDates("30.12.2025", "02.01.2026", "28.12.2025");
        assertThat(sorted).containsExactly("02.01.2026", "30.12.2025", "28.12.2025").inOrder();
    }

    // ── Null ve boş handling ──

    @Test
    public void sortByDate_nullList_noException() {
        DateSortHelper.sortByDate(null, item -> "");
        // Exception fırlatılmaması yeterli
    }

    @Test
    public void sortByDate_emptyList_noException() {
        List<Item> items = new ArrayList<>();
        DateSortHelper.sortByDate(items, item -> item.date);
        assertThat(items).isEmpty();
    }

    @Test
    public void sortByDate_singleItem_noException() {
        List<String> sorted = sortAndGetDates("15.03.2026");
        assertThat(sorted).containsExactly("15.03.2026");
    }

    @Test
    public void sortByDate_nullDates_movedToEnd() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(null));
        items.add(new Item("15.03.2026"));
        items.add(new Item(""));
        items.add(new Item("01.01.2026"));

        DateSortHelper.sortByDate(items, item -> item.date);

        // Geçerli tarihler önce (descending), null/boş sona
        assertThat(items.get(0).date).isEqualTo("15.03.2026");
        assertThat(items.get(1).date).isEqualTo("01.01.2026");
    }

    @Test
    public void sortByDate_invalidDateFormat_treatedAsNull() {
        List<Item> items = new ArrayList<>();
        items.add(new Item("geçersiz"));
        items.add(new Item("15.03.2026"));
        items.add(new Item("2026-03-15")); // Yanlış format

        DateSortHelper.sortByDate(items, item -> item.date);

        // Geçerli tarih en başta
        assertThat(items.get(0).date).isEqualTo("15.03.2026");
    }

    // ── Büyük liste ──

    @Test
    public void sortByDate_largeList_correctOrder() {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            items.add(new Item(String.format("%02d.01.2026", i)));
        }

        DateSortHelper.sortByDate(items, item -> item.date);

        assertThat(items.get(0).date).isEqualTo("31.01.2026");
        assertThat(items.get(30).date).isEqualTo("01.01.2026");
    }
}
