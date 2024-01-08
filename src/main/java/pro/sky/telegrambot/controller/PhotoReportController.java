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
import pro.sky.telegrambot.model.photoReport.PhotoReport;
import pro.sky.telegrambot.model.ShelterId;
import pro.sky.telegrambot.service.PhotoReportService;

import java.time.LocalDate;
import java.util.Collection;


@RestController
@RequestMapping("/photoReport")
    public class PhotoReportController {
    private PhotoReportService service;

    public PhotoReportController(PhotoReportService service) {
        this.service = service;
    }
    @Operation(summary = "Поиск отчета о животном по id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный отчет о животном",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PhotoReport.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/{photoReport_id}")
    public ResponseEntity<PhotoReport> getPhotoReport(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "id отчета о животном")
            @PathVariable("photoReport_id") Integer photoReportId) {
        PhotoReport animalPhotoReport = service.getPhotoReportById(shelterId, photoReportId);
        return ResponseEntity.ok(animalPhotoReport);
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
    @GetMapping("{shelter_id}/{photoReport_id}/data")
    public ResponseEntity<byte[]> getPhotoReportData(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "id отчета о животном")
            @PathVariable("photoReport_id") Integer photoReportId) {
        PhotoReport photoReport = service.getPhotoReportById(shelterId, photoReportId);
        byte[] data = photoReport.getData();

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
                                    schema = @Schema(implementation = PhotoReport.class)
                            )
                    )
            })
    @DeleteMapping("{shelter_id}/{photoReport_id}")
    public ResponseEntity<PhotoReport> deletePhotoReport(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "id отчета о животном")
            @PathVariable("photoReport_id") Integer photoReportId) {
        PhotoReport photoReport = service.getPhotoReportById(shelterId, photoReportId);
        return ResponseEntity.ok(photoReport);
    }

    @Operation(summary = "Поиск всех отчетов о животных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все отчеты о животных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PhotoReport.class))
                            )
                    )
            })
    @GetMapping("{shelter_id}/all") // все отчеты приюта
    public ResponseEntity<Collection<PhotoReport>> getAllReports(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId
    ) {
        return ResponseEntity.ok(service.getAllPhotoReport(shelterId));
    }

    @Operation(summary = "Поиск всех отчетов о животных на конкретную дату",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все отчеты о животных на конкретную дату",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PhotoReport.class))
                            )
                    )
            })
    @GetMapping(value = "{shelter_id}", params = "date") // отчет за указанную дату
    public  ResponseEntity<Collection<PhotoReport>> getPhotoReportByDate(
            @Parameter(description = "id приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Дата поиска отчетов о животных")
            @RequestParam("date") LocalDate date){
        //@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date){
        return ResponseEntity.ok(service.getAllPhotoReportByDate(shelterId, date));
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
                              @PathVariable("user_id") long id)
    {
        service.warningToUser(id);
    }

}
