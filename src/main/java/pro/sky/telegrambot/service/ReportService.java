package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.TelegramApiException;
import pro.sky.telegrambot.exception.ShelterNotFoundException;
import pro.sky.telegrambot.exception.TelegramException;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.adoption.Adoption;
import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.model.report.CatReport;
import pro.sky.telegrambot.model.report.DogReport;
import pro.sky.telegrambot.model.report.Report;
import pro.sky.telegrambot.repository.CatReportRepository;
import pro.sky.telegrambot.repository.DogReportRepository;
import pro.sky.telegrambot.repository.UserRepository;
import pro.sky.telegrambot.service.ShelterService;


import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportService.class);
    private final DogReportRepository dogReportRepository;
    private final CatReportRepository catReportRepository;
    private final ShelterService shelterService;
    private final UserRepository userRepository;
    private final TelegramBotUpdatesListener telegramBotUpdatesListener;


    public ReportService(CatReportRepository catReportRepository,
                         DogReportRepository dogReportRepository,
                         ShelterService shelterService,
                         UserRepository userRepository,
                         TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.dogReportRepository = dogReportRepository;
        this.catReportRepository = catReportRepository;
        this.shelterService = shelterService;
        this.userRepository = userRepository;
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }

    //из такого репозитория удается прочитать, возвращается предок
    //но в него ничего не удается сохранить уже при компиляции, при подстановке любого типа возникает
    //method save in interface org.springframework.data.repository.CrudRepository<T,ID> cannot be applied to given types;
    private JpaRepository<? extends Report, Integer> reportRepository(ShelterId shelterId) {
        return (shelterId == ShelterId.DOG) ? dogReportRepository : catReportRepository;
    }

    /**
     * Метод сохраняет отчет по питомцу в ДБ .<br>
     * Используется метод репозитория {@link JpaRepository#save(Object)}.<br>
     * Если для заданного усыновления и даты отчет найден, то он дополняется,
     * если нет, то создается новый
     *
     * @param adoption активное усыновление пользователя
     * @param date     дата отчета
     * @param data     фото отчета, может быть null, если прислан текст
     * @param text     текст отчета, может быть null, если прислано фото
     * @return Report сохраненные данные отчета для кошки или собаки
     */
    public Report saveReport(Adoption adoption, LocalDate date, byte[] data, String mediaType, Long fileSize, String text) {
        //вызывается из бота (дата в этом случае всегда now()), волонтер отчеты только читает
        if (adoption.getUser().getShelterId() == ShelterId.DOG) {
            DogAdoption dogAdoption = (DogAdoption) adoption;
            List<DogReport> reportList = dogReportRepository.findByAdoptionAndDate(dogAdoption, date);
            DogReport report;  //объект для сохранения
            if (reportList.isEmpty()) {
                report = new DogReport(dogAdoption, LocalDate.now(), data, mediaType, fileSize, text);
            } else {
                report = reportList.get(0);
                if (date != null) {
                    report.setDate(date);
                    report.setMediaType(mediaType);
                    report.setFileSize(fileSize);
                }
                if (text != null) {
                    report.setText(text);
                }
            }
            return dogReportRepository.save(report);
        } else {
            CatAdoption catAdoption = (CatAdoption) adoption;
            List<CatReport> reportList = catReportRepository.findByAdoptionAndDate(catAdoption, date);
            CatReport report;  //объект для сохранения
            if (reportList.isEmpty()) {
                report = new CatReport(catAdoption, LocalDate.now(), data, mediaType, fileSize, text);
            } else {
                report = reportList.get(0);
                if (date != null) {
                    report.setDate(date);
                    report.setMediaType(mediaType);
                    report.setFileSize(fileSize);
                }
                if (text != null) {
                    report.setText(text);
                }
            }
            return catReportRepository.save(report);
        }
    }

    /**
     * Метод выводит отчет по индентификатору.<br>
     * Используется метод репозитория {@link JpaRepository#findById(Object)} (Object)}
     *
     * @param shelterId идентификатор приюта.
     * @param reportId  индентификатор отчета
     * @return Report возвращает отчет
     * @throws ShelterNotFoundException если id приюта не найден в базе
     * @throws EntityNotFoundException  если id отчета не найден в базе
     */
    public Report getReportById(ShelterId shelterId, int reportId) {
        shelterService.checkShelterId(shelterId);
        return reportRepository(shelterId).findById(reportId).orElseThrow(() ->
                new EntityNotFoundException("Report with id " + reportId + " in shelter " + shelterId + " not found"));
    }

    /**
     * Метод удаляет отчет по заданному индентификатору.
     * Используется метод репозитория {@link JpaRepository#deleteById(Object)}.<br><br>
     *
     * @param shelterId идентификатор приюта.
     * @param reportId  идетификатор отчета
     * @return Report возвращает удаленный отчет
     * @throws ShelterNotFoundException если id приюта не найден в базе
     * @throws EntityNotFoundException  если id отчета не найден в базе
     */
    public Report deleteReportById(ShelterId shelterId, int reportId) {
        shelterService.checkShelterId(shelterId);
        Report report = getReportById(shelterId, reportId);
        reportRepository(shelterId).deleteById(reportId);
        return report;
    }

    /**
     * Метод выводит все отчеты по дате из БД кошек или собак.<br>
     *
     * @param shelterId идентификатор приюта.
     * @param date      дата отчета
     * @return List<Report> возвращает список отчетов кошек или собак за заданную дату
     * @throws ShelterNotFoundException если id приюта не найден в базе
     */
    public List<Report> getAllReportsByDate(ShelterId shelterId, LocalDate date) {
        shelterService.checkShelterId(shelterId);
        if (shelterId == ShelterId.DOG) {
            return List.copyOf(dogReportRepository.findByDate(date));
        } else {
            return List.copyOf(catReportRepository.findByDate(date));
        }
    }

    /**
     * Метод выводит все отчеты кошек и собак из БД.<br>
     * Используется метод репозитория {@link JpaRepository#findAll()}.<br><br>
     *
     * @param shelterId идентификатор приюта.
     * @return List<Report> возвращает список всех отчетов кошек или собак
     * @throws ShelterNotFoundException если id приюта не найден в базе
     */
    public List<Report> getAllReports(ShelterId shelterId) {
        shelterService.checkShelterId(shelterId);
        return List.copyOf(reportRepository(shelterId).findAll());
    }

    /**
     * Метод находит отчет за сегодня для заданного усыновления.<br>
     * Используется для определения полноты сданного отчета
     * при выводе запроса пользователю, прислать оставшуюся чась отчета
     *
     * @param adoption усыновление, для которого ищем отчет за сегодня
     * @return Report  найденный отчет, null - если не найден
     */
    public Report getReportForToday(Adoption adoption) {
        List<? extends Report> reportList;
        if (adoption.getUser().getShelterId() == ShelterId.DOG) {
            DogAdoption dogAdoption = (DogAdoption) adoption;
            reportList = dogReportRepository.findByAdoptionAndDate(dogAdoption, LocalDate.now());
        } else {
            CatAdoption catAdoption = (CatAdoption) adoption;
            reportList = catReportRepository.findByAdoptionAndDate(catAdoption, LocalDate.now());
        }
        if (!reportList.isEmpty()) {
            return reportList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Метод отправляет пользователю предупреждение о том, что
     * он не так подробно заполняет ежедневный отчет, как необходимо.
     * Предупреждение посылает волонтер через API.
     *
     * @param id идентификатор пользователя
     * @throws EntityNotFoundException если пользователь не найден.
     * @throws TelegramException       если отправка не состоялась.
     */
    public void warningToUser(long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " not found"));
        try {
            telegramBotUpdatesListener.sendMessageToUser(
                    user, "Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так подробно, как необходимо. " +
                            "Пожалуйста, подойди ответственнее к этому занятию. В противном случае волонтеры приюта будут обязаны " +
                            "самолично проверять условия содержания животного", 0);
        } catch (TelegramApiException e) {
            LOGGER.error("Ошибка при отправке сообщения " + e.getMessage());
            //TelegramException - это RunTimeException, в отличие от TelegramApiException
            throw new TelegramException();
        }
    }
}