package com.rxvlvxr.clinic.dtos;

import com.rxvlvxr.clinic.models.Speciality;

public class PatientDoctorResponse {
    private String name;
    private Speciality speciality;
    private Long cabinet;

    public PatientDoctorResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public Long getCabinet() {
        return cabinet;
    }

    public void setCabinet(Long cabinet) {
        this.cabinet = cabinet;
    }
}
