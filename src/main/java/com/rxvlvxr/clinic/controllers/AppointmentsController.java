package com.rxvlvxr.clinic.controllers;

import com.rxvlvxr.clinic.dtos.AppointmentCreateRequest;
import com.rxvlvxr.clinic.dtos.AppointmentDTO;
import com.rxvlvxr.clinic.dtos.AppointmentsResponse;
import com.rxvlvxr.clinic.dtos.PatientReserveRequest;
import com.rxvlvxr.clinic.models.Appointment;
import com.rxvlvxr.clinic.services.AppointmentService;
import com.rxvlvxr.clinic.utils.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

// REST сервис
@RestController
@RequestMapping("/rest/appointments")
public class AppointmentsController {
    private final AppointmentService appointmentService;
    private final ModelMapper modelMapper;

    @Autowired
    public AppointmentsController(AppointmentService appointmentService, ModelMapper modelMapper) {
        this.appointmentService = appointmentService;
        this.modelMapper = modelMapper;
    }

    /**
     * метод для записи к врачу
     *
     * @param id            передается ID талона из URL
     * @param request       передается объект PatientReserveRequest (DTO, Data Transfer Object), где в качестве запроса передается
     *                      ключ - "patient_uuid"
     *                      значение - "UUID"
     * @param bindingResult данный параметр нужен для валидации входных значений
     * @return возвращает response на сервер со статусом 201 (CREATED) с телом "Назначена запись"
     */
    @PatchMapping("/{id}/reserve")
    public ResponseEntity<String> reserve(@PathVariable("id") long id,
                                          @RequestBody @Valid PatientReserveRequest request,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new AppointmentNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        appointmentService.reserveTime(id, request.getPatientUuid());

        return ResponseEntity.status(HttpStatus.CREATED).body("Назначена запись");
    }

    // методы, представленные ниже нужны были для удобства, поэтому я добавил CRUD
    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid AppointmentCreateRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new AppointmentNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        appointmentService.create(convertToAppointment(request));

        return ResponseEntity.status(HttpStatus.CREATED).body("Талон создан");
    }

    @GetMapping
    public AppointmentsResponse read() {
        return new AppointmentsResponse(appointmentService.findAll().stream()
                .map(this::convertToAppointmentDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public AppointmentDTO read(@PathVariable("id") long id) {
        return convertToAppointmentDTO(appointmentService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable("id") long id,
                                         @RequestBody @Valid AppointmentDTO appointmentDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new AppointmentNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        appointmentService.update(id, convertToAppointment(appointmentDTO));

        return ResponseEntity.status(HttpStatus.CREATED).body("Талон успешно обновлен");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        appointmentService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body("Талон был успешно удален");
    }

    // ловим исключения, которые вызываются только в этом классе
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AppointmentNotAvailableException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AppointmentNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(AppointmentNotCreatedException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    // конвертация объектов из одного в другой
    private Appointment convertToAppointment(AppointmentCreateRequest request) {
        return modelMapper.map(request, Appointment.class);
    }

    private Appointment convertToAppointment(AppointmentDTO appointmentDTO) {
        return modelMapper.map(appointmentDTO, Appointment.class);
    }

    private AppointmentDTO convertToAppointmentDTO(Appointment appointment) {
        return modelMapper.map(appointment, AppointmentDTO.class);
    }
}
