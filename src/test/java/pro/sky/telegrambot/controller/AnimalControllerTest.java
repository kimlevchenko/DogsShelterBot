package pro.sky.telegrambot.controller;

import static org.junit.jupiter.api.Assertions.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.service.AnimalService;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестовый класс для PetController
 */
@WebMvcTest(AnimalController.class)
public class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnimalService animalService;

    private Animal animal;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(1);
        animal.setAnimalName("Тузик");
    }

    @Test
    void createAnimalTest() throws Exception {
        when(animalService.createAnimal(any(ShelterId.class), any(Animal.class))).thenReturn(animal);

        mockMvc.perform(post("/animal/{shelter_id}", "DOG")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.animalName").value("Тузик"));
    }

    @Test
    void getAnimalTest() throws Exception {
        when(animalService.getAnimal(any(ShelterId.class), any(Integer.class))).thenReturn(animal);

        mockMvc.perform(get("/animal/{shelter_id}/{animal_id}", "DOG", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.animalName").value("Тузик"));
    }

    @Test
    void updateAnimalTest() throws Exception {
        when(animalService.updateAnimal(any(ShelterId.class), any(Animal.class))).thenReturn(animal);

        mockMvc.perform(put("/animal/{shelter_id}", "DOG")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(animal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.animalName").value("Тузик"));
    }

    @Test
    void deleteAnimalTest() throws Exception {
        when(animalService.getAnimal(any(ShelterId.class), any(Integer.class))).thenReturn(animal);
        when(animalService.deleteAnimal(any(ShelterId.class), any(Integer.class))).thenReturn(animal);

        mockMvc.perform(delete("/animal/{shelter_id}/{animal_id}", "DOG", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.animalName").value("Тузик"));
    }

    @Test
    void getAllAnimalsTest() throws Exception {
        Animal animal1;
        animal1 = new Animal();
        animal1.setId(2);
        animal1.setAnimalName("Басик");
        Collection<Animal> animals = Arrays.asList(animal, animal1);
        when(animalService.getAllAnimals(any(ShelterId.class))).thenReturn(animals);

        mockMvc.perform(get("/animal/{shelter_id}/all", "DOG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].animalName").value("Тузик"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].animalName").value("Басик"));
    }
}