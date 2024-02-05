package com.rxvlvxr.clinic.repositories;

import com.rxvlvxr.clinic.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    /**
     * метод для получения всех талонов в заданном промежутке времени
     *
     * @param start начало промежутка
     * @param end   конец промежутка
     * @return объекты типа Appointment
     */
    List<Appointment> findAllByTimeBetween(LocalDateTime start, LocalDateTime end);
}
