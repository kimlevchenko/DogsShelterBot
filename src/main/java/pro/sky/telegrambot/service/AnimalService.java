package pro.sky.telegrambot.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.ShelterId;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.model.animal.Cat;
import pro.sky.telegrambot.model.animal.Dog;
import pro.sky.telegrambot.repository.CatRepository;
import pro.sky.telegrambot.repository.DogRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class AnimalService {
    private final DogRepository dogRepository;
    private final CatRepository catRepository;
    private final ShelterService shelterService;

    public AnimalService(DogRepository dogRepository, CatRepository catRepository, ShelterService shelterService) {
        this.dogRepository = dogRepository;
        this.catRepository = catRepository;
        this.shelterService = shelterService;
    }

    private JpaRepository<? extends Animal, Integer> animalRepository(ShelterId shelterId) {
        return (shelterId == ShelterId.DOG) ? dogRepository : catRepository;
    }

    public Animal getAnimal(ShelterId shelterId, int id) {
        shelterService.checkShelterId(shelterId);
        return animalRepository(shelterId).findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Animal with id " + id + " in shelter " + shelterId + " not found"));
    }

    public Animal createAnimal(ShelterId shelterId, Animal animal) {
        shelterService.checkShelterId(shelterId);
        if (shelterId == ShelterId.DOG) {
            Dog dog = new Dog(animal);
            dog.setId(0);
            return dogRepository.save(dog);
        } else {
            Cat cat = new Cat(animal);
            cat.setId(0);
            return catRepository.save(cat);
        }
    }

    public Animal updateAnimal(ShelterId shelterId, Animal animal) {
        shelterService.checkShelterId(shelterId);
        getAnimal(shelterId, animal.getId());
        if (shelterId == ShelterId.DOG) {
            return dogRepository.save(new Dog(animal));
        } else {
            return catRepository.save(new Cat(animal));
        }
    }

    public Animal deleteAnimal(ShelterId shelterId, int id) {
        shelterService.checkShelterId(shelterId);
        Animal animal = getAnimal(shelterId, id);
        animalRepository(shelterId).deleteById(id);
        return animal;
    }

    public Collection<Animal> getAllAnimals(ShelterId shelterId) {
        shelterService.checkShelterId(shelterId);
        return List.copyOf(animalRepository(shelterId).findAll());
    }
}
