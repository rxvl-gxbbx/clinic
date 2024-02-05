package com.rxvlvxr.clinic.utils;

public class AppointmentNotFoundException extends RuntimeException {
    private final String message;

    public AppointmentNotFoundException() {
        message = "Талон не найден";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
