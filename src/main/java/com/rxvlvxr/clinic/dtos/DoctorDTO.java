package com.rxvlvxr.clinic.dtos;

import com.rxvlvxr.clinic.models.Shift;
import com.rxvlvxr.clinic.models.Speciality;
import jakarta.validation.constraints.*;

import java.util.UUID;

public class DoctorDTO {
    private UUID uuid;
    @Size(min = 2, max = 255, message = "ФИО должно быть в диапазоне от 5 до 255 символов")
    @NotBlank(message = "ФИО не может быть пустым")
    @Pattern(regexp = "[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+\\s[А-ЯЁ][а-яё]+", message = "Убедитесь, что имя соответствует шаблону: Фамилия Имя Отчество")
    private String name;
    @NotNull(message = "Поле не может быть пустым")
    private Shift shift;
    @NotNull(message = "Поле не может быть пустым")
    private Speciality speciality;
    // допустим у нас 5-этажное здание, где первая цифра это этаж, а остальные номер кабинета
    @Min(value = 100, message = "Номер кабинета не может быть ниже 100")
    @Max(value = 500, message = "Номер кабинета не может быть выше 500")
    @NotNull(message = "Поле не должно быть пустым")
    private Integer cabinet;

    public DoctorDTO() {
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

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public Integer getCabinet() {
        return cabinet;
    }

    public void setCabinet(Integer cabinet) {
        this.cabinet = cabinet;
    }
}
