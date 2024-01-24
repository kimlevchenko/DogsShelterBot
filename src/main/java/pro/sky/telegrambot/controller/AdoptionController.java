package pro.sky.telegrambot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.model.adoption.Adoption;
import pro.sky.telegrambot.model.adoption.AdoptionDTO;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.service.AdoptionService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }


    @Operation(summary = "Создание нового усыновления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Созданное усыновление",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Adoption.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новое усыновление",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdoptionDTO.class)
                    )
            )
    )
    @PostMapping("{shelter_id}")
    public ResponseEntity<Adoption> createAdoption(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable(name = "shelter_id") ShelterId shelterId,
            @RequestBody AdoptionDTO adoptionDTO
    ) {
        return ResponseEntity.ok(
                adoptionService.createAdoption(shelterId,
                        adoptionDTO.getUserId(), adoptionDTO.getAnimalId(), adoptionDTO.getTrialDate()));
    }

    @Operation(summary = "Поиск усыновления по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденное усыновление",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Adoption.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/{adoption_id}")
    public Adoption getAdoption(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор усыновления")
            @PathVariable("adoption_id") Integer adoptionId) {
        return adoptionService.getAdoption(shelterId, adoptionId);
    }

    @Operation(summary = "Изменение испытательного срока",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Усыновление с измененным испытательным сроком",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Adoption.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Не удалось изменить испытательный срок, " +
                                    "т.к. сервис отправки сообщений пользователю недоступен"
                    )
            })
    @PutMapping(value = "{shelter_id}/{adoption_id}", params = "trial_date")
    public Adoption setTrialDate(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор усыновления")
            @PathVariable("adoption_id") Integer adoptionId,
            @Parameter(description = "Новый испытательный срок")
            @RequestParam("trial_date") LocalDate trialDate
    ) {
        return adoptionService.setTrialDate(shelterId, adoptionId, trialDate);
    }

    @Operation(summary = "Удаление усыновления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленное усыновление",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Adoption.class)
                            )
                    )
            })
    @DeleteMapping("{shelter_id}/{adoption_id}")
    public Adoption deleteAdoption(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор усыновления")
            @PathVariable("adoption_id") Integer adoptionId) {
        return adoptionService.deleteAdoption(shelterId, adoptionId);
    }

    @Operation(summary = "Поиск всех усыновлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все усыновления",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Adoption.class))
                            )
                    )
            })
    @GetMapping("{shelter_id}/all")
    public Collection<Adoption> getAllAdoptions(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId) {
        return adoptionService.getAllAdoptions(shelterId);
    }

    @Operation(summary = "Поиск всех действующих усыновлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все действующие усыновления",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Adoption.class))
                            )
                    )
            })
    @GetMapping("{shelter_id}/active")
    public Collection<Adoption> getAllActiveAdoptions(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId) {
        return adoptionService.getAllActiveAdoptions(shelterId);
    }
}
