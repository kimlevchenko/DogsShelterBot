package pro.sky.telegrambot.service;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.entity.FeedbackRequest;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.FeedbackRequestRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис "запрос обратной связи".
 * Метод save создает запись в БД в таблице "feedback_request"
 * с указанием пользователя chatId, временем запроса requestTime и контактными данными contact.
 * Метод getWaitingList показывает список всех ожидающих вызова пользователей.
 * Метод executionTimeUpdate заполняет поле executionTime текущим временем после связи с пользователем.
 */

@Service
public class FeedbackRequestService {

    private final FeedbackRequestRepository feedBackRequestRepository;

    public FeedbackRequestService(FeedbackRequestRepository feedBackRequestRepository) {
        this.feedBackRequestRepository = feedBackRequestRepository;
    }

    /**
     * Cоздает запись в БД в таблице "feedback_request".<br>
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param user    пользователь
     * @param contact контактная информация
     */
    public void createFeedbackRequest(User user, String contact) {
        feedBackRequestRepository.save(new FeedbackRequest(user, LocalDateTime.now(), contact));
    }

    /**
     * Находит список всех ожидающих вызова запросов пользователей.
     * Используется метод репозитория {@link JpaRepository#findAll(Sort)}
     *
     * @return список запросов с нулевым полем executionTime.
     */
    public List<FeedbackRequest> getWaitingList() {
        return feedBackRequestRepository.findAllByExecutionTimeIsNull();
    }

    /**
     * Находит запись в БД в таблице "feedback_request" по ее идентификатору.<br>
     * Используется метод репозитория {@link JpaRepository#findById(Object)}
     *
     * @param id идентификатор запроса.
     * @throws EntityNotFoundException если запроса с таким идентификатором нет в БД.
     */
    public FeedbackRequest getFeedbackRequest(Integer id) {
        return feedBackRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "FeedBackRequest with id = " + id + " not found"));
    }

    /**
     * Изменяет запись в БД в таблице "feedback_request".<br>
     * Заполняет поле executionTime текущим временем после связи с пользователем.
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param id идентификатор запроса.
     * @throws EntityNotFoundException если запроса с таким идентификатором нет в БД.
     */
    public void updateExecutionTime(Integer id) {
        FeedbackRequest feedBackRequest = feedBackRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "FeedBackRequest with id = " + id + " not found"));
        feedBackRequest.setExecutionTime(LocalDateTime.now());
        feedBackRequestRepository.save(feedBackRequest);
    }

}
