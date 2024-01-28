package pro.sky.telegrambot.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pro.sky.telegrambot.model.entity.FeedbackRequest;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.FeedbackRequestRepository;
import pro.sky.telegrambot.service.FeedbackRequestService;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FeedbackRequestController.class)
class FeedbackRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackRequestRepository feedbackRequestRepository;

    @SpyBean
    private FeedbackRequestService feedbackRequestService;

    //@InjectMocks
    //private FeedbackRequestController feedbackRequestController;
    //Контроллер здесь не нужен. К нему нет обращений

    static List<FeedbackRequest> testList = new ArrayList<>();

    @BeforeAll
    static void setUp() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        testList.add(new FeedbackRequest(user1, LocalDateTime.now(), "contact1"));
        testList.add(new FeedbackRequest(user2, LocalDateTime.now(), "contact2"));
        testList.add(new FeedbackRequest(user3, LocalDateTime.now(), "contact3"));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getWaitingListTest() throws Exception {
        when(feedbackRequestRepository.findAllByExecutionTimeIsNull()).thenReturn(testList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/feedback_request/waiting_list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    jsonPath("$[0].contact").value("contact1");
                    jsonPath("$[1].contact").value("contact2");
                    jsonPath("$[2].contact").value("contact3");
                });
    }

    @Test
    void getFeedbackRequestTest() throws Exception {
        when(feedbackRequestRepository.findById(any(Integer.class))).thenReturn(Optional.of(testList.get(2)))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/feedback_request/3")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    jsonPath("$.contact").value("contact3");
                });

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/feedback_request/4")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String resultAsString = result.getResponse().getContentAsString();
                    Assertions.assertThat(resultAsString).isEqualTo("Entity by this Id not found. FeedBackRequest with id = 4 not found");
                });
    }

    @Test
    void updateExecutionTimeTest() throws Exception {
        when(feedbackRequestRepository.findById(any(Integer.class))).thenReturn(Optional.of(testList.get(2)));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/feedback_request/3")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                            jsonPath("$.contact").value("contact3");
                            jsonPath("$.executionTime").isEmpty();
                        }
                );

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put("/feedback_request/3"))
                .andExpect(status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/feedback_request/3")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                            jsonPath("$.contact").value("contact3");
                            jsonPath("$.executionTime").isNotEmpty();
                        }
                );

    }
}