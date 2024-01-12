package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.animal.Cat;

@Repository
public interface CatRepository extends JpaRepository<Cat, Integer> {
}
