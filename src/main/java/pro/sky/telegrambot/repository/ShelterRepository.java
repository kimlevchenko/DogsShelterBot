package pro.sky.telegrambot.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.entity.Shelter;
import pro.sky.telegrambot.model.entity.ShelterId;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, ShelterId> {


}
