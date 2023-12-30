package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.model.animal.Dog;

import java.util.Collection;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Collection<Animal> findAllByAge(int age);

}
