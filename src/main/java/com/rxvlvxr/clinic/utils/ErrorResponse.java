package com.rxvlvxr.clinic.utils;

import java.time.LocalDateTime;

// обертка для response об ошибке
public class ErrorResponse {
    private String message;
    private LocalDateTime time;

    public ErrorResponse(String message, LocalDateTime time) {
        this.message = message;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
