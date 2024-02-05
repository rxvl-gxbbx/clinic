package com.rxvlvxr.clinic.services;

import com.rxvlvxr.clinic.models.Appointment;
import com.rxvlvxr.clinic.models.Doctor;
import com.rxvlvxr.clinic.models.Patient;
import com.rxvlvxr.clinic.repositories.AppointmentRepository;
import com.rxvlvxr.clinic.repositories.DoctorRepository;
import com.rxvlvxr.clinic.repositories.PatientRepository;
import com.rxvlvxr.clinic.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository, PatientService patientService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.patientService = patientService;
    }

    /**
     * метод для автоматической генерации расписания
     * при запуске метода все талоны, существующие с начала переданной даты будут удалены (!)
     *
     * @param fromDate      дата начала генерации расписания
     * @param durationInMin продолжительность приема
     * @param daysAmount    на какое количество дней создать талоны
     */
    @Transactional
    public void autoGenerate(LocalDate fromDate, int durationInMin, int daysAmount) {
        // находит всех врачей, отсортированных по смене
        List<Doctor> doctors = doctorRepository.findAll(Sort.by("shift"));
        // если врачей нет, то выбрасывается исключение
        if (doctors.isEmpty()) throw new DoctorNotFoundException();
        // получаем максимально допустимое количество талонов для создания
        final int MAX_APPOINTMENTS = 9 * 60 / durationInMin;
        // так как это автоматическая генерация, то мы проходимся по всем доступным врачам для удобства создания
        for (int i = 0; i < daysAmount; i++) {
            for (Doctor doctor : doctors) {
                // в зависимости от смены врача устанавливается начало рабочей смены
                LocalDateTime currentTime = ShiftUtil.getShiftBegin(fromDate, doctor);
                // в зависимости от смены врача устанавливается конец рабочей смены
                LocalDateTime shiftEnd = ShiftUtil.getShiftEnd(fromDate, doctor);
                // удаляем все предыдущие записи в заданный промежуток пакетным удалением (Batch)
                appointmentRepository.deleteAllInBatch(appointmentRepository.findAllByTimeBetween(fromDate.atStartOfDay(), fromDate.plusDays(1).atStartOfDay()));
                // добавляем максимальное количество талонов на день
                for (int j = 0; j < MAX_APPOINTMENTS; j++) {
                    currentTime = saveAndGetTime(durationInMin, currentTime, shiftEnd, doctor);
                    // если currentTime == null, значит достигнут предел времени
                    if (currentTime == null) break;
                }
            }
            // переходим к следующему дню
            fromDate = fromDate.plusDays(1);
        }
    }

    /**
     * метод для более тонкой настройки генерации расписания для врача
     * работает только для одного дня и одного указанного врача
     *
     * @param doctorId           указываем ID врача
     * @param dateTimeFrom       указываем время отсчета с начала генерации талонов
     * @param durationInMin      указываем длительность приема
     * @param appointmentsAmount указываем количество талонов для создания
     * @param clearBefore        уточняем нужно ли удалять записи в текущий день перед созданием нового расписания
     */
    @Transactional
    public void generateByRules(long doctorId, LocalDateTime dateTimeFrom, int durationInMin, int appointmentsAmount, boolean clearBefore) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);

        if (optionalDoctor.isEmpty()) throw new DoctorNotFoundException();

        Doctor doctor = optionalDoctor.get();
        // если передано значение true, то удаляются все строки таблицы на тот день, который был передан
        if (clearBefore)
            // удаляем Batch методом, т.е. одним запросом
            appointmentRepository.deleteAllInBatch(appointmentRepository.findAllByTimeBetween(dateTimeFrom.truncatedTo(ChronoUnit.DAYS), dateTimeFrom.plusDays(1).truncatedTo(ChronoUnit.DAYS)));

        LocalDateTime currentTime = dateTimeFrom.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime shiftEnd = ShiftUtil.getShiftEnd(LocalDate.from(dateTimeFrom), doctor);

        for (int i = 0; i < appointmentsAmount; i++) {
            currentTime = saveAndGetTime(durationInMin, currentTime, shiftEnd, doctor);

            if (currentTime == null) break;
        }
    }

    /**
     * метод сохраняющий объект врача в таблицу и возврающий время следующего талона доступного для создания
     *
     * @param durationInMin передаем продолжительность приема врача
     * @param currentTime   передаем время от которого пойдет отсчет
     * @param shiftEnd      передаем время когда смена врача заканчивается
     * @param doctor        передаем объект врача для того чтобы узнать в какой смене он работает
     * @return возвращает время для создания следующего талона
     */
    @Transactional
    protected LocalDateTime saveAndGetTime(int durationInMin, LocalDateTime currentTime, LocalDateTime shiftEnd, Doctor doctor) {
        if (currentTime.isAfter(shiftEnd) || currentTime.isEqual(shiftEnd)) return null;

        Appointment appointment = new Appointment();

        appointment.setDoctor(doctor);
        appointment.setTime(currentTime);
        doctor.getAppointments().add(appointment);

        appointmentRepository.save(appointment);

        currentTime = currentTime.plusMinutes(durationInMin);

        return currentTime;
    }

    /**
     * метод для записи по талону
     *
     * @param id          передается ID талона
     * @param patientUuid передается UUID пациента
     */
    @Transactional
    public void reserveTime(long id, UUID patientUuid) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        optionalAppointment.ifPresentOrElse(appointment -> {
            // проверяем не занят ли талон
            if (appointment.getPatient() != null) {
                throw new AppointmentNotAvailableException("Извините, талон уже занят");
            }

            Optional<Patient> optionalPatient = patientRepository.findByUuid(patientUuid);

            optionalPatient.ifPresentOrElse(patient -> {
                // проверяем есть ли у данного пациента талон к врачу на этот день
                boolean isAvailable = patientService.findAllAppointmentsByUuid(patientUuid).stream().noneMatch(value -> value.getTime().truncatedTo(ChronoUnit.DAYS).equals(appointment.getTime().truncatedTo(ChronoUnit.DAYS)) && value.getDoctor().getId() == appointment.getDoctor().getId());

                if (!isAvailable) throw new AppointmentNotAvailableException("Уже есть запись к выбранному врачу");
                // устанавливаем объект пациента в талон
                appointment.setPatient(patient);
                // устанавливаем двустороннюю связь
                patient.getAppointments().add(appointment);

                appointmentRepository.save(appointment);
            }, () -> {
                // если пациент не найден
                throw new PatientNotFoundException();
            });
        }, () -> {
            throw new AppointmentNotFoundException();
        });
    }

    /**
     * метод для создания талона в БД
     *
     * @param appointment передается объект талона
     */
    @Transactional
    public void create(Appointment appointment) {
        // ищем объект врача в БД по переданному Doctor uuid внутри объекта Appointment
        Optional<Doctor> optionalDoctor = doctorRepository.findByUuid(appointment.getDoctor().getUuid());

        if (optionalDoctor.isEmpty()) throw new DoctorNotFoundException();
        // без врача талон создать невозможно
        Doctor existed = optionalDoctor.get();
        if (!ShiftUtil.isAvailable(appointment.getTime(), existed))
            throw new AppointmentNotCreatedException("Невозможно создать талон! Врач работает c " + ShiftUtil.getShiftBegin(LocalDate.from(appointment.getTime()), existed).getHour() + " до " + ShiftUtil.getShiftEnd(LocalDate.from(appointment.getTime()), existed).getHour());
        appointment.setDoctor(existed);
        // устанавливаем двустороннюю связь
        existed.getAppointments().add(appointment);

        appointmentRepository.save(appointment);
    }

    /**
     * метод для получения всех талонов
     *
     * @return список объектов типа Appointment
     */
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    /**
     * метод для нахождения талона по ID
     *
     * @param id передается ID талона
     * @return объект типа Appointment
     */
    public Appointment findById(long id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isEmpty()) throw new AppointmentNotFoundException();

        return optionalAppointment.get();
    }

    /**
     * метод для обновления талона на случай если врач не сможет провести прием
     *
     * @param id                 передается ID талона
     * @param updatedAppointment передается обновленный объект типа Appointment
     */
    @Transactional
    public void update(long id, Appointment updatedAppointment) {
        updatedAppointment.setId(id);

        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isEmpty()) throw new AppointmentNotFoundException();

        Appointment existedAppointment = optionalAppointment.get();
        Optional<Doctor> optionalDoctor = doctorRepository.findByUuid(updatedAppointment.getDoctor().getUuid());

        if (optionalDoctor.isEmpty()) throw new DoctorNotFoundException();

        Doctor doctor = optionalDoctor.get();

        if (!ShiftUtil.isAvailable(updatedAppointment.getTime(), doctor))
            throw new AppointmentNotCreatedException("Невозможно обновить талон! Врач работает c " + ShiftUtil.getShiftBegin(LocalDate.from(updatedAppointment.getTime()), doctor).getHour() + " до " + ShiftUtil.getShiftEnd(LocalDate.from(updatedAppointment.getTime()), doctor).getHour());
        // если обновленному талону не было передано значения пациента, то остается прежний пациент
        if (updatedAppointment.getPatient() == null && existedAppointment.getPatient() != null)
            updatedAppointment.setPatient(existedAppointment.getPatient());
        // если все же пациент передан был, то происходит следующая логика
        if (updatedAppointment.getPatient() != null) {
            Optional<Patient> optionalPatient = patientRepository.findByUuid(updatedAppointment.getPatient().getUuid());
            // если такого пациента нет, то бросается исключение
            if (optionalPatient.isEmpty()) throw new PatientNotFoundException();

            Patient existedPatient = optionalPatient.get();
            // в ином случае устанавливаются связи между этими двумя объектами
            updatedAppointment.getPatient().setId(existedPatient.getId());
            existedPatient.getAppointments().remove(existedAppointment);
        }

        updatedAppointment.setDoctor(doctor);
        // устанавливаются двусторонние связи
        existedAppointment.getDoctor().getAppointments().remove(existedAppointment);
        if (existedAppointment.getPatient() != null)
            existedAppointment.getPatient().getAppointments().remove(existedAppointment);
        existedAppointment.setPatient(null);
        existedAppointment.setDoctor(null);

        appointmentRepository.save(updatedAppointment);
    }

    /**
     * метод для удаления записи в таблице appointment
     *
     * @param id передается ID талона
     */
    @Transactional
    public void delete(long id) {
        Optional<Appointment> optional = appointmentRepository.findById(id);

        if (optional.isEmpty()) throw new AppointmentNotFoundException();

        appointmentRepository.delete(optional.get());
    }
}