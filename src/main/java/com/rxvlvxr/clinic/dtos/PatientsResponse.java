package com.rxvlvxr.clinic.dtos;

import java.util.List;

public class PatientsResponse {
    private List<PatientDTO> patients;

    public PatientsResponse() {
    }

    public PatientsResponse(List<PatientDTO> patients) {
        this.patients = patients;
    }

    public List<PatientDTO> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientDTO> patients) {
        this.patients = patients;
    }
}
