package pro.sky.telegrambot.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.service.AnimalService;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.model.entity.Animal;

import java.util.Collection;

@RestController
@RequestMapping("animal")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @Operation(summary = "Создание нового питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Созданный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый питомец",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class)
                    )
            )
    )
    @PostMapping("{shelter_id}")
    public ResponseEntity<Animal> createAnimal(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @RequestBody Animal animal) {
        if (animal == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(animalService.createAnimal(shelterId, animal));
    }

    @Operation(summary = "Поиск питомца по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/{animal_id}")
    public ResponseEntity<Animal> getAnimal(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор питомца")
            @PathVariable("animal_id") Integer animalId) {
        return ResponseEntity.ok(animalService.getAnimal(shelterId, animalId));
    }

    @Operation(summary = "Изменение питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Измененный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Измененный питомец",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Animal.class)
                    )
            )
    )
    @PutMapping("{shelter_id}")
    public ResponseEntity<Animal> updateAnimal(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @RequestBody Animal animal) {
        if (animal == null) {
            return ResponseEntity.noContent().build();
        }
        Animal updatedAnimal = animalService.updateAnimal(shelterId, animal);
        return ResponseEntity.ok(updatedAnimal);
    }

    @Operation(summary = "Удаление питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            })
    @DeleteMapping("{shelter_id}/{animal_id}")
    public ResponseEntity<Animal> deleteAnimal(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор питомца")
            @PathVariable("animal_id") Integer petId) {
        Animal animal = animalService.getAnimal(shelterId, petId);
        animalService.deleteAnimal(shelterId, petId);
        return ResponseEntity.ok(animal);
    }

    @Operation(summary = "Поиск всех питомцев",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все найденные питомцы",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Animal.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/all")
    public ResponseEntity<Collection<Animal>> getAllDogs(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId) {
        return ResponseEntity.ok(animalService.getAllAnimals(shelterId));
    }
}
