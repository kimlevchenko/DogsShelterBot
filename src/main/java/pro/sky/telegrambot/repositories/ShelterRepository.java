package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.Shelter;
import pro.sky.telegrambot.model.ShelterId;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, ShelterId> {


}

