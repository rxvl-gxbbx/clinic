package com.rxvlvxr.clinic.utils;

public class AppointmentNotAvailableException extends RuntimeException {
    public AppointmentNotAvailableException(String msg) {
        super(msg);
    }
}
