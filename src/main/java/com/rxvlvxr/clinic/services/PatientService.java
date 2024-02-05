package com.rxvlvxr.clinic.services;

import com.rxvlvxr.clinic.models.Appointment;
import com.rxvlvxr.clinic.models.Patient;
import com.rxvlvxr.clinic.repositories.PatientRepository;
import com.rxvlvxr.clinic.utils.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PatientService {
    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * метод возвращающий все талоны, которые есть у пациента
     *
     * @param uuid передаем UUID пациента
     * @return возвращаем список объектов Appointment (все талоны, принадлежащие пациенту)
     */
    public List<Appointment> findAllAppointmentsByUuid(UUID uuid) {
        Optional<Patient> optionalPatient = patientRepository.findByUuid(uuid);

        if (optionalPatient.isEmpty()) throw new PatientNotFoundException();

        return optionalPatient.get().getAppointments();
    }

    // CRUD операции для удобства
    @Transactional
    public void create(Patient patient) {
        patient.setUuid(UUID.randomUUID());

        patientRepository.save(patient);
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findByUuid(UUID uuid) {
        Optional<Patient> optionalPatient = patientRepository.findByUuid(uuid);

        if (optionalPatient.isEmpty()) throw new PatientNotFoundException();

        return optionalPatient.get();
    }

    @Transactional
    public void update(UUID uuid, Patient updatedPatient) {
        Optional<Patient> optionalPatient = patientRepository.findByUuid(uuid);

        if (optionalPatient.isEmpty()) throw new PatientNotFoundException();

        Patient existed = optionalPatient.get();

        updatedPatient.setId(existed.getId());
        updatedPatient.setUuid(uuid);
        existed.getAppointments().forEach(appointment -> appointment.setPatient(updatedPatient));
        existed.getAppointments().clear();

        patientRepository.save(updatedPatient);
    }

    @Transactional
    public void delete(UUID uuid) {
        Optional<Patient> optionalPatient = patientRepository.findByUuid(uuid);

        if (optionalPatient.isEmpty()) throw new PatientNotFoundException();

        patientRepository.delete(optionalPatient.get());
    }

}
