package com.rxvlvxr.clinic.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class DoctorDateRequest {
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Поле не может быть пустым")
    private LocalDate date;

    public DoctorDateRequest() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
