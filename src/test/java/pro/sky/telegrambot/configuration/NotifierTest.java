package pro.sky.telegrambot.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.configuration.Notifier;
import pro.sky.telegrambot.configuration.TelegramBotSender;
import pro.sky.telegrambot.model.adoption.Adoption;
import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.animal.Cat;
import pro.sky.telegrambot.model.animal.Dog;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.CatAdoptionRepository;
import pro.sky.telegrambot.repository.DogAdoptionRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotifierTest {

    @Mock
    private CatAdoptionRepository catAdoptionRepository;
    @Mock
    private DogAdoptionRepository dogAdoptionRepository;
    @Mock
    private TelegramBotSender telegramBotSender;

    @InjectMocks
    private Notifier notifier;


    @Test
    void sendCongratulationCatAdoptionTest() throws TelegramApiException {
        Cat cat1 = new Cat();
        cat1.setId(1);
        User user1 = new User();
        user1.setId(111111111);
        user1.setName("Иван");
        CatAdoption adoption1 = new CatAdoption(user1, cat1, LocalDate.now());
        when(catAdoptionRepository.findByTrialDate(LocalDate.now())).thenReturn(List.of(adoption1));
        notifier.sendCongratulation();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(telegramBotSender, only()).sendMessageToUser(
                userArgumentCaptor.capture(), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(user1, userArgumentCaptor.getValue());
        assertEquals("Иван! Поздравляем !!! Вы успешно прошли испытательный период. " +
                "Всего наилучшего Вам и вашему питомцу.", stringArgumentCaptor.getValue());
    }

    @Test
    void sendCongratulationDogAdoptionTest() throws TelegramApiException {

        Dog dog4 = new Dog();
        dog4.setId(4);
        User user4 = new User();
        user4.setId(444444444);
        user4.setName("Мария");
        DogAdoption adoption4 = new DogAdoption(user4, dog4, LocalDate.now());
        when(dogAdoptionRepository.findByTrialDate(LocalDate.now())).thenReturn(List.of(adoption4));
        notifier.sendCongratulation();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(telegramBotSender, times(1)).sendMessageToUser(
                userArgumentCaptor.capture(), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(user4, userArgumentCaptor.getValue());
        assertEquals("Мария! Поздравляем !!! Вы успешно прошли испытательный период. " +
                "Всего наилучшего Вам и вашему питомцу.", stringArgumentCaptor.getValue());
    }

    @Test
    void sendNotificationTest() throws TelegramApiException {
        Cat cat3 = new Cat();
        cat3.setId(3);
        User user3 = new User();
        user3.setId(333333333);
        Adoption adoption3 = new CatAdoption(user3, cat3, LocalDate.now());
        String notification3 = "notification3";
        notifier.sendNotification(adoption3, notification3);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(telegramBotSender, only()).sendMessageToUser(any(User.class), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(stringArgumentCaptor.getValue(), notification3);
    }
}
