<h1>Задача</h1>
<h4>Создать систему записи пациентов на приём, состоящую из:</h4>
<ol>
    <li>Базы данных (желательно <b>PostgreSQL</b>)</li>
        <ol>
            <li>С таблицами</li>
            <ol>
                <li><b>врачи</b>: id, uuid, ФИО и т. д. (здесь и далее «и т. д.» обозначает, что можно расширить структуру, если будут свои идеи)</li>
                <li><b>пациенты</b>: id, uuid, ФИО, дата рождения и т. д.</li>
                <li><b>талон (слот времени)</b>: id, id врача, id пациента, время начала приёма и т. д.</li>
            </ol>
            <li><b>SQL-скрипт</b> создания структура этой базы в выбранной СУБД (можно взять автоматически сгенерированный скрипт по созданной структуре).</li>
        </ol>
    <li>Java web приложение, выполняющее основную логику (желательно <b>Spring Boot</b>)</li>
        <ol>
            <li>Осуществлять подключение и операции в БД (желательно средствами <b>Java Persistence API</b>)</li>
            <li><b>SOAP</b> сервис с методом создания расписания, который по переданным правилам создаёт слоты времени. Правила могут состоять из даты/времени начала расписания, продолжительность талона (слота времени), их количество и т. д. Можно применить более сложную структуру правил, если будет желание.</li>
            <li><b>REST</b> сервис работы с методами:</li>
            <ol>
                <li>получение свободных слотов времени к указанному врачу на указанную дату</li>
                <li>занятие слота времени по его id</li>
                <li>получение всех слотов времени, занятых одним пациентом по id/uuid</li>
            </ol>
        </ol>
</ol>

<h4>SQL-script - <a href="https://gist.github.com/rxvl-gxbbx/fc726a76ec4314669f952a75d1a7a215">SCRIPT</a></h4>

<h4>Описание пакетов:</h4>
<ol>
    <li><b><a href="">controllers</a></b> - пакет контроллеров для REST сервиса, данные классы нужны для обработки запросов</li>
    <li><b><a href="">dtos (Data Transfer Objects)</a></b> - пакет классов-обертки для JSON request/response, эти классы нужны для передачи данных, т.е. все поля этих классов конвертируются в JSON формат для удобства передачи запросов, либо для того чтобы скрыть информацию, которая является необязательной для клиента (напримерю, ID из БД)</li>
    <li><b><a href="">endpoints</a></b> - пакет для endpoints, endpoint класс нужен для обработки SOAP запроса</li>
    <li><b><a href="">models</a></b> - пакет классов-моделей, которые описывают сущности таблиц</li>
    <li><b><a href="">repositories</a></b> - пакет классов репозитория, которые взаимодействуют с БД</li>
    <li><b><a href="">services</a></b> - пакет классов сервиса, отвечают за бизнес-логику приложения</li>
    <li><b><a href="">utils</a></b> - пакет классов утилит, отвечают за обработку исключений, валидацию, конвертацию одних объектов в другие и т.п.</li>
</ol>
<h3>Примечание:</h3>
<p>Множество методов в контроллерах и сервисе было создано для удобства взаимодействия с БД, в основном это CRUD операции (не относятся к требованиям ТЗ)</p>
<p>Классы DTO были созданы для удобства передачи данных и для того чтобы скрыть необязательную для клиента информацию, но я также (в контроллерах, где методы CRUD операций) оставил почти полные классы-обертки моделей (не включащающие в себя ID из БД) для, к примеру, администрирования, т.е. ответы (response) здесь более детальные, например ответ на GET-запрос по URL: http://localhost:8080/rest/appointments</p>
<pre>
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
</pre>
<h2>Более подробное описание логики работы сервисов находится в <a href="#TODO">services</a> </h2>
<h3>Описание методов из тестового задания:</h3>

