package com.rxvlvxr.clinic.controllers;

import com.rxvlvxr.clinic.dtos.PatientAppointmentDTO;
import com.rxvlvxr.clinic.dtos.PatientAppointmentsResponse;
import com.rxvlvxr.clinic.dtos.PatientDTO;
import com.rxvlvxr.clinic.dtos.PatientsResponse;
import com.rxvlvxr.clinic.models.Appointment;
import com.rxvlvxr.clinic.models.Patient;
import com.rxvlvxr.clinic.services.PatientService;
import com.rxvlvxr.clinic.utils.ErrorResponse;
import com.rxvlvxr.clinic.utils.ErrorUtil;
import com.rxvlvxr.clinic.utils.PatientNotCreatedException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.Collectors;

// REST сервис
@RestController
@RequestMapping("/rest/patients")
public class PatientsController {
    private final PatientService patientService;
    private final ModelMapper modelMapper;

    @Autowired
    public PatientsController(PatientService patientService, ModelMapper modelMapper) {
        this.patientService = patientService;
        this.modelMapper = modelMapper;
    }

    /**
     * метод для получения всех записей пациента по указанному UUID
     *
     * @param uuid передается UUID пациента
     * @return возвращается объект PatientAppointmentsResponse (DTO),
     * где в качестве response выступает список всех талонов указанного пациента, ограниченных DTO,
     * т.е. в ответе не будет ID врачей из БД, не будет данных о пациенте, т.к. это не нужно в данном response
     * response состоит из ограниченных DTO данных и времени талона
     */
    @GetMapping("/{uuid}/appointments")
    public PatientAppointmentsResponse getAllAppointments(@PathVariable("uuid") String uuid) {
        return new PatientAppointmentsResponse
                (
                        patientService.findAllAppointmentsByUuid(UUID.fromString(uuid)).stream()
                                .map(this::convertToPatientAppointmentDTO)
                                .collect(Collectors.toList())
                );
    }

    // методы, представленные ниже были добавлены для удобства CRUD операций с БД
    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid PatientDTO patientDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new PatientNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        patientService.create(convertToPatient(patientDTO));

        return ResponseEntity.status(HttpStatus.CREATED).body("Пациент добавлен");
    }

    @GetMapping
    public PatientsResponse read() {
        return new PatientsResponse(patientService.findAll().stream()
                .map(this::convertToPatientDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{uuid}")
    public PatientDTO read(@PathVariable("uuid") String uuid) {
        return convertToPatientDTO(patientService.findByUuid(UUID.fromString(uuid)));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<String> update(@PathVariable("uuid") String uuid, @RequestBody @Valid PatientDTO patientDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new PatientNotCreatedException(ErrorUtil.getErrorMsg(bindingResult));

        patientService.update(UUID.fromString(uuid), convertToPatient(patientDTO));

        return ResponseEntity.status(HttpStatus.OK).body("Данные успешно обновлены");
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> delete(@PathVariable("uuid") String uuid) {
        patientService.delete(UUID.fromString(uuid));

        return ResponseEntity.status(HttpStatus.OK).body("Запись успешно удалена");
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(PatientNotCreatedException e) {
        return new ResponseEntity<>(new ErrorResponse(e.getMessage(), LocalDateTime.now(ZoneId.systemDefault())), HttpStatus.BAD_REQUEST);
    }

    private PatientAppointmentDTO convertToPatientAppointmentDTO(Appointment appointment) {
        return modelMapper.map(appointment, PatientAppointmentDTO.class);
    }

    private Patient convertToPatient(PatientDTO patientDTO) {
        return modelMapper.map(patientDTO, Patient.class);
    }

    private PatientDTO convertToPatientDTO(Patient patient) {
        return modelMapper.map(patient, PatientDTO.class);
    }
}
