package com.rxvlvxr.clinic.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public class PatientDTO {
    private UUID uuid;
    @Size(min = 2, max = 255, message = "ФИО должно быть в диапазоне от 5 до 255 символов")
    @NotBlank(message = "ФИО не может быть пустым")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+", message = "Убедитесь, что имя соответствует шаблону: Фамилия Имя Отчество")
    private String name;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("date_of_birth")
    @NotNull(message = "Требуется указать дату рождения")
    private LocalDate dateOfBirth;
    @NotNull(message = "Поле не может быть пустым")
    private Boolean quarantine;
    @JsonProperty("phone_number")
    @NotBlank(message = "Поле не может быть пустым")
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$", message = "Неккоректно введен номер, попробуйте: +7 926 123 45 67")
    private String phoneNumber;
    @NotBlank(message = "Введите адрес")
    private String address;

    public PatientDTO() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getQuarantine() {
        return quarantine;
    }

    public void setQuarantine(Boolean quarantine) {
        this.quarantine = quarantine;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
