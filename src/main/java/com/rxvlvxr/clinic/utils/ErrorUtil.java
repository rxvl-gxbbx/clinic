package com.rxvlvxr.clinic.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ErrorUtil {
    /**
     * метод для генерации сообщений об ошибке
     *
     * @param bindingResult передается объект типа BindingResult
     * @return возвращается строка об ошибке
     */
    public static String getErrorMsg(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();

        for (FieldError error : errors)
            errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");

        return errorMessage.toString();
    }
}
