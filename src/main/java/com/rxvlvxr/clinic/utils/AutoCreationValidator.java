package com.rxvlvxr.clinic.utils;

import com.rxvlvxr.clinic.AutoCreation;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// валидатор для входных данных запроса с SOAP сервиса
@Component
public class AutoCreationValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return AutoCreation.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AutoCreation auto = (AutoCreation) target;

        final int minDaysAmount = 1;

        if (auto.getDaysAmount() < minDaysAmount)
            errors.rejectValue("daysAmount", "", "Укажите корректное количество дней! Минимальное - " + minDaysAmount);

        final int maxDuration = 45;
        final int minDuration = 20;

        if (auto.getDurationInMin() < minDuration || auto.getDurationInMin() > maxDuration)
            errors.rejectValue("durationInMin", "", "Продолжительность приема должна быть в промежутке с 20 до 45 минут");

        if (auto.getDateFrom() == null)
            errors.rejectValue("dateFrom", "", "Укажите корректную дату: yyyy-MM-dd");
    }
}
