package com.rxvlvxr.clinic.controllers;

import com.rxvlvxr.clinic.dtos.*;
import com.rxvlvxr.clinic.models.Appointment;
import com.rxvlvxr.clinic.models.Doctor;
import com.rxvlvxr.clinic.services.DoctorService;
import com.rxvlvxr.clinic.utils.DoctorNotCreatedException;
import com.rxvlvxr.clinic.utils.ErrorResponse;
import com.rxvlvxr.clinic.utils.ErrorUtil;
import com.rxvlvxr.clinic.utils.IncorrectDateException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.Collectors;

// REST сервис
@RestController
@RequestMapping("/rest/doctors")
public class DoctorsController {

    private final DoctorService doctorService;
    private final ModelMapper modelMapper;

    @Autowired
    public DoctorsController(DoctorService doctorService, ModelMapper modelMapper) {
        this.doctorService = doctorService;
        this.modelMapper = modelMapper;
    }

    /**
     * метод для получения всех слотов времени по UUID врача
     * POST был выбран для удобства передачи UUID в теле запроса
     *
     * @param uuid    передается UUID врача из URL
     * @param request передается объект DTO типа DoctorDateRequest, где в качестве запроса передается
     *                ключ - "date"
     *                значение - "dd/MM/yyyy"
     * @return возвращает объект DoctorAppointmentsResponse (DTO),
     * где в виде response предоставляются все свободные слоты времени данного врача
     */
    @PostMapping("/{uuid}")
    public DoctorAppointmentsResponse getAllAppointments(@PathVariable("uuid") String uuid, @RequestBody @Valid DoctorDateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new IncorrectDateException("Введите корректную дату");

        LocalDate date = request.getDate();

        return new DoctorAppointmentsResponse
                (
                        doctorService.getVacantTime(UUID.fromString(uuid), date).stream()
                                .map(this::convertToTimeResponse)
                                .collect(Collectors.toList())
                );
    }

    // CRUD был добавлен для удобства взаимодействия с БД
    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid DoctorDTO doctorDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new DoctorNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        doctorService.save(convertToDoctor(doctorDTO));

        return ResponseEntity.status(HttpStatus.CREATED).body("Врач добавлен в базу данных");
    }

    @GetMapping
    public DoctorsResponse read() {
        return new DoctorsResponse(doctorService.findAll().stream()
                .map(this::convertToDoctorDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{uuid}")
    public DoctorDTO read(@PathVariable("uuid") String uuid) {
        return convertToDoctorDTO(doctorService.findByUuid(UUID.fromString(uuid)));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<String> update(@PathVariable("uuid") String uuid, @RequestBody @Valid DoctorDTO doctorDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new DoctorNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        doctorService.update(UUID.fromString(uuid), convertToDoctor(doctorDTO));

        return ResponseEntity.status(HttpStatus.OK).body("Данные успешно обновлены");
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> delete(@PathVariable("uuid") String uuid) {
        doctorService.delete(UUID.fromString(uuid));

        return ResponseEntity.status(HttpStatus.OK).body("Запись успешно удалена");
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(DoctorNotCreatedException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(IncorrectDateException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    private DoctorTimeResponse convertToTimeResponse(Appointment appointment) {
        return modelMapper.map(appointment, DoctorTimeResponse.class);
    }

    private Doctor convertToDoctor(DoctorDTO doctorDTO) {
        return modelMapper.map(doctorDTO, Doctor.class);
    }

    private DoctorDTO convertToDoctorDTO(Doctor doctor) {
        return modelMapper.map(doctor, DoctorDTO.class);
    }
}
