package com.rxvlvxr.clinic.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DoctorAppointmentsResponse {
    @JsonProperty("doctor_vacant_appointments")
    private List<DoctorTimeResponse> doctorAppointments;

    public DoctorAppointmentsResponse() {
    }

    public DoctorAppointmentsResponse(List<DoctorTimeResponse> doctorAppointments) {
        this.doctorAppointments = doctorAppointments;
    }

    public List<DoctorTimeResponse> getDoctorAppointments() {
        return doctorAppointments;
    }

    public void setDoctorAppointments(List<DoctorTimeResponse> doctorAppointments) {
        this.doctorAppointments = doctorAppointments;
    }
}
