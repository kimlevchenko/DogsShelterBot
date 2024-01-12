package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.animal.Animal;

import java.util.Optional;


public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    Optional<Object> findById(Long animalId);
}
