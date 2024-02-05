# Задача

#### Создать систему записи пациентов на приём, состоящую из:

1) Базы данных (желательно **PostgreSQL**)
    1) С таблицами
        1) **врачи**: id, uuid, ФИО и т. д. (здесь и далее «и т. д.» обозначает, что можно расширить структуру, если
           будут свои
           идеи)
        2) **пациенты**: id, uuid, ФИО, дата рождения и т. д.
        3) **талон (слот времени)**: id, id врача, id пациента, время начала приёма и т. д.
    2) **SQL-скрипт** создания структура этой базы в выбранной СУБД (можно взять автоматически сгенерированный скрипт по
       созданной структуре).
2) Java web приложение, выполняющее основную логику (желательно **Spring Boot**)
    1) Осуществлять подключение и операции в БД (желательно средствами **Java Persistence API**)
    2) **SOAP** сервис с методом создания расписания, который по переданным правилам создаёт слоты времени. Правила
       могут
       состоять из даты/времени начала расписания, продолжительность талона (слота времени), их количество и т. д. Можно
       применить более сложную структуру правил, если будет желание.
    3) **REST** сервис работы с методами:
        1) получение свободных слотов времени к указанному врачу на указанную дату
        2) занятие слота времени по его id
        3) получение всех слотов времени, занятых одним пациентом по id/uuid

### [SQL-скрипт](https://gist.github.com/rxvl-gxbbx/fc726a76ec4314669f952a75d1a7a215)

### Описание пакетов:

1) [controllers](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/controllers) - пакет
   контроллеров для REST сервиса, данные классы нужны для обработки запросов
2) [dtos (Data Transfer Objects)](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/dtos) -
   пакет классов-обертки для JSON request/response, эти классы нужны для передачи данных, т.е. все поля этих классов
   конвертируются в JSON формат для удобства передачи запросов, либо для того чтобы скрыть информацию, которая является
   необязательной для клиента (напримерю, ID из БД)
3) [endpoints](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/endpoints) - пакет для
   endpoints, endpoint класс нужен для обработки SOAP запроса
4) [models](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/models) - пакет
   классов-моделей, которые описывают сущности таблиц
5) [repositories](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/repositories) -
   пакет классов репозитория, которые взаимодействуют с БД
6) [services](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/services) - пакет
   классов сервиса, отвечают за бизнес-логику приложения
7) [utils](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/utils) - пакет классов
   утилит, отвечают за обработку исключений, валидацию, конвертацию одних
   объектов в другие и т.п.

### Примечание:

* Множество методов в контроллерах и сервисе было создано для удобства взаимодействия с БД, в основном это CRUD
  операции (
  не относятся к требованиям ТЗ)
* Классы DTO были созданы для удобства передачи данных и для того чтобы скрыть необязательную для клиента информацию, но
  я
  также (в контроллерах, где методы CRUD операций) оставил почти полные классы-обертки моделей (не включащающие в себя
  ID
  из БД) для, к примеру, администрирования, т.е. ответы (response) здесь более детальные, например ответ на GET-запрос
  по
  URL: http://localhost:8080/rest/appointments

```json
{
  "appointments": [
    {
      "doctor": {
        "uuid": "08a68b0b-fa43-457f-8449-698b0955a514",
        "name": "Габараев Рауль Зурабович",
        "shift": "DAY",
        "speciality": "SURGEON",
        "cabinet": 100
      },
      "patient": {
        "uuid": "22ffbf06-eb37-4bd2-a90d-7f57a4787be6",
        "name": "Носов Артем Егорович",
        "quarantine": false,
        "address": "Москва",
        "date_of_birth": "12/12/2000",
        "phone_number": "8925913929"
      },
      "time": "2024-02-01T10:00:00"
    },
    {
      "doctor": {
        "uuid": "08a68b0b-fa43-457f-8449-698b0955a514",
        "name": "Габараев Рауль Зурабович",
        "shift": "DAY",
        "speciality": "SURGEON",
        "cabinet": 100
      },
      "patient": null,
      "time": "2024-02-01T10:40:00"
    }
  ]
}
```

## Более подробное описание логики работы сервисов находится в [services](https://github.com/rxvl-gxbbx/clinic/tree/master/src/main/java/com/rxvlvxr/clinic/services)

## Описание методов из тестового задания:

