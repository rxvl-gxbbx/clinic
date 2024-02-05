package com.rxvlvxr.clinic.utils;

public class PatientNotFoundException extends RuntimeException {
    private final String message;

    public PatientNotFoundException() {
        message = "Пациент не найден";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
