package com.rxvlvxr.clinic.dtos;

import java.time.LocalDateTime;

public class DoctorTimeResponse {
    private LocalDateTime time;

    public DoctorTimeResponse() {
    }


    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

}
