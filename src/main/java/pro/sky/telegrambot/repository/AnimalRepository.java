package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.animal.Animal;

import java.util.Optional;


public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    Optional<Object> findById(Long animalId);
}
