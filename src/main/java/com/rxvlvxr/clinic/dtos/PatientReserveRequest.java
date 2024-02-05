package com.rxvlvxr.clinic.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PatientReserveRequest {
    @JsonProperty(value = "patient_uuid")
    @NotNull(message = "Поле не может быть пустым")
    private UUID patientUuid;

    public PatientReserveRequest() {
    }

    public UUID getPatientUuid() {
        return patientUuid;
    }

    public void setPatientUuid(UUID patientUuid) {
        this.patientUuid = patientUuid;
    }

}
