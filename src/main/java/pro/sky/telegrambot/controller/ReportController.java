package pro.sky.telegrambot.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.telegrambot.model.photoReport.DogReport;
import pro.sky.telegrambot.model.photoReport.Report;
import pro.sky.telegrambot.model.ShelterId;
import pro.sky.telegrambot.service.ReportService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;


@RestController
@RequestMapping("/report")
    public class ReportController {
    private ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @Operation(summary = "Поиск отчета о животном по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный отчет о животном",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/{report_id}")
    public ResponseEntity<Report> getReport(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "id отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report animalReport = service.getReportById(shelterId, reportId);
        return ResponseEntity.ok(animalReport);
    }

    @Operation(summary = "Получение фотографии животного из отчета",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Фотография животного",
                            content = @Content(
                                    mediaType = MediaType.IMAGE_JPEG_VALUE
                            )
                    )
            })
    @GetMapping("{shelter_id}/{report_id}/data")
    public ResponseEntity<byte[]> getReportData(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "id отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report report = service.getReportById(shelterId, reportId);
        byte[] data = report.getData();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(data.length);

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }


    @Operation(summary = "Удаление отчета о животном по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный отчет о животном",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            })
    @DeleteMapping("{shelter_id}/{report_id}")
    public ResponseEntity<Report> deleteReport(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "id отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report report = service.getReportById(shelterId, reportId);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Поиск всех отчетов о животных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все отчеты о животных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            })
    @GetMapping("{shelter_id}/all") // все отчеты приюта
    public ResponseEntity<Collection<Report>> getAllReports(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId
    ) {
        return ResponseEntity.ok(service.getAllReport(shelterId));
    }

    @Operation(summary = "Поиск всех отчетов о животных на конкретную дату",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все отчеты о животных на конкретную дату",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            })
    @GetMapping(value = "{shelter_id}", params = "date") // отчет за указанную дату
    public ResponseEntity<Collection<Report>> getReportByDate(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Дата поиска отчетов о животных")
            @RequestParam("date") LocalDate date) {
        //@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date){
        return ResponseEntity.ok(service.getAllReportByDate(shelterId, date));
    }

    @Operation(
            summary = "Отправляет предупреждение усыновителю при неполном отчете",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отправка сообщения состоялась успешно"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если пользователя с таким идентификатором нет в БД"
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Сервис отправки сообщений недоступен"
                    )
            }
    )
    @PostMapping("/warning_to_user/{user_id}")
    public void warningToUser(@Parameter(description = "id пользователя")
                              @PathVariable("user_id") long id) {
        service.warningToUser(id);
    }

    @Operation(
            summary = "Upload file",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "успешно"),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request.")
            }
    )

    @PostMapping("/{upload}")
    public void upload(@Parameter(description="id животного")
                           @RequestParam("animal_id") Long animalId,
                       @RequestParam("file")MultipartFile file,
                       @RequestParam("text") String text,
                       @RequestParam("dogReport") DogReport dogReport)
    {
        try {
            service.upload(animalId,file,text,dogReport);
        } catch (IOException e) { //если при чтении или записи файла произошла ошибка ввода-вывода брасает исключение RuntimeException
            throw new RuntimeException(e);
        }
    }
}



