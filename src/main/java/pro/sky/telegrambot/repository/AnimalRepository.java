package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.animal.Animal;

import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    Optional<Object> findById(Long animalId);
}
