package com.rxvlvxr.clinic.utils;

public class DoctorNotFoundException extends RuntimeException {
    private final String message;

    public DoctorNotFoundException() {
        message = "Доктор не найден";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
