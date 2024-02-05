package com.rxvlvxr.clinic.dtos;

import java.util.List;

public class AppointmentsResponse {
    public List<AppointmentDTO> appointments;

    public AppointmentsResponse() {
    }

    public AppointmentsResponse(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }
}
