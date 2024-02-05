package com.rxvlvxr.clinic.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    // устанавливаем связь с врачом
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "id", name = "doctor_id")
    private Doctor doctor;
    // устанавливаем связь с пациентом
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "patient_id")
    private Patient patient;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time")
    private LocalDateTime time;

    public Appointment() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
