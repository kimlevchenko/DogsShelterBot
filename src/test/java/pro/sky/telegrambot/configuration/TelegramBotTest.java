package pro.sky.telegrambot.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.model.entity.*;
import pro.sky.telegrambot.model.adoption.*;
import pro.sky.telegrambot.model.animal.*;
import pro.sky.telegrambot.model.report.*;
import pro.sky.telegrambot.model.entity.MessageToVolunteer;
import pro.sky.telegrambot.model.entity.Shelter;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.model.state.NamedState;
import pro.sky.telegrambot.model.state.State;
import pro.sky.telegrambot.model.state.StateButton;
import pro.sky.telegrambot.repository.*;
import pro.sky.telegrambot.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramBotTest {
    //репозитории мокаем
    @Mock
    private UserRepository userRepository;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private MessageToVolunteerRepository messageToVolunteerRepository;
    @Mock
    private FeedbackRequestRepository feedbackRequestRepository;
    @Mock
    private DogAdoptionRepository dogAdoptionRepository;
    @Mock
    private CatAdoptionRepository catAdoptionRepository;
    @Mock
    private DogReportRepository dogReportRepository;
    @Mock
    private CatReportRepository catReportRepository;
    @Mock
    private DogRepository dogRepository;
    @Mock
    private CatRepository catRepository;
    @Mock
    private ShelterRepository shelterRepository;

    //сервисы реальные - их тоже потестируем. Можно было упростить жизнь и их тоже замокать.
    //@InjectMocks - shelterService будем создавать сами, тогда и заинжектим репозиторий
    private ShelterService shelterService;
    @InjectMocks
    private FeedbackRequestService feedbackRequestService;
    @InjectMocks
    private MessageToVolunteerService messageToVolunteerService;
    @InjectMocks
    private AdoptionService adoptionService;
    @InjectMocks
    private ReportService reportService;
    @InjectMocks
    @Spy
    //В тестируемый бот инжектим моки и одновременно сделаем его spy-объектом
    //Реальный сервис TelegramBotSender уже является родителем TelegramBot
    //и не создается отдельно. Он подпадает под @Spy вместе с TelegramBot
    private TelegramBot telegramBot;  //out - ObjectUnderTest

    @BeforeEach
    void injectServicesIntoTelegramBot() {
        //пересоздадим shelterService, передадим туда список приютов
        Shelter dogShelter = new Shelter();
        dogShelter.setId(ShelterId.DOG);
        dogShelter.setName("Собаки");

        Shelter catShelter = new Shelter();
        catShelter.setId(ShelterId.CAT);
        catShelter.setName("Кошки");
        when(shelterRepository.findAll()).thenReturn(List.of(dogShelter, catShelter));
        shelterService = new ShelterService(shelterRepository); //в конструкторе считываются все приюты
        //для работы бота передадим в него сервисы с заинжекченными моками репозиториев
        telegramBot.setServices(shelterService, adoptionService, reportService,
                feedbackRequestService, messageToVolunteerService);
    }

    @Test
    public void onUpdateReceived_WhenNewUser() throws TelegramApiException {
        //При поиске пользователя возвратим, что не найден
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        //при посылке сообщения - ошибку не выбрасываем
        //так возникает TelegramApiException: Parameter method can not be null
        //when(spyTelegramBot.execute(any(SendMessage.class))).thenReturn(null); т.е. сначала идет ошибка, а потом задание вернуть null
        doReturn(null).when(telegramBot).execute(any(SendMessage.class));

        //создадим объект - начальное состояние с кнопкой. Туда мы будем переводить пользователя
        State initialState = new State("1", "Начало", false, NamedState.INITIAL_STATE,
                Collections.singletonList(new StateButton(
                        null, "Первая кнопка", null, (byte) 1, (byte) 1, null)));

        //передадим созданное состояние в бот через мок репозитория состояний
        when(stateRepository.findByNamedState(NamedState.INITIAL_STATE)).thenReturn(initialState);
        telegramBot.initStates();

        //Метод замоканного объекта UserRepository.save ничего не возвращает, далать when не нужно
        //а нужно будет проверить, какие параметры попадают ему на вход

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение с id 123 c текстом 'abc'
        Message message = new Message();
        message.setChat(new Chat(1L, ""));
        message.setText("abc");

        Update update = new Update();
        update.setUpdateId(123);
        update.setMessage(message);

        //Инициируем получение сообщения
        telegramBot.onUpdateReceived(update);
        //Проверим, что было вызвано в spy и mock - объектах

        //Должно было быть послано 2 сообщения. Проверим это
        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        //Интересно, что посылаем мы сервисом TelegramBotSender, а следим за TelegramBot.
        //Но из-за наследования все работает
        verify(telegramBot, times(2)).execute(sendMessageCaptor.capture());
        List<SendMessage> actualSendMessages = sendMessageCaptor.getAllValues();
        assertEquals("1", actualSendMessages.get(0).getChatId());
        assertEquals("Привет, null", actualSendMessages.get(0).getText());
        assertEquals("Начало", actualSendMessages.get(1).getText());

        //Должен быть 1 поиск в репозитории User
        verify(userRepository, times(1)).findById(1L); //times(1) можно не писать
        //Должно быть 1 сохранение в репозиторий User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());  //если подставить user, почему-то не работает new User(1L, null, initialState)
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий
        assertEquals(1, actualUser.getId());
        assertEquals(null, actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(initialState, actualUser.getState());
    }

    @Test
    public void onUpdateReceived_GoToNextState() throws TelegramApiException {
        //создадим объект - какое-то не именованное старое состояние c кодом 11 с какой-то кнопкой
        //которая переводит в новое состояние 22 c другой кнопкой
        //Напомню, что только если в новом состоянии будут кнопки переход туда состоится,
        //иначе при отсутствии кнопок пользователю бросается текст состояния, а перехода не происходит

        State newState = new State(
                "22", "Новое состояние", false, null, new ArrayList<>());
        StateButton otherButton = new StateButton(
                newState, "Другая кнопка", null, (byte) 1, (byte) 1, null);
        newState.getButtons().add(0, otherButton);

        State oldState = new State(
                "11", "Старое состояние", false, null, new ArrayList<>());
        StateButton anyButton = new StateButton(
                oldState, "Какая-то кнопка", newState, (byte) 1, (byte) 1, null);
        oldState.getButtons().add(0, anyButton);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии 11 (старое состояние)
        //и от него пришел текст "Какая-то кнопка". Ожидаю, что новое состояние будет 22 - новое состояние

        User user = new User(1L, "Какой-то юзер", oldState);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        //при посылке сообщения - ошибку не выбрасываем
        doReturn(null).when(telegramBot).execute(any(SendMessage.class));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c текстом 'Какая-то кнопка'
        Message message = new Message();
        message.setChat(new Chat(1L, ""));
        message.setText("Какая-то кнопка");

        Update update = new Update();
        //update.setUpdateId(123);
        update.setMessage(message);

        //Инициируем получение сообщения
        telegramBot.onUpdateReceived(update);
        //Проверим, что было вызвано в spy и mock - объектах

        //Должно было быть послано 1 сообщение. Проверим это
        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class); //создаем ловца SendMessage
        verify(telegramBot).execute(sendMessageCaptor.capture()); //times(1) можем не указывать
        SendMessage actualSendMessage = sendMessageCaptor.getValue();  //вынем из ловца, то что он словил
        assertEquals("1", actualSendMessage.getChatId()); //проверяем, что сообщение послано пользователю 1
        assertEquals("Новое состояние", actualSendMessage.getText());

        //Должен быть 1 поиск в репозитории
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Должно быть 1 сохранение в репозиторий
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(newState, actualUser.getState());

        //----------------------------------------------------------
        //Если в сообщении состояния используется служебный символ @,
        //то надо при переходе в новое состояние взять текст из описания приюта
        //Если старое состояние начальное (т.е. со списком приютов),
        //то надо задать у пользователя приют и только потом перейти в Новое состояние.
        //Сымитируем эту нереальную ситуацию с двумя особенностями сразу для большего покрытия
        newState.setText("@information");
        try {
            shelterService.setInformation(ShelterId.DOG, "information", "Информация о приюте");
        } catch (IllegalAccessException e) {
        }
        //создадим объект - начальное состояние. Оттуда мы будем переводить пользователя
        //кнопок там нет. Сообщение будет сравниваться не с кнопками, а с именами приютов
        oldState.setNamedState(NamedState.INITIAL_STATE);
        user.setState(oldState);
        message.setText("Собаки");

        //try{
        //    when(shelterService.getInformation(any(),any())).thenReturn("Информация о приюте");
        //} catch (IllegalAccessException e) {}

        //Еще в боте надо задать поле afterShelterChoiceState,
        //т.к. именно туда перейдет пользователь после выбора приюта
        //передадим в бот новое состояние через мок репозитория состояний
        when(stateRepository.findByNamedState(NamedState.INITIAL_STATE)).thenReturn(oldState);
        when(stateRepository.findByNamedState(NamedState.BAD_CHOICE)).thenReturn(null);
        when(stateRepository.findByNamedState(NamedState.AFTER_SHELTER_CHOICE_STATE)).thenReturn(newState);
        telegramBot.initStates();

        reset(telegramBot);  //обнуляем счетчик
        doReturn(null).when(telegramBot).execute(any(SendMessage.class));
        //Инициируем получение сообщения
        telegramBot.onUpdateReceived(update);
        //В ответ должно было быть послано 1 сообщение. Проверим это
        sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class); //обнуляем ловца
        verify(telegramBot).execute(sendMessageCaptor.capture()); //times(1) можем не указывать
        actualSendMessage = sendMessageCaptor.getValue();  //вынем из ловца, то что он словил
        assertEquals("1", actualSendMessage.getChatId()); //проверяем, что сообщение послано пользователю 1
        assertEquals("Информация о приюте", actualSendMessage.getText());
    }

    @Test
    public void onUpdateReceived_MessageToVolunteer() throws TelegramApiException {
        //создадим объект - состояние messageToVolunteer
        State state = new State("MessageToVolunteer",
                "Введите сообщение для волонтера - это пользователь уже получил. Ждем, что пришлет",
                true, NamedState.MESSAGE_TO_VOLUNTEER, null);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии MessageToVolunteer
        //и от него пришел текст "Что собаки едят?".
        //Ожидаю, что новое состояние останется прежним - MessageToVolunteer
        //а в базу запишется новая запись для волонтера. Id записи = Id пришедшего Message

        when(userRepository.findById(1L)).thenReturn(
                Optional.of(new User(1L, "Какой-то юзер", state)));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c текстом 'Что собаки едят?'
        Message message = new Message();
        message.setMessageId(123);
        message.setChat(new Chat(1L, ""));
        message.setText("Что собаки едят?");
        Update update = new Update();
        update.setMessage(message);

        //Инициируем получение сообщения
        telegramBot.onUpdateReceived(update);

        //Никаких сообщений не должно быть послано. Проверим это
        verify(telegramBot, never()).execute(any(SendMessage.class));

        //Должен быть 1 поиск User
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Проверим, что сохранено в User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий User
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(state, actualUser.getState());

        //Проверим, что сохранено в MessageToVolunteer
        ArgumentCaptor<MessageToVolunteer> messageToVolunteerCaptor = ArgumentCaptor.forClass(MessageToVolunteer.class);
        verify(messageToVolunteerRepository).save(messageToVolunteerCaptor.capture());
        MessageToVolunteer actualMessageToVolunteer = messageToVolunteerCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий id, user, questionTime, question
        assertEquals(123, actualMessageToVolunteer.getId());
        assertEquals(actualUser, actualMessageToVolunteer.getUser());
        assertEquals(0, ChronoUnit.SECONDS.between(actualMessageToVolunteer.getQuestionTime(), LocalDateTime.now()));
        assertEquals("Что собаки едят?", actualMessageToVolunteer.getQuestion());
    }

    @Test
    public void onUpdateReceived_Feedback() throws TelegramApiException {
        //создадим объект - состояние FeedbackRequest
        State state = new State("FeedbackRequest",
                "Введите контакты для обратной связи - это пользователь уже получил. Ждем, что пришлет",
                true, NamedState.FEEDBACK_REQUEST, null);
        State previousState = new State("PreviousState",
                "Предыдущее состояние", false, null, new ArrayList<>());
        //у предыдущего состояния обязательно должна быть кнопка,
        //иначе мы бы не попали в FeedbackRequest и во-вторых,
        //назад в бескнопочное состояние переход будет невозможен
        //возникнет ошибка при построении клавиатуры
        StateButton anyButton = new StateButton(
                previousState, "Какая-то кнопка", null, (byte) 1, (byte) 1, null);
        previousState.getButtons().add(0, anyButton);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии FeedbackRequest
        //с предыдущим состоянием initialState (хотя такого не бывает)
        //и от него пришел текст "Перезвоните мне по номеру 123".
        //Ожидаю, что новое состояние изменится на предыдущее - initialState
        //а в базу в табл FeedbackRequest запишется новая запись для волонтера.
        User user = new User(1L, "Какой-то юзер", state);
        user.setPreviousState(previousState);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        //при посылке сообщения - ошибку не выбрасываем
        doReturn(null).when(telegramBot).execute(any(SendMessage.class));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c текстом 'Что собаки едят?'
        Message message = new Message();
        message.setMessageId(123);
        message.setChat(new Chat(1L, ""));
        message.setText("Перезвоните мне по номеру 123");
        Update update = new Update();
        update.setMessage(message);

        //Инициируем получение сообщения
        telegramBot.onUpdateReceived(update);

        //Должно было быть послано 2 сообщения. Проверим это
        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(sendMessageCaptor.capture());
        List<SendMessage> actualSendMessages = sendMessageCaptor.getAllValues();
        assertEquals("1", actualSendMessages.get(0).getChatId());
        assertEquals("Запрос обратной связи принят. Волонтер свяжется с вами указанным способом.", actualSendMessages.get(0).getText());
        assertEquals("Предыдущее состояние", actualSendMessages.get(1).getText());

        //Должен быть 1 поиск User
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Проверим, что сохранено в User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий User
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(previousState, actualUser.getState());

        //Проверим, что сохранено в FeedbackRequest
        ArgumentCaptor<FeedbackRequest> feedbackRequestCaptor = ArgumentCaptor.forClass(FeedbackRequest.class);
        verify(feedbackRequestRepository).save(feedbackRequestCaptor.capture());
        FeedbackRequest actualFeedbackRequest = feedbackRequestCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий user, LocalDateTime.now(), contact
        assertEquals(0, actualFeedbackRequest.getId());
        assertEquals(actualUser, actualFeedbackRequest.getUser());
        assertEquals(0, ChronoUnit.SECONDS.between(actualFeedbackRequest.getRequestTime(), LocalDateTime.now()));
        assertEquals("Перезвоните мне по номеру 123", actualFeedbackRequest.getContact());
    }

    @Test
    public void onUpdateReceived_Report() throws TelegramApiException {
        //создадим объект - состояние Report - ждем отчет
        State state = new State("Report",
                "Пришлите файлы отчета - это пользователь уже получил. Ждем, что пришлет",
                true, NamedState.REPORT, null);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии Report
        //и от него пришел файл  *.jpg.
        //Ожидаю, что новое состояние останется прежним - Report
        //а в базу запишется запись для нового отчета.

        User user = new User(1L, "Какой-то юзер", state);
        user.setShelterId(ShelterId.DOG);
        DogAdoption adoption = new DogAdoption(user, new Dog(), LocalDate.of(2024, 1, 1));
        adoption.setId(11);
        DogReport report = new DogReport(adoption, LocalDate.now(), null, null, 0L, null);
        report.setId(111L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(dogReportRepository.save(any())).thenReturn(report);
        //у пользователя есть активное усыновление. API волонтера следит, чтобы такое было только одно
        when(dogAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(any(), any(), any()))
                .thenReturn(Collections.singletonList(adoption));
        //Усыновление-Дата - уникальный индекс таблицы. Двух таких комбинаций быть не может
        when(dogReportRepository.findByAdoptionAndDate(any(), any()))
                .thenReturn(Collections.singletonList(report));
        //при посылке сообщения - ошибку не выбрасываем
        doReturn(null).when(telegramBot).execute(any(SendMessage.class));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c текстом
        Message message = new Message();
        message.setMessageId(123);
        message.setChat(new Chat(1L, ""));
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileId("1111");
        photoSize.setFileSize(1234);
        //мокаем все методы бота, участвующие в скачивании файла
        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                "{\"ok\":true,\"result\":{\"file_id\":\"abcd\",\"file_unique_id\":\"dcba\",\"file_size\":12345,\"file_path\":\"photos/file_1.jpg\"}}"
                , HttpStatus.OK);
        doReturn(responseEntity).when(telegramBot).getFilePath(anyString());
        doReturn("https://api.telegram.org/bot{telegram.bot.token}/getFile?file_id={fileId}")
                .when(telegramBot).getFileStorageUri();
        doReturn("12345").when(telegramBot).getBotToken();
        doReturn(new byte[]{1, 2}).when(telegramBot).downloadJFileByURL(any());

        message.setPhoto(Collections.singletonList(photoSize));
        message.setText("Сдаю отчет");
        Update update = new Update();
        update.setMessage(message);

        //Инициируем получение сообщения
        telegramBot.onUpdateReceived(update);

        //В ответ должно было быть послано 1 сообщение. Проверим это
        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class); //создаем ловца SendMessage
        verify(telegramBot).execute(sendMessageCaptor.capture()); //times(1) можем не указывать
        SendMessage actualSendMessage = sendMessageCaptor.getValue();  //вынем из ловца, то что он словил
        assertEquals("1", actualSendMessage.getChatId()); //проверяем, что сообщение послано пользователю 1


        //Должен быть 1 поиск User
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Проверим, что сохранено в User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий User
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(state, actualUser.getState());

        //Проверим, что сохранено в Report
        ArgumentCaptor<DogReport> reportCaptor = ArgumentCaptor.forClass(DogReport.class);
        verify(dogReportRepository).save(reportCaptor.capture());
        DogReport actualReport = reportCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий Report - id, adoption, date, photo, text
        assertEquals(111, actualReport.getId());
        assertEquals(adoption, actualReport.getAdoption());
        assertEquals(LocalDate.now(), actualReport.getDate());

        //Что будет, если у пользователя нет активного усыновления.
        //Теоретически мы должны оказаться в предыдущем состоянии
        when(dogAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        //создаем предыдущее состояние
        State previousState = new State("PreviousState",
                "Предыдущее состояние", false, null, new ArrayList<>());
        //у предыдущего состояния обязательно должна быть кнопка,
        //назад в бескнопочное состояние переход будет невозможен
        StateButton anyButton = new StateButton(
                previousState, "Какая-то кнопка", null, (byte) 1, (byte) 1, null);
        previousState.getButtons().add(0, anyButton);
        user.setPreviousState(previousState);

        reset(telegramBot);  //обнуляем счетчик
        doReturn(null).when(telegramBot).execute(any(SendMessage.class));
        message.setPhoto(null); //фотографию больше не читаем

        //Инициируем получение сообщения
        telegramBot.onUpdateReceived(update);
        //В ответ должно было быть послано 2 сообщения. Проверим это
        sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class); //обнуляем ловца
        verify(telegramBot, times(2)).execute(sendMessageCaptor.capture()); //times(1) можем не указывать
        List<SendMessage> actualSendMessages = sendMessageCaptor.getAllValues();  //вынем из ловца, то что он словил
        assertEquals("1", actualSendMessages.get(0).getChatId()); //проверяем, что сообщение послано пользователю 1
        assertEquals("В приюте Собаки у Вас нет активного испытательного срока", actualSendMessages.get(0).getText());
        assertEquals("Предыдущее состояние", actualSendMessages.get(1).getText());
    }
}