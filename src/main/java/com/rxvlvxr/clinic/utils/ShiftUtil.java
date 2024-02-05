package com.rxvlvxr.clinic.utils;

import com.rxvlvxr.clinic.models.Doctor;
import com.rxvlvxr.clinic.models.Shift;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
}
