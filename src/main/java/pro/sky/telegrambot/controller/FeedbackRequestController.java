package pro.sky.telegrambot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.model.entity.FeedbackRequest;
import pro.sky.telegrambot.service.FeedbackRequestService;


import java.util.List;

@RestController
@RequestMapping("feedback_request")
@Tag(name = "FeedbackRequest")
public class FeedbackRequestController {

    private final FeedbackRequestService feedbackRequestService;

    public FeedbackRequestController(FeedbackRequestService feedbackRequestService) {
        this.feedbackRequestService = feedbackRequestService;
    }

    @Operation(
            summary = "Поиск всех запросов, ожидающих вызова",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Лист ожидания FeedbackRequest",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = FeedbackRequest.class))
                            )
                    )
            }
    )
    @GetMapping("waiting_list")
    public List<FeedbackRequest> getWaitingList() {
        return feedbackRequestService.getWaitingList();
    }

    @Operation(
            summary = "Поиск запроса по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный запрос",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FeedbackRequest.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если запроса нет в БД"
                    )
            }
    )
    @GetMapping("{id}")
    public ResponseEntity<FeedbackRequest> getFeedbackRequest(
            @Parameter(description = "Идентификатор объекта") @PathVariable("id") int id) {
        return ResponseEntity.ok(feedbackRequestService.getFeedbackRequest(id));
    }

    @Operation(
            summary = "Вводит текущее время и дату в поле executionTime после обработки запроса",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Если запроса нет в БД"
                    )
            }
    )
    @PutMapping("{id}")
    public void updateExecutionTime(@Parameter(description = "Идентификатор объекта") @PathVariable("id") int id) {
        feedbackRequestService.updateExecutionTime(id);
    }

}
