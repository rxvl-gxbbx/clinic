package com.rxvlvxr.clinic.dtos;

import java.util.List;

public class DoctorsResponse {
    private List<DoctorDTO> doctors;

    public DoctorsResponse() {
    }

    public DoctorsResponse(List<DoctorDTO> doctors) {
        this.doctors = doctors;
    }

    public List<DoctorDTO> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<DoctorDTO> doctors) {
        this.doctors = doctors;
    }
}
