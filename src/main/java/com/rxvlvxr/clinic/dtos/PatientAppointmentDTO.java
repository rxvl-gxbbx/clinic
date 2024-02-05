package com.rxvlvxr.clinic.dtos;

import java.time.LocalDateTime;

public class PatientAppointmentDTO {
    private PatientDoctorResponse doctor;
    private LocalDateTime time;

    public PatientAppointmentDTO() {
    }

    public PatientDoctorResponse getDoctor() {
        return doctor;
    }

    public void setDoctor(PatientDoctorResponse doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
