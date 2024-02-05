package pro.sky.telegrambot.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.telegrambot.configuration.TelegramBot;
import pro.sky.telegrambot.model.entity.MessageToVolunteer;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.MessageToVolunteerRepository;
import pro.sky.telegrambot.service.MessageToVolunteerService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageToVolunteerController.class)
public class MessageToVolunteerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageToVolunteerRepository messageToVolunteerRepository;

    @SpyBean
    private MessageToVolunteerService messageToVolunteerService;

    @MockBean
    private TelegramBot telegramBot;

    //@InjectMocks
    //private MessageToVolunteerController messageToVolunteerController;
    //Контроллер здесь не нужен. К нему нет обращений
    //В шпаргалке здесь создан контроллер, чтобы проверить, что он не null.

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    public void findAllWithoutAnswerTest() throws Exception {
        List<MessageToVolunteer> messageToVolunteers = new ArrayList<>();
        messageToVolunteers.add(messageToVolunteer1);
        messageToVolunteers.add(messageToVolunteer2);

        when(messageToVolunteerRepository.findAllByAnswerIsNull()).thenReturn(messageToVolunteers);

        mockMvc.perform(
                        get("/message_to_volunteer/all_without_answer")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    List<MessageToVolunteer> messageToVolunteers1 = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<MessageToVolunteer>>() {
                            }
                    );
                    assertThat(messageToVolunteers1).isNotNull().isNotEmpty();
                    assertThat(messageToVolunteers1.size()).isEqualTo(messageToVolunteers.size());
                });
    }

    @Test
    public void updateAnswerTest() throws Exception {
        int id = 1;
        String answer = "Answer";
        boolean answerToMessage = true;
        when(messageToVolunteerRepository.findById(id)).thenReturn(Optional.of(messageToVolunteer1));
        messageToVolunteer1.setAnswerTime(LocalDateTime.now());
        messageToVolunteer1.setAnswer(answer);
        when(messageToVolunteerRepository.save(any())).thenReturn(messageToVolunteer1);

        mockMvc.perform(
                put("/message_to_volunteer/1/?answer={Answer}&replyToMessage={true}", answer, answerToMessage)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        // not found checking
        when(messageToVolunteerRepository.findById(2)).thenReturn(Optional.empty());

        mockMvc.perform(
                        put("/message_to_volunteer/2/?answer={Answer}&replyToMessage={true}", answer, answerToMessage)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString)
                            .isEqualTo("MessageToVolunteer not found. MessageToVolunteer with id: 2 is not found!");
                });
    }

}
