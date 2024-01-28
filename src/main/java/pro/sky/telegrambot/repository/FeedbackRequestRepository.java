package pro.sky.telegrambot.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.entity.FeedbackRequest;

import java.util.List;

public interface FeedbackRequestRepository extends JpaRepository<FeedbackRequest, Integer> {

    List<FeedbackRequest> findAllByExecutionTimeIsNull();

}