<ol>
    <li><b>SOAP сервис с методом создания расписания, который по переданным правилам создаёт слоты времени. Правила могут состоять из даты/времени начала расписания, продолжительность талона (слота времени), их количество и т. д. Можно применить более сложную структуру правил, если будет желание.</b></li>
    <ol>
        <li><b>Авто-генерация расписания для всех врачей в базе данных</b></li>
        <pre>
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "autoGenerateRequest")
@ResponsePayload
public AutoGenerateResponse autoGenerate(@RequestPayload AutoGenerateRequest request)
        </pre>
        <p>Метод, который принимает в виде запроса класс типа AutoGenerateRequest, который является оберткой для класса AutoCreation, где полями выступают <b>dateFrom</b> - дата начала генерации расписания, <b>durationInMin</b> - продолжительность приема и <b>daysAmount</b> - количество дней, на которые нужно сгенерировать расписание</p>
        <p>Логика (выполняется с помощью внедрения зависимости AppointmentService):</p>
        <ol>
            <li>При успешной валидации и в случае если хотя бы один объект Doctor будет найден в таблице, то произойдет перебор по списку этих объектов</li>
            <li>В зависимости от рабочей смены врача будут устанавливаться временные ограничения, до/после которых нельзя будет устанавливать слот времени</li>
            <li>Я решил, что данный запрос не будет использоваться при тонкой настройке, а больше для автоматической генерации расписания, поэтому для удобства удаляются все слоты времени, начинающиеся с той даты, которую указали в запросе</li>
            <li>В зависимости от продолжительности приема устанавливаются слоты времени и происходит переход на следующий день, если в запросе передали значение, отличное от 1</li>
        </ol>
        <p>Пример запроса:</p>
        <pre>
            &lt;soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:clin="http://clinic.rxvlvxr.com"&gt
               &lt;soapenv:Header/&gt
               &lt;soapenv:Body&gt
                  &lt;clin:autoGenerateRequest&gt
                     &lt;clin:autoCreation&gt
                        &lt;clin:dateFrom&gt;2024-02-04&lt;/clin:dateFrom&gt
                        &lt;clin:durationInMin&gt;20&lt;/clin:durationInMin&gt
                        &lt;clin:daysAmount&gt;5&lt;/clin:daysAmount&gt
                     &lt;/clin:autoCreation&gt
                  &lt;/clin:autoGenerateRequest&gt
               &lt;/soapenv:Body&gt
            &lt;/soapenv:Envelope&gt
        </pre>
        <p>Пример ответа:</p>
        <pre>
            &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"&gt
               &lt;SOAP-ENV:Header/&gt
               &lt;SOAP-ENV:Body&gt
                  &lt;ns2:autoGenerateResponse xmlns:ns2="http://clinic.rxvlvxr.com"&gt
                     &lt;ns2:serviceStatus&gt
                        &lt;ns2:status&gt;УСПЕШНО&lt;/ns2:status&gt
                        &lt;ns2:message&gt;Расписание успешно создано&lt;/ns2:message&gt
                     &lt;/ns2:serviceStatus&gt
                  &lt;/ns2:autoGenerateResponse&gt
               &lt;/SOAP-ENV:Body&gt
            &lt;/SOAP-ENV:Envelope&gt
        </pre>
        <p>Пример ошибки:</p>
        <pre>
            &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"&gt
               &lt;SOAP-ENV:Header/&gt
               &lt;SOAP-ENV:Body&gt
                  &lt;SOAP-ENV:Fault&gt
                     &lt;faultcode&gt;SOAP-ENV:Server&lt;/faultcode&gt
                     &lt;faultstring xml:lang="en"&gt;daysAmount - Укажите корректное количество дней! Минимальное - 1;durationInMin - Продолжительность приема должна быть в промежутке с 20 до 45 минут;dateFrom - Укажите корректную дату: yyyy-MM-dd;&lt;/faultstring&gt
                  &lt;/SOAP-ENV:Fault&gt
               &lt;/SOAP-ENV:Body&gt
            &lt;/SOAP-ENV:Envelope&gt;
        </pre>
        <li><b>Тонкая настройка генерации расписания для одного врача</b></li>
        <pre>
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "generateByRulesRequest")
@ResponsePayload
public GenerateByRulesResponse generateByRules(@RequestPayload GenerateByRulesRequest request)
        </pre>
        <p>Метод, принимающий в качестве запроса класс GenerateByRulesRequest, который является классом обертке для класса AppointmentRules, где правилами для расписания выступают поля: <b>doctorId</b> - ID врача, для которого будет генерироваться расписание, <b>dateTimeFrom</b> - точное время, от которого пойдет отсчет слотов времени, <b>durationInMin</b> - продолжительность приема в минутах, <b>appointmentsAmount</b> - количество талонов, которые нужно сгенерировать, <b>clearBefore</b> - булево значение, которое указывает нужно ли очищать тот день, на котором будет происходить генерация расписания или нет</p>
        <p>Логика (выполняется с помощью внедрения зависимости AppointmentService</p>
        <ol>
            <li>При успешной валидации и в случае если объект Doctor будет найден в таблице, то будет проверено значение clearBefore, соответственно, при значении true или 1 будет очищены все слоты найденного врача перед генерацией расписания</li>    
            <li>В зависимости от количества талонов, продолжительности, смены врача будет сгенерировано расписание</li>    
        </ol>
        <p>Пример запроса:</p>
        <pre>
            &lt;soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:clin="http://clinic.rxvlvxr.com"&gt;
               &lt;soapenv:Header/&gt;
               &lt;soapenv:Body&gt;
                  &lt;clin:generateByRulesRequest&gt;
                     &lt;clin:appointmentsRules&gt;
                        &lt;clin:doctorId&gt;28&lt;/clin:doctorId&gt;
                        &lt;clin:dateTimeFrom&gt;2024-02-01T07:12:12&lt;/clin:dateTimeFrom&gt;
                        &lt;clin:durationInMin&gt;20&lt;/clin:durationInMin&gt;
                        &lt;clin:appointmentsAmount&gt;27&lt;/clin:appointmentsAmount&gt;
                        &lt;clin:clearBefore&gt;true&lt;/clin:clearBefore&gt;
                     &lt;/clin:appointmentsRules&gt;
                  &lt;/clin:generateByRulesRequest&gt;
               &lt;/soapenv:Body&gt;
            &lt;/soapenv:Envelope&gt;
        </pre>
        <p>Пример ответа:</p>
        <pre>
            &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"&gt;
               &lt;SOAP-ENV:Header/&gt;
               &lt;SOAP-ENV:Body&gt;
                  &lt;ns2:generateByRulesResponse xmlns:ns2="http://clinic.rxvlvxr.com"&gt;
                     &lt;ns2:serviceStatus&gt;
                        &lt;ns2:status&gt;УСПЕШНО&lt;/ns2:status&gt;
                        &lt;ns2:message&gt;Расписание успешно создано&lt;/ns2:message&gt;
                     &lt;/ns2:serviceStatus&gt;
                  &lt;/ns2:generateByRulesResponse&gt;
               &lt;/SOAP-ENV:Body&gt;
            &lt;/SOAP-ENV:Envelope&gt;
        </pre>
        <p>Пример ошибки:</p>
        <pre>
            &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"&gt;
               &lt;SOAP-ENV:Header/&gt;
               &lt;SOAP-ENV:Body&gt;
                  &lt;SOAP-ENV:Fault&gt;
                     &lt;faultcode&gt;SOAP-ENV:Server&lt;/faultcode&gt;
                     &lt;faultstring xml:lang="en"&gt;dateTimeFrom - Невозможно сгенерировать расписание! Врач работает c 10 до 19;&lt;/faultstring&gt;
                  &lt;/SOAP-ENV:Fault&gt;
               &lt;/SOAP-ENV:Body&gt;
            &lt;/SOAP-ENV:Envelope&gt;
        </pre>
    </ol>
    <li>REST сервис работы с методами:</li>
        <ol>
        <li><b>Получение свободных слотов времени к указанному врачу на указанную дату</b></li>
        <pre>
@PostMapping("/{uuid}")
public DoctorAppointmentsResponse getAllAppointments(@PathVariable("uuid") String uuid, 
                                                     @RequestBody @Valid DoctorDateRequest request, 
                                                     BindingResult bindingResult)
        </pre>
        <p>Метод POST-запроса, принимающий из URL строку <b>UUID</b> врача, в качестве JSON запроса выступает класс DTO <b>DoctorDateRequest</b>, который имеет единственное поле <b>date</b></p>
        <p><b>Примечание:</b> я решил выбрать POST, а не GET, потому что передаю в теле запроса данные</p>
        <p>Пример запроса к сервису:</p>
        <p>URL: http://localhost:8080/rest/doctors/{uuid}</p>
        <pre>
{
    "date": "04/02/2024"
}
        </pre>
        <p>Пример ответа на запрос:</p>
        <pre>
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
        </pre>
        <p>Пример ошибки (допустим, некорректная <b>date</b>):</p>
        <pre>
{
    "message": "Введите корректную дату",
    "time": "2024-02-05T11:42:57.7985638"
}
        </pre>
        <li><b>Занятие слота времени по его id</b></li>
        <pre>
@PatchMapping("/{id}/reserve")
public ResponseEntity&lt;String&gt reserve(@PathVariable("id") long id,
                                      @RequestBody @Valid PatientReserveRequest request,
                                      BindingResult bindingResult)
        </pre> 
        <p>Метод PATCH-запроса, принимающий из URL <b>ID</b> талона, по которому будет осуществляться запись, в качестве JSON запроса выступает класс DTO <b>PatientReserveRequest</b>, у которого есть единственное поле <b>patientUuid</b>, интерфейс BindingResult нужен для перехвата ошибок валидации</p>
        <p>Пример запроса к сервису:</p>
        <p>URL: http://localhost:8080/rest/appointments/{id}/reserve</p>
        <pre>
{
    "patient_uuid": "d8ffdcc2-68d5-4e5b-89a2-ae942f5d2ae1"
}
        </pre>
        <p>Пример успешного ответа на запрос</p>
        <pre>Назначена запись</pre>
        <p>Пример ошибки (допустим, не ввели <b>patientUuid</b>):</p>
        <pre>
{
    "message": "patientUuid - Поле не может быть пустым;",
    "time": "2024-02-05T11:42:09.5483935"
}
        </pre>
        <li><b>Получение всех слотов времени, занятых одним пациентом по id/uuid</b></li>
        <pre>
@GetMapping("/{uuid}/appointments")
public PatientAppointmentsResponse getAllAppointments(@PathVariable("uuid") String uuid)
        </pre>
        <p>Метод GET-запроса, принимающий из URL <b>UUID</b> пациента и возвращающий DTO класса <b>PatientAppointmentsResponse</b> в виде JSON ответа, DTO выступает здесь в роли сокрытия тех данных, которые необязательно видеть пользователю (ID из базы данных, UUID и смена врача, данные самого пациента и т.п.)</p>
        <p>Пример ответа на GET-запрос:</p>
        <p>URL: http://localhost:8080/rest/patients/{uuid}/appointments</p>
        <pre>
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
        </pre>
        <p>Пример ошибки:</p>
        <pre>
{
    "message": "Пациент не найден",
    "time": "2024-02-05T11:51:50.9896191"
}
        </pre>
        </ol>
</ol>
