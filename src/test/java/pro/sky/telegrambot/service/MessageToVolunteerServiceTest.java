package pro.sky.telegrambot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.configuration.TelegramBot;
import pro.sky.telegrambot.exception.MessageToVolunteerNotFoundException;
import pro.sky.telegrambot.model.entity.MessageToVolunteer;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.MessageToVolunteerRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageToVolunteerServiceTest {

    @Mock
    private MessageToVolunteerRepository messageToVolunteerRepository;

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private MessageToVolunteerService messageToVolunteerService;

    private MessageToVolunteer messageToVolunteer1;

    private MessageToVolunteer messageToVolunteer2;

    @BeforeEach
    public void beforeEach() {
        User user1 = new User();
        User user2 = new User();
        messageToVolunteer1 = new MessageToVolunteer();
        messageToVolunteer2 = new MessageToVolunteer();
        messageToVolunteer1.setId(1);
        messageToVolunteer1.setUser(user1);
        messageToVolunteer1.setQuestionTime(LocalDateTime.now());
        messageToVolunteer1.setQuestion("Вопрос 1");
        messageToVolunteer1.setAnswerTime(null);
        messageToVolunteer1.setAnswer(null);

        messageToVolunteer2.setId(2);
        messageToVolunteer2.setUser(user2);
        messageToVolunteer2.setQuestionTime(LocalDateTime.now());
        messageToVolunteer2.setQuestion("Вопрос 2");
        messageToVolunteer2.setAnswerTime(null);
        messageToVolunteer2.setAnswer(null);
    }

    /*
    @Test
    public void createTest() {
        User user3 = new User();
        MessageToVolunteer messageToVolunteer3 = new MessageToVolunteer();
        messageToVolunteer3.setUser(user3);
        LocalDateTime questionTime = LocalDateTime.now();
        String question = "question";
        messageToVolunteer3.setQuestionTime(questionTime);
        messageToVolunteer3.setQuestion(question);
        when(messageToVolunteerRepository.save(messageToVolunteer3)).thenReturn(messageToVolunteer3);
        messageToVolunteerService.create(0, user3, question);
        verify(messageToVolunteerRepository, atLeast(1)).save(any());
    }
    */
    @Test
    public void findAllWithoutAnswerTest() {
        List<MessageToVolunteer> messageToVolunteers = List.of(messageToVolunteer1, messageToVolunteer2);
        when(messageToVolunteerRepository.findAllByAnswerIsNull()).thenReturn(messageToVolunteers);
        assertThat(messageToVolunteerService.findAllWithoutAnswer())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyInAnyOrder(messageToVolunteer1, messageToVolunteer2);
    }

    @Test
    public void updateAnswerTest() {
        int id = 1;
        String answer = "answer";
        boolean answerToMessage = true;
        when(messageToVolunteerRepository.findById(any())).thenReturn(Optional.of(messageToVolunteer1));
        messageToVolunteerService.updateAnswer(id, answer, answerToMessage);
        verify(messageToVolunteerRepository, atLeast(1)).save(messageToVolunteer1);
    }

    @Test
    public void updateAnswerNegativeTest() {
        int id = 1;
        String answer = "answer";
        boolean answerToMessage = true;
        when(messageToVolunteerRepository.findById(any())).thenReturn(Optional.empty());
        assertThatExceptionOfType(MessageToVolunteerNotFoundException.class)
                .isThrownBy(() -> messageToVolunteerService.updateAnswer(id, answer, answerToMessage));
        verify(messageToVolunteerRepository, atLeast(0)).save(messageToVolunteer1);
    }

}