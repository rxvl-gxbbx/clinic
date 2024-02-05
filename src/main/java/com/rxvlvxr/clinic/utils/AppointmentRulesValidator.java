package com.rxvlvxr.clinic.utils;

import com.rxvlvxr.clinic.AppointmentRules;
import com.rxvlvxr.clinic.models.Doctor;
import com.rxvlvxr.clinic.repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.rxvlvxr.clinic.utils.ShiftUtil.isAvailable;

// валидатор для входных данных запроса с SOAP сервиса
@Component
public class AppointmentRulesValidator implements Validator {
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentRulesValidator(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AppointmentRules.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AppointmentRules rules = (AppointmentRules) target;
        final int maxDuration = 45;
        final int minDuration = 20;
        final LocalDateTime time = rules.getDateTimeFrom();

        int maxAppointments = 9 * 60 / minDuration;

        if (rules.getDurationInMin() < minDuration || rules.getDurationInMin() > maxDuration)
            errors.rejectValue("durationInMin", "", "Продолжительность приема должна быть в промежутке с 20 до 45 минут");
        else maxAppointments = 9 * 60 / rules.getDurationInMin();

        final int minAppointments = 1;

        if (rules.getAppointmentsAmount() < minAppointments || rules.getAppointmentsAmount() > maxAppointments)
            errors.rejectValue("appointmentsAmount", "", "Значение должно быть от " + minAppointments + " до " + maxAppointments);

        if (time == null) errors.rejectValue("dateTimeFrom", "", "Укажите корректное время: yyyy-MM-ddTHH:mm:ss");
        else {
            Optional<Doctor> optionalDoctor = doctorRepository.findById(rules.getDoctorId());

            optionalDoctor.ifPresent((doctor -> {
                if (!isAvailable(time, doctor))
                    errors.rejectValue("dateTimeFrom", "", "Невозможно сгенерировать расписание! Врач работает c " + ShiftUtil.getShiftBegin(LocalDate.from(time), doctor).getHour() + " до " + ShiftUtil.getShiftEnd(LocalDate.from(time), doctor).getHour());
            }));
        }
    }
}
