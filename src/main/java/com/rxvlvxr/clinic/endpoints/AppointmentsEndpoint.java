package com.rxvlvxr.clinic.endpoints;

import com.rxvlvxr.clinic.*;
import com.rxvlvxr.clinic.services.AppointmentService;
import com.rxvlvxr.clinic.utils.AppointmentNotCreatedException;
import com.rxvlvxr.clinic.utils.AppointmentRulesValidator;
import com.rxvlvxr.clinic.utils.AutoCreationValidator;
import com.rxvlvxr.clinic.utils.ErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.DataBinder;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

// SOAP сервис
@Endpoint
public class AppointmentsEndpoint {
    private static final String NAMESPACE_URI = "http://clinic.rxvlvxr.com";

    private final AppointmentService appointmentService;
    private final AppointmentRulesValidator appointmentRulesValidator;
    private final AutoCreationValidator autoCreationValidator;

    @Autowired
    public AppointmentsEndpoint(AppointmentService appointmentService, AppointmentRulesValidator appointmentRulesValidator, AutoCreationValidator autoCreationValidator) {
        this.appointmentService = appointmentService;
        this.appointmentRulesValidator = appointmentRulesValidator;
        this.autoCreationValidator = autoCreationValidator;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "autoGenerateRequest")
    @ResponsePayload
    public AutoGenerateResponse autoGenerate(@RequestPayload AutoGenerateRequest request) {
        final DataBinder dataBinder = new DataBinder(request.getAutoCreation());
        dataBinder.addValidators(autoCreationValidator);
        // валидируем входные данные
        dataBinder.validate();

        if (dataBinder.getBindingResult().hasErrors())
            throw new AppointmentNotCreatedException(ErrorUtil.getErrorMsg(dataBinder.getBindingResult()));

        AutoGenerateResponse response = new AutoGenerateResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        // получаем параметры для создания талонов из request
        AutoCreation auto = request.getAutoCreation();
        // автоматическая генерация талонов по переданным параметрам
        appointmentService.autoGenerate(auto.getDateFrom(), auto.getDurationInMin(), auto.getDaysAmount());
        // устанавливаем статус
        serviceStatus.setStatus("УСПЕШНО");
        serviceStatus.setMessage("Расписание успешно создано");
        // передаем статус в response
        response.setServiceStatus(serviceStatus);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "generateByRulesRequest")
    @ResponsePayload
    public GenerateByRulesResponse generateByRules(@RequestPayload GenerateByRulesRequest request) {
        final DataBinder dataBinder = new DataBinder(request.getAppointmentsRules());
        dataBinder.addValidators(appointmentRulesValidator);
        // валидируем входные данные
        dataBinder.validate();

        if (dataBinder.getBindingResult().hasErrors())
            throw new AppointmentNotCreatedException(ErrorUtil.getErrorMsg(dataBinder.getBindingResult()));

        GenerateByRulesResponse response = new GenerateByRulesResponse();
        ServiceStatus serviceStatus = new ServiceStatus();
        // получаем правила для создания талонов из request
        AppointmentRules rules = request.getAppointmentsRules();
        // более тонкая настройка генерации расписания врачей
        appointmentService.generateByRules(rules.getDoctorId(), rules.getDateTimeFrom(), rules.getDurationInMin(), rules.getAppointmentsAmount(), rules.isClearBefore());
        // устанавливаем статус
        serviceStatus.setStatus("УСПЕШНО");
        serviceStatus.setMessage("Расписание успешно создано");
        // передаем статус в response
        response.setServiceStatus(serviceStatus);

        return response;
    }


}