**SOAP сервис с методом создания расписания, который по переданным правилам создаёт слоты времени. Правила могут
состоять из даты/времени начала расписания, продолжительность талона (слота времени), их количество и т. д. Можно
применить более сложную структуру правил, если будет желание.**

### Авто-генерация расписания для всех врачей в базе данных

```java

@PayloadRoot(namespace = NAMESPACE_URI, localPart = "autoGenerateRequest")
@ResponsePayload
public AutoGenerateResponse autoGenerate(@RequestPayload AutoGenerateRequest request) {
    // code
}
```

Метод, который принимает в виде запроса класс типа **AutoGenerateRequest**, который является оберткой для класса
**AutoCreation**, где полями выступают **dateFrom** - дата начала генерации расписания, **durationInMin** -
продолжительность приема и **daysAmount** - количество дней, на которые нужно сгенерировать расписание

#### Логика (выполняется с помощью внедрения зависимости **AppointmentService**):

1) При успешной валидации и в случае если хотя бы один объект Doctor будет найден в таблице, то произойдет перебор по
   списку этих объектов
2) В зависимости от рабочей смены врача будут устанавливаться временные ограничения, до/после которых нельзя будет
   устанавливать слот времени
3) Я решил, что данный запрос не будет использоваться при тонкой настройке, а больше для автоматической генерации
   расписания, поэтому для удобства удаляются все слоты времени, начинающиеся с той даты, которую указали в запросе
4) В зависимости от продолжительности приема устанавливаются слоты времени и происходит переход на следующий день, если
   в
   запросе передали значение, отличное от 1

#### Пример запроса:

```xml

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:clin="http://clinic.rxvlvxr.com">
    <soapenv:Header/>
    <soapenv:Body>
        <clin:autoGenerateRequest>
            <clin:autoCreation>
                <clin:dateFrom>2024-02-04</clin:dateFrom>
                <clin:durationInMin>20</clin:durationInMin>
                <clin:daysAmount>5</clin:daysAmount>
            </clin:autoCreation>
        </clin:autoGenerateRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

#### Пример ответа:

```xml

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:autoGenerateResponse xmlns:ns2="http://clinic.rxvlvxr.com">
            <ns2:serviceStatus>
                <ns2:status>УСПЕШНО</ns2:status>
                <ns2:message>Расписание успешно создано</ns2:message>
            </ns2:serviceStatus>
        </ns2:autoGenerateResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

#### Пример ошибки:

```xml

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <SOAP-ENV:Fault>
            <faultcode>SOAP-ENV:Server</faultcode>
            <faultstring xml:lang="en">daysAmount - Укажите корректное количество дней! Минимальное - 1;durationInMin - Продолжительность приема должна быть в промежутке с 20 до 45 минут;dateFrom - Укажите корректную дату: yyyy-MM-dd;</faultstring>
        </SOAP-ENV:Fault>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

## Тонкая настройка генерации расписания для одного врача

```java

@PayloadRoot(namespace = NAMESPACE_URI, localPart = "generateByRulesRequest")
@ResponsePayload
public GenerateByRulesResponse generateByRules(@RequestPayload GenerateByRulesRequest request) {
    /// code
}
```

Метод, принимающий в качестве запроса класс **GenerateByRulesRequest**, который является классом обертке для класса **AppointmentRules**, где правилами для расписания выступают поля: **doctorId** - ID врача, для которого будет
генерироваться расписание, **dateTimeFrom** - точное время, от которого пойдет отсчет слотов времени, **durationInMin** -
продолжительность приема в минутах, **appointmentsAmount** - количество талонов, которые нужно сгенерировать, **clearBefore** - булево значение, которое указывает нужно ли очищать тот день, на котором будет происходить генерация
расписания или нет

#### Логика (выполняется с помощью внедрения зависимости **AppointmentService**)

* При успешной валидации и в случае если объект Doctor будет найден в таблице, то будет проверено значение **clearBefore**,
  соответственно, при значении true или 1 будет очищены все слоты найденного врача перед генерацией расписания
* В зависимости от количества талонов, продолжительности, смены врача будет сгенерировано расписание

#### Пример запроса:

```xml

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:clin="http://clinic.rxvlvxr.com">
    <soapenv:Header/>
    <soapenv:Body>
        <clin:generateByRulesRequest>
            <clin:appointmentsRules>
                <clin:doctorId>28</clin:doctorId>
                <clin:dateTimeFrom>2024-02-01T07:12:12</clin:dateTimeFrom>
                <clin:durationInMin>20</clin:durationInMin>
                <clin:appointmentsAmount>27</clin:appointmentsAmount>
                <clin:clearBefore>true</clin:clearBefore>
            </clin:appointmentsRules>
        </clin:generateByRulesRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

