package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.animal.Dog;

@Repository
public interface DogRepository extends JpaRepository<Dog, Integer> {

}
