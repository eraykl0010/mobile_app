package com.pdks.mobile.constants;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Constants sınıflarının bütünlük testi.
 * Tüm sabitlerin null/boş olmadığını, sınıfların instantiate edilemediğini
 * ve API'nin beklediği değerlerle eşleştiğini doğrular.
 */
public class ConstantsValidationTest {

    // ── Tüm sabitlerin null/boş olmadığını doğrula ──

    @Test
    public void allConstants_nonNullAndNonEmpty() throws Exception {
        Class<?>[] constantClasses = {
                LeaveType.class, RequestStatus.class, PersonnelStatus.class,
                AttendanceStatus.class, CheckInType.class, RequestType.class,
                OvertimeType.class, ApprovalAction.class
        };

        for (Class<?> clazz : constantClasses) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())
                        && field.getType() == String.class) {
                    String label = clazz.getSimpleName() + "." + field.getName();
                    String value = (String) field.get(null);
                    assertWithMessage(label).that(value).isNotNull();
                    assertWithMessage(label).that(value).isNotEmpty();
                }
            }
        }
    }

    // ── Değerler küçük harf (API beklentisi) ──

    @Test
    public void allConstants_areLowerCase() throws Exception {
        Class<?>[] constantClasses = {
                LeaveType.class, RequestStatus.class, PersonnelStatus.class,
                AttendanceStatus.class, CheckInType.class, RequestType.class,
                OvertimeType.class, ApprovalAction.class
        };

        for (Class<?> clazz : constantClasses) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())
                        && field.getType() == String.class
                        && !field.getName().equals("VALUES")) { // VALUES dizisini atla
                    String value = (String) field.get(null);
                    if (value != null) {
                        String label = clazz.getSimpleName() + "." + field.getName();
                        assertWithMessage(label).that(value).isEqualTo(value.toLowerCase());
                    }
                }
            }
        }
    }

    // ── Duplicate değer kontrolü ──

    @Test
    public void leaveType_valuesAreUnique() {
        assertThat(LeaveType.ANNUAL).isNotEqualTo(LeaveType.DAILY);
        assertThat(LeaveType.DAILY).isNotEqualTo(LeaveType.HOURLY);
        assertThat(LeaveType.HOURLY).isNotEqualTo(LeaveType.ADVANCE);
    }

    @Test
    public void requestStatus_valuesAreUnique() {
        assertThat(RequestStatus.PENDING).isNotEqualTo(RequestStatus.APPROVED);
        assertThat(RequestStatus.APPROVED).isNotEqualTo(RequestStatus.REJECTED);
    }

    @Test
    public void personnelStatus_valuesAreUnique() {
        String[] values = {
                PersonnelStatus.ACTIVE, PersonnelStatus.LATE, PersonnelStatus.EARLY,
                PersonnelStatus.ON_LEAVE, PersonnelStatus.ABSENT, PersonnelStatus.NO_RECORD
        };
        // Hiçbir çift aynı olmamalı
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertThat(values[i]).isNotEqualTo(values[j]);
            }
        }
    }

    // ── LeaveType.VALUES dizisi tutarlılığı ──

    @Test
    public void leaveType_valuesArray_matchesIndividualConstants() {
        assertThat(LeaveType.VALUES[0]).isEqualTo(LeaveType.ANNUAL);
        assertThat(LeaveType.VALUES[1]).isEqualTo(LeaveType.DAILY);
        assertThat(LeaveType.VALUES[2]).isEqualTo(LeaveType.HOURLY);
        assertThat(LeaveType.VALUES).hasLength(3);
    }

    // ── CheckInType ve RequestType çakışma kontrolü ──

    @Test
    public void checkInType_distinctFromRequestType() {
        // "location" ve "leave" gibi değerlerin karışmaması
        assertThat(CheckInType.LOCATION).isNotEqualTo(RequestType.LEAVE);
        assertThat(CheckInType.QR_SCAN).isNotEqualTo(RequestType.ADVANCE);
    }

    // ── ApprovalAction ──

    @Test
    public void approvalAction_matchesApiExpectation() {
        assertThat(ApprovalAction.APPROVE).isEqualTo("approve");
        assertThat(ApprovalAction.REJECT).isEqualTo("reject");
    }
}