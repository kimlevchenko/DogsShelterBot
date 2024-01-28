package pro.sky.telegrambot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.model.animal.Cat;
import pro.sky.telegrambot.model.animal.Dog;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.repository.CatRepository;
import pro.sky.telegrambot.repository.DogRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для PetService
 */

@ExtendWith(MockitoExtension.class)
public class AnimalServiceTest {
    @Mock
    private DogRepository dogRepository;

    @Mock
    private CatRepository catRepository;

    @Mock
    private ShelterService shelterService;

    @InjectMocks
    private AnimalService animalService;

    private final Dog dog = new Dog();
    private final Cat cat = new Cat();

    @BeforeEach
    void setUp() {
        dog.setId(1);
        dog.setAnimalName("Тузик");
        cat.setId(2);
        cat.setAnimalName("Кокос");
    }

    @Test
    void createDogAnimal() {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        when(dogRepository.save(any(Dog.class))).thenReturn(dog);
        Animal createdAnimal = animalService.createAnimal(ShelterId.DOG, dog);

        assertNotNull(createdAnimal);
        assertEquals(dog.getId(), createdAnimal.getId());
        assertEquals(dog.getAnimalName(), createdAnimal.getAnimalName());
        verify(dogRepository).save(any(Dog.class));
    }

    @Test
    void createCatAnimal() {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.CAT);
        when(catRepository.save(any(Cat.class))).thenReturn(cat);
        Animal createdAnimal = animalService.createAnimal(ShelterId.CAT, cat);

        assertNotNull(createdAnimal);
        assertEquals(cat.getId(), createdAnimal.getId());
        assertEquals(cat.getAnimalName(), createdAnimal.getAnimalName());
        verify(catRepository).save(any(Cat.class));
    }

    @Test
    void getDogAnimal() {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        when(dogRepository.findById(dog.getId())).thenReturn(Optional.of(dog));
        Animal existingAnimal = animalService.getAnimal(ShelterId.DOG, dog.getId());

        assertNotNull(existingAnimal);
        assertEquals(dog.getId(), existingAnimal.getId());
        assertEquals(dog.getAnimalName(), existingAnimal.getAnimalName());
        verify(dogRepository).findById(dog.getId());
    }

    @Test
    void getCatAnimal() {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.CAT);
        when(catRepository.findById(cat.getId())).thenReturn(Optional.of(cat));
        Animal existingAnimal = animalService.getAnimal(ShelterId.CAT, cat.getId());

        assertNotNull(existingAnimal);
        assertEquals(cat.getId(), existingAnimal.getId());
        assertEquals(cat.getAnimalName(), existingAnimal.getAnimalName());
        verify(catRepository).findById(cat.getId());
    }

    @Test
    void getNonExistingPetShouldThrowNotFoundException() {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.CAT);
        when(catRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> animalService.getAnimal(ShelterId.CAT, 3));
    }

    @Test
    void updateDogAnimal() {
        Dog updatedDog = new Dog();
        updatedDog.setId(1);
        updatedDog.setAnimalName("Шурик");
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        when(dogRepository.findById(dog.getId())).thenReturn(Optional.of(dog));
        when(dogRepository.save(any(Dog.class))).thenReturn(updatedDog);

        Animal updatedAnimal = animalService.updateAnimal(ShelterId.DOG, updatedDog);
        assertNotNull(updatedAnimal);
        assertEquals(updatedDog.getId(), updatedAnimal.getId());
        assertEquals(updatedDog.getAnimalName(), updatedAnimal.getAnimalName());
        verify(dogRepository).save(any(Dog.class));
    }

    @Test
    void updateCatAnimal() {
        Cat updatedCat = new Cat();
        updatedCat.setId(2);
        updatedCat.setAnimalName("Пушок");
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.CAT);
        when(catRepository.findById(cat.getId())).thenReturn(Optional.of(cat));
        when(catRepository.save(any(Cat.class))).thenReturn(updatedCat);

        Animal updatedAnimal = animalService.updateAnimal(ShelterId.CAT, updatedCat);
        assertNotNull(updatedAnimal);
        assertEquals(updatedCat.getId(), updatedAnimal.getId());
        assertEquals(updatedCat.getAnimalName(), updatedAnimal.getAnimalName());
        verify(catRepository).save(any(Cat.class));
    }

    @Test
    void deleteDogAnimal() {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        when(dogRepository.findById(dog.getId())).thenReturn(Optional.of(dog));
        doNothing().when(dogRepository).deleteById(dog.getId());
        Animal deletedAnimal = animalService.deleteAnimal(ShelterId.DOG, dog.getId());

        assertNotNull(deletedAnimal);
        assertEquals(dog.getId(), deletedAnimal.getId());
        assertEquals(dog.getAnimalName(), deletedAnimal.getAnimalName());
        verify(dogRepository).deleteById(dog.getId());
    }

    @Test
    void deleteCatAnimal() {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.CAT);
        when(catRepository.findById(cat.getId())).thenReturn(Optional.of(cat));
        doNothing().when(catRepository).deleteById(cat.getId());
        Animal deletedAnimal = animalService.deleteAnimal(ShelterId.CAT, cat.getId());

        assertNotNull(deletedAnimal);
        assertEquals(cat.getId(), deletedAnimal.getId());
        assertEquals(cat.getAnimalName(), deletedAnimal.getAnimalName());
        verify(catRepository).deleteById(cat.getId());
    }
}