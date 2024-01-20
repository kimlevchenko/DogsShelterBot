package pro.sky.telegrambot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.model.entity.MessageToVolunteer;
import pro.sky.telegrambot.service.MessageToVolunteerService;

import java.util.List;


@RestController
@RequestMapping("/message_to_volunteer")
public class MessageToVolunteerController {

    private final MessageToVolunteerService messageToVolunteerService;

    public MessageToVolunteerController(MessageToVolunteerService messageToVolunteerService) {
        this.messageToVolunteerService = messageToVolunteerService;
    }

    @Operation(
            summary = "Поиск всех объектов MessageToVolunteer с полем answer = null",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все обекты MessageToVolunteer с полем answer = null",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = MessageToVolunteer.class))
                            )
                    )
            }
    )
    @GetMapping("/all_without_answer")
    public List<MessageToVolunteer> findAllWithoutAnswer() {
        return messageToVolunteerService.findAllWithoutAnswer();
    }

   /* @Operation(
            summary = "Возвращает ответ от волонтера и вставляет его в поле answer",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            }
    )

    @PutMapping(value = "{id}", params = {"answer", "replyToMessage"})
    public void updateAnswer(@Parameter(description = "Идентификатор объекта MessageToVolunteer")
                             @PathVariable("id") int id,
                             @Parameter(description = "Ответ волонтера")
                             @RequestParam String answer,
                             @RequestParam boolean replyToMessage)
    {
        messageToVolunteerService.updateAnswer(id, answer, replyToMessage);
    }*/


}
