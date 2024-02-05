package com.rxvlvxr.clinic.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AppointmentCreateRequest {
    @NotNull(message = "Требуется указать доктора")
    private AppointmentDoctorDTO doctor;

    @NotNull(message = "Требуется указать время")
    private LocalDateTime time;

    public AppointmentCreateRequest() {
    }

    public AppointmentDoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(AppointmentDoctorDTO doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
