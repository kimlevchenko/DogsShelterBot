package pro.sky.telegrambot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.model.adoption.*;
import pro.sky.telegrambot.model.report.*;
import pro.sky.telegrambot.model.entity.*;
import pro.sky.telegrambot.repository.*;
import pro.sky.telegrambot.service.MessageToVolunteerService;
import pro.sky.telegrambot.configuration.TelegramBotSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Проверяет каждый день в 21:01 по Московскому времени (GMT+ 3) все ежедневные отчеты усыновителей.<br>
 * Если усыновитель не прислал, или прислал не полный отчет напоминает ему об этом.
 * Если усыновитель не присылает отчет более 2 дней извещает волонтера.<br>
 * Проверяет каждый день в 23:01 Московскому времени (GMT+ 3) все усыновления.<br>
 * Если пользователю не продлили испытательный период, поздравляет его.
 */
@Component
@EnableScheduling
public class Notifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(Notifier.class);

    private final CatAdoptionRepository catAdoptionRepository;
    private final DogAdoptionRepository dogAdoptionRepository;
    private final CatReportRepository catReportRepository;
    private final DogReportRepository dogReportRepository;
    private final MessageToVolunteerService messageToVolunteerService;
    private final TelegramBotSender telegramBotSender;

    public Notifier(CatAdoptionRepository catAdoptionRepository,
                    DogAdoptionRepository dogAdoptionRepository,
                    CatReportRepository catReportRepository,
                    DogReportRepository dogReportRepository,
                    MessageToVolunteerService messageToVolunteerService,
                    TelegramBotSender telegramBotSender) {
        this.catAdoptionRepository = catAdoptionRepository;
        this.dogAdoptionRepository = dogAdoptionRepository;
        this.catReportRepository = catReportRepository;
        this.dogReportRepository = dogReportRepository;
        this.messageToVolunteerService = messageToVolunteerService;
        this.telegramBotSender = telegramBotSender;
    }

    /**
     * Проверяет каждый день в 21:01 все ежедневные отчеты усыновителей.<br>
     * Если усыновитель не прислал, или прислал не полный отчет напоминает ему об этом.Используется метод <u>sendNotification</u> этого сервиса.
     * Полноту данных проверяем методамм {@link  DogReportRepository#findByDateAndDataIsNotNullAndTextIsNotNull(LocalDate)} и
     * {@link CatReportRepository#findByDateAndDataIsNotNullAndTextIsNotNull(LocalDate)}
     * Если усыновитель не присылает отчет более 2 дней извещает волонтера.
     * посредством {@link MessageToVolunteerRepository#save(Object)} save()}
     */
    @Scheduled(cron = "1 * * * * *")
    //@Scheduled(cron = "0 21 * * * *")
    //улучшенный формат <Минуты> <Часы> <Дни_месяца> <Месяцы> <Дни_недели> <Годы>
    @Transactional
    public void sendWarningNoReport() {
        LOGGER.info("Вызов sendWarningNoReport " + LocalDateTime.now());
        long today = ChronoUnit.DAYS.between(LocalDate.of(2022, 12, 31), LocalDate.now());
        long dayLatestReport;
        LocalDate date;
        Report report;

        List<DogAdoption> currentDogAdoptionList = dogAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now());
        Map<Object, LocalDate> todayDogReports = dogReportRepository.findByDateAndDataIsNotNullAndTextIsNotNull(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(DogReport::getAdoption, Report::getDate));
        for (DogAdoption adoption : currentDogAdoptionList) {
            if (todayDogReports.containsKey(adoption)) {
                break;
            } else {
                report = dogReportRepository.findLatestReport(adoption.getId());
                date = (report != null) ? report.getDate() : adoption.getDate();
                dayLatestReport = ChronoUnit.DAYS.between(LocalDate.of(2022, 12, 31), date);
                if (today - dayLatestReport > 2) {
                    LOGGER.info("Вызов messageToVolunteer");
                    messageToVolunteerService.createMessageToVolunteer(adoption.getId(), adoption.getUser(),
                            "ВНИМАНИЕ !!! Опекун " + adoption.getUser().getName()
                                    + " не присылал ежедневный отчет по собаке " + adoption.getAnimal().getAnimalName() + " более 2х дней.");
                }
                sendNotification(adoption, "ВНИМАНИЕ !!! " + adoption.getUser().getName() +
                        ", просим Вас присылать ежедневный отчет по собаке " + adoption.getAnimal().getAnimalName() + " до 21:00.");
            }
        }

        List<CatAdoption> currentCatAdoptionList = catAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now());
        Map<Object, LocalDate> todayCatReports = catReportRepository.findByDateAndDataIsNotNullAndTextIsNotNull(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(CatReport::getAdoption, Report::getDate));
        for (CatAdoption adoption : currentCatAdoptionList) {
            if (todayCatReports.containsKey(adoption)) {
                break;
            } else {
                report = catReportRepository.findLatestReport(adoption.getId());
                date = (report != null) ? report.getDate() : adoption.getDate();
                dayLatestReport = ChronoUnit.DAYS.between(LocalDate.of(2022, 12, 31), date);
                if (today - dayLatestReport > 2) {
                    messageToVolunteerService.createMessageToVolunteer(adoption.getId(), adoption.getUser(),
                            "ВНИМАНИЕ !!! Опекун " + adoption.getUser().getName()
                                    + " не присылал ежедневный отчет по кошке " + adoption.getAnimal().getAnimalName() + " более 2х дней.");
                }
                sendNotification(adoption, "ВНИМАНИЕ !!! " + adoption.getUser().getName() +
                        ", просим Вас присылать ежедневный отчет по кошке " + adoption.getAnimal().getAnimalName() + " до 21:00.");
            }
        }
    }

    /**
     * Проверяет каждый день в 23:01 все усыновления.<br>
     * Если пользователю не продлили испытательный период, поздравляет его.
     * Используется метод <u>sendNotification</u> этого сервиса.
     */
    //@Scheduled(cron = "0 22 * * * *")  //в 22 часа каждый день
    @Scheduled(cron = "1 * * * * *")
    @Transactional
    public void sendCongratulation() {
        List<DogAdoption> currentDogAdoptionList = dogAdoptionRepository.findByTrialDate(LocalDate.now());
        for (DogAdoption adoption : currentDogAdoptionList) {
            sendNotification(adoption, adoption.getUser().getName()
                    + "! Поздравляем !!! Вы успешно прошли испытательный период. "
                    + "Всего наилучшего Вам и вашему питомцу.");
        }
        List<CatAdoption> currentCatAdoptionList = catAdoptionRepository.findByTrialDate(LocalDate.now());
        for (CatAdoption adoption : currentCatAdoptionList) {
            sendNotification(adoption, adoption.getUser().getName()
                    + "! Поздравляем !!! Вы успешно прошли испытательный период. "
                    + "Всего наилучшего Вам и вашему питомцу.");
        }
    }

    /**
     * Отправляет уведомление пользователю, проходящему испытательный период.<br>
     * Используется метод сервиса {@link TelegramBotSender#sendMessageToUser(User, String, int)}  }
     *
     * @param adoption     (пользователь является усыновителем, получаем через adoption.getUser())
     * @param notification текст уведомления.
     */
    public void sendNotification(Adoption adoption, String notification) {
        //это почти тоже что sendMessageToUser, но не сигналит об ошибке отправки
        //что не допустимо для вызова из контроллеров.
        //а если по расписанию, то можно ошибку и проигнорировать
        try {
            telegramBotSender.sendMessageToUser(adoption.getUser(), notification, 0);
        } catch (TelegramApiException e) {
            LOGGER.error("TelegramError " + e.getMessage());
        }
    }
}
