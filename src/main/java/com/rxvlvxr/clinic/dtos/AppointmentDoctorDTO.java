package com.rxvlvxr.clinic.dtos;

import java.util.UUID;

public class AppointmentDoctorDTO {
    private UUID uuid;

    public AppointmentDoctorDTO() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
