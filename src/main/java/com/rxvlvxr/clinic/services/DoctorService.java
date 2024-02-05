package com.rxvlvxr.clinic.services;

import com.rxvlvxr.clinic.models.Appointment;
import com.rxvlvxr.clinic.models.Doctor;
import com.rxvlvxr.clinic.repositories.DoctorRepository;
import com.rxvlvxr.clinic.utils.DoctorNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DoctorService {
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * метод возвращающий свободные талоны к указанному врачу
     *
     * @param uuid передаем UUID врача
     * @param date передаем дату, которую хотим проверить
     * @return возвращаем список объектов Appointment (все свободные талоны врача)
     */
    public List<Appointment> getVacantTime(UUID uuid, LocalDate date) {
        Optional<Doctor> optional = doctorRepository.findByUuid(uuid);

        if (optional.isEmpty()) throw new DoctorNotFoundException();

        return optional.get().getAppointments().stream().filter(appointment -> appointment.getTime().toLocalDate().isEqual(date) && appointment.getPatient() == null).collect(Collectors.toList());
    }

    // CRUD операции для удобства
    @Transactional
    public void save(Doctor doctor) {
        doctor.setUuid(UUID.randomUUID());

        doctorRepository.save(doctor);
    }

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findByUuid(UUID uuid) {
        Optional<Doctor> optional = doctorRepository.findByUuid(uuid);

        if (optional.isEmpty()) throw new DoctorNotFoundException();

        return optional.get();
    }

    @Transactional
    public void update(UUID uuid, Doctor updatedDoctor) {
        Optional<Doctor> optional = doctorRepository.findByUuid(uuid);

        if (optional.isEmpty()) throw new DoctorNotFoundException();

        optional.ifPresent(existedDoctor -> {
            updatedDoctor.setId(existedDoctor.getId());
            updatedDoctor.setUuid(uuid);
            existedDoctor.getAppointments().forEach(appointment -> appointment.setDoctor(updatedDoctor));
            existedDoctor.getAppointments().clear();
        });

        doctorRepository.save(updatedDoctor);
    }

    @Transactional
    public void delete(UUID uuid) {
        Optional<Doctor> optionalDoctor = doctorRepository.findByUuid(uuid);

        if (optionalDoctor.isEmpty()) throw new DoctorNotFoundException();

        doctorRepository.delete(optionalDoctor.get());
    }

}
