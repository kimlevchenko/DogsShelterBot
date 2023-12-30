package pro.sky.telegrambot.service;

import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.repository.AnimalRepository;

import java.util.Collection;
import java.util.Collections;

public class AnimalService {
    private final AnimalRepository repository;

    public AnimalService(AnimalRepository repository) {
        this.repository = repository;
    }
    public Animal findAnimal(long id) {
        return repository.findById(id).orElse(null); // вывели
    }
    public Animal addAnimal(Animal animal) {
        return repository.save(animal); // добавили
    }
    public Animal editAnimal(Animal animal) {
        return repository.findById(animal.getId())
                .map(entity -> repository.save(animal))
                .orElse(null); //заменили
    }
    public Animal deletedAnimal(Long id) {
        var entity = repository.findById(id).orElse(null);
        if (entity != null) {
            repository.delete(entity);
        }
        return entity; // удалили
    }
    public Collection<Animal> filterByAge(int age) {
        return repository.findAllByAge(age); //вывели список животных по возрасту
    }
    public Collection<String> filterAllAnimal() {
        return Collections.singleton(repository.toString());//вывели список животных
    }
}
