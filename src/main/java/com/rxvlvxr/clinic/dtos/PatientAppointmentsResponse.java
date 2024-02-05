package com.rxvlvxr.clinic.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PatientAppointmentsResponse {
    @JsonProperty("patient_appointments")
    private List<PatientAppointmentDTO> patientAppointment;

    public PatientAppointmentsResponse() {
    }

    public PatientAppointmentsResponse(List<PatientAppointmentDTO> patientAppointment) {
        this.patientAppointment = patientAppointment;
    }

    public List<PatientAppointmentDTO> getPatientAppointment() {
        return patientAppointment;
    }

    public void setPatientAppointment(List<PatientAppointmentDTO> patientAppointment) {
        this.patientAppointment = patientAppointment;
    }
}
