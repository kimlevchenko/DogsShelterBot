package pro.sky.telegrambot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.entity.FeedbackRequest;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.FeedbackRequestRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackRequestServiceTest {

    @Mock
    private FeedbackRequestRepository feedbackRequestRepository;

    @InjectMocks
    private FeedbackRequestService feedbackRequestService;

    @Test
    void createFeedbackRequestTest() {
        User user1 = new User();
        String contact1 = "contact1";
        feedbackRequestService.createFeedbackRequest(user1, contact1);

        verify(feedbackRequestRepository, only()).save(any());
    }

    @Test
    void getWaitingListTest() {
        User user1 = new User();
        String contact1 = "contact1";
        FeedbackRequest feedbackRequest = new FeedbackRequest(user1, LocalDateTime.now(), contact1);
        List<FeedbackRequest> feedbackRequests = new ArrayList<>();
        feedbackRequests.add(feedbackRequest);
        when(feedbackRequestRepository.findAllByExecutionTimeIsNull()).thenReturn(feedbackRequests);

        assertThat(feedbackRequestService.getWaitingList()).isEqualTo(feedbackRequests);
        verify(feedbackRequestRepository, atLeast(1)).findAllByExecutionTimeIsNull();
    }

    @Test
    void getFeedbackRequestTest() {
        User user2 = new User();
        String contact2 = "contact2";
        FeedbackRequest feedbackRequest2 = new FeedbackRequest(user2, LocalDateTime.now(), contact2);

        when(feedbackRequestRepository.findById(any())).thenReturn(Optional.of(feedbackRequest2));
        Assertions.assertTrue(feedbackRequest2.equals(feedbackRequestService.getFeedbackRequest(2)));
        verify(feedbackRequestRepository, only()).findById(any());

        when(feedbackRequestRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> feedbackRequestService.getFeedbackRequest(3));
        verify(feedbackRequestRepository, times(2)).findById(any());
    }

    @Test
    public void updateExecutionTimeTest() {
        int id = 1;
        User user1 = new User();
        String contact1 = "contact1";
        FeedbackRequest feedbackRequest = new FeedbackRequest(user1, null, contact1);
        when(feedbackRequestRepository.findById(any())).thenReturn(Optional.of(feedbackRequest));
        feedbackRequest.setExecutionTime(LocalDateTime.now());
        feedbackRequestService.updateExecutionTime(id);
        verify(feedbackRequestRepository, atLeast(1)).save(feedbackRequest);
    }

    @Test
    void updateExecutionTimeNegativeTest() {
        User user1 = new User();
        String contact1 = "contact1";
        FeedbackRequest feedbackRequest = new FeedbackRequest(user1, null, contact1);
        when(feedbackRequestRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> feedbackRequestService.updateExecutionTime(any()));
        verify(feedbackRequestRepository, atLeast(0)).save(feedbackRequest);
    }

}