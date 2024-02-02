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
import pro.sky.telegrambot.model.report.Report;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.service.ReportService;


import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Поиск отчета о животном по идентификатору",
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
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report dogReport = reportService.getReportById(shelterId, reportId);
        return ResponseEntity.ok(dogReport);
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
    @GetMapping("{shelter_id}/{report_id}/photo")
    public ResponseEntity<byte[]> getReportPhoto(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report report = reportService.getReportById(shelterId, reportId);
        byte[] photo = report.getPhoto();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(photo.length);

        return new ResponseEntity<>(photo, headers, HttpStatus.OK);
    }


    @Operation(summary = "Удаление отчета о животном по идентификатору",
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
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report report = reportService.deleteReportById(shelterId, reportId);
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
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId
    ) {
        return ResponseEntity.ok(reportService.getAllReports(shelterId));
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
    public ResponseEntity<Collection<Report>> getReportsByDate(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Дата поиска отчетов о животных")
            @RequestParam("date") LocalDate date) {
        //@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date){
        return ResponseEntity.ok(reportService.getAllReportsByDate(shelterId, date));
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
    public void warningToUser(@Parameter(description = "Идентификатор пользователя")
                              @PathVariable("user_id") long id) {
        reportService.warningToUser(id);
    }

}
