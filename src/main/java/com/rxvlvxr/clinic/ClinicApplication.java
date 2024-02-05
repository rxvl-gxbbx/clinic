package com.rxvlvxr.clinic;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@SpringBootApplication
// включаем поддержку SOAP
@EnableWs
public class ClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicApplication.class, args);
    }

    // регистрируем сервлет для SOAP сервиса
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/soap/appointments/*");
    }

    // указываем endpoint
    @Bean(name = "autoGenerate")
    public DefaultWsdl11Definition defaultWsdl11Definition1(XsdSchema appointmentsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AppointmentsGeneratorPort");
        wsdl11Definition.setLocationUri("/soap/appointments");
        wsdl11Definition.setTargetNamespace("clinic.rxvlvxr.com");
        wsdl11Definition.setSchema(appointmentsSchema);
        return wsdl11Definition;
    }

    // указываем endpoint
    @Bean(name = "generate")
    public DefaultWsdl11Definition defaultWsdl11Definition2(XsdSchema appointmentsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AppointmentsGeneratorPort");
        wsdl11Definition.setLocationUri("/soap/appointments");
        wsdl11Definition.setTargetNamespace("clinic.rxvlvxr.com");
        wsdl11Definition.setSchema(appointmentsSchema);
        return wsdl11Definition;
    }

    // указывем схему для SOAP приложения
    @Bean
    public XsdSchema appointmentsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("appointments.xsd"));
    }

    // нужен для конвертации объектов
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
