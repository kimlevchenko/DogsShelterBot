package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.entity.MessageToVolunteer;

import java.util.List;

@Repository
public interface MessageToVolunteerRepository extends JpaRepository<MessageToVolunteer, Integer> {
    List<MessageToVolunteer> findAllByAnswerIsNull();

}