#### Пример ответа:

```xml

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:generateByRulesResponse xmlns:ns2="http://clinic.rxvlvxr.com">
            <ns2:serviceStatus>
                <ns2:status>УСПЕШНО</ns2:status>
                <ns2:message>Расписание успешно создано</ns2:message>
            </ns2:serviceStatus>
        </ns2:generateByRulesResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

#### Пример ошибки:

```xml

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <SOAP-ENV:Fault>
            <faultcode>SOAP-ENV:Server</faultcode>
            <faultstring xml:lang="en">dateTimeFrom - Невозможно сгенерировать расписание! Врач работает c 10 до 19;</faultstring>
        </SOAP-ENV:Fault>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>

```

## REST сервис работы с методами:

### Получение свободных слотов времени к указанному врачу на указанную дату

```java

@PostMapping("/{uuid}")
public DoctorAppointmentsResponse getAllAppointments(@PathVariable("uuid") String uuid,
                                                     @RequestBody @Valid DoctorDateRequest request,
                                                     BindingResult bindingResult) {
    // code
}
```

Метод POST-запроса, принимающий из URL строку **UUID** врача, в качестве JSON запроса выступает класс DTO **DoctorDateRequest**, который имеет единственное поле **date**

**Примечание**: я решил выбрать POST, а не GET, потому что передаю в теле запроса данные

Пример запроса к сервису:

URL: http://localhost:8080/rest/doctors/{uuid}

```json
{
  "date": "04/02/2024"
}
```

#### Пример ответа на запрос:

```json
{
  "doctor_vacant_appointments": [
    {
      "time": "2024-02-01T18:40:00"
    },
    {
      "time": "2024-02-01T18:20:00"
    },
    {
      "time": "2024-02-01T18:00:00"
    },
    {
      "time": "2024-02-01T10:00:00"
    }
  ]
}
```

#### Пример ошибки (допустим, некорректная **date**):

```json
{
  "message": "Введите корректную дату",
  "time": "2024-02-05T11:42:57.7985638"
}
```

### Занятие слота времени по его id

```java

@PatchMapping("/{id}/reserve")
public ResponseEntity<String> reserve(@PathVariable("id") long id,
                                      @RequestBody @Valid PatientReserveRequest request,
                                      BindingResult bindingResult) {
    // code    
}
```

Метод **PATCH**-запроса, принимающий из URL **ID** талона, по которому будет осуществляться запись, в качестве JSON
запроса выступает класс DTO **PatientReserveRequest**, у которого есть единственное поле **patientUuid**, интерфейс **BindingResult** нужен для перехвата ошибок валидации

#### Пример запроса к сервису:

URL: http://localhost:8080/rest/appointments/{id}/reserve

```json
{
  "patient_uuid": "d8ffdcc2-68d5-4e5b-89a2-ae942f5d2ae1"
}
```

#### Пример успешного ответа на запрос

```
Назначена запись
```

#### Пример ошибки (допустим, не ввели **patientUuid**):

```json
{
  "message": "patientUuid - Поле не может быть пустым;",
  "time": "2024-02-05T11:42:09.5483935"
}
```

### Получение всех слотов времени, занятых одним пациентом по id/uuid

```java

@GetMapping("/{uuid}/appointments")
public PatientAppointmentsResponse getAllAppointments(@PathVariable("uuid") String uuid) {
    // code
}

```

Метод GET-запроса, принимающий из URL **UUID** пациента и возвращающий DTO класса **PatientAppointmentsResponse**
в виде JSON ответа, DTO выступает здесь в роли сокрытия тех данных, которые необязательно видеть пользователю (ID из
базы данных, UUID и смена врача, данные самого пациента и т.п.)

#### Пример ответа на GET-запрос:

URL: http://localhost:8080/rest/patients/{uuid}/appointments

```json
{
  "patient_appointments": [
    {
      "doctor": {
        "name": "Габараев Рауль Зурабович",
        "speciality": "SURGEON",
        "cabinet": 100
      },
      "time": "2024-02-01T18:20:00"
    }
  ]
}
```

#### Пример ошибки:

```json
{
  "message": "Пациент не найден",
  "time": "2024-02-05T11:51:50.9896191"
}

```