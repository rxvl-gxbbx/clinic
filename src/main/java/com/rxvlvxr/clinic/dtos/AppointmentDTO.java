package com.rxvlvxr.clinic.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AppointmentDTO {
    @NotNull(message = "Требуется указать доктора")
    private DoctorDTO doctor;
    private PatientDTO patient;
    @NotNull(message = "Требуется указать время")
    private LocalDateTime time;

    public AppointmentDTO() {
    }

    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
