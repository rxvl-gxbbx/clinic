package com.rxvlvxr.clinic.utils;

import com.rxvlvxr.clinic.models.Doctor;
import com.rxvlvxr.clinic.models.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ShiftUtil {
    /**
     * получаем время начала смены для данного врача
     *
     * @param fromDate передаем дату для дальнейших манипуляций
     * @param doctor   передаем объект врача, чтобы понять в какой смене он работает
     * @return возвращает объект типа LocalDateTime - время начала смены врача
     */
    public static LocalDateTime getShiftBegin(LocalDate fromDate, Doctor doctor) {
        LocalDateTime start;

        if (doctor.getShift().equals(Shift.MORNING)) start = fromDate.atTime(7, 0);
        else if (doctor.getShift().equals(Shift.DAY)) start = fromDate.atTime(10, 0);
        else start = fromDate.atTime(14, 0);

        return start;
    }

    /**
     * метод возвращающий конца семны для данного врача
     *
     * @param fromDate передаем дату для дальнейших манипуляций
     * @param doctor   передаем объект врача чтобы узнать в какой смене он работает
     * @return возвращает время конца смены врача
     */
    public static LocalDateTime getShiftEnd(LocalDate fromDate, Doctor doctor) {
        LocalDateTime end;

        if (doctor.getShift().equals(Shift.MORNING)) end = fromDate.atTime(16, 0);
        else if (doctor.getShift().equals(Shift.DAY)) end = fromDate.atTime(19, 0);
        else end = fromDate.atTime(23, 0);

        return end;
    }

    /**
     * метод проверяющий доступно ли время для записи к врачу
     *
     * @param dateTimeFrom передаем время, которое ввели через сервис (на нем будет проходить валидация)
     * @param doctor       передаем объект врача, чтобы узнать смену в которой он работает
     * @return возвращает объект типа boolean: true = запись доступна, false = запись недоступна
     */
    public static boolean isAvailable(LocalDateTime dateTimeFrom, Doctor doctor) {
        boolean available = !doctor.getShift().equals(Shift.MORNING) || (!dateTimeFrom.isBefore(dateTimeFrom.truncatedTo(ChronoUnit.DAYS).plusHours(7)) && !dateTimeFrom.isAfter(dateTimeFrom.truncatedTo(ChronoUnit.DAYS).plusHours(16)));

        if (doctor.getShift().equals(Shift.DAY) && (dateTimeFrom.isBefore(dateTimeFrom.truncatedTo(ChronoUnit.DAYS).plusHours(10)) || dateTimeFrom.isAfter(dateTimeFrom.truncatedTo(ChronoUnit.DAYS).plusHours(19))))
            available = false;
        if (doctor.getShift().equals(Shift.EVENING) && (dateTimeFrom.isBefore(dateTimeFrom.truncatedTo(ChronoUnit.DAYS).plusHours(14)) || dateTimeFrom.isAfter(dateTimeFrom.truncatedTo(ChronoUnit.DAYS).plusHours(23))))
            available = false;

        return available;
    }
}
