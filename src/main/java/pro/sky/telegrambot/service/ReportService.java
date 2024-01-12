package pro.sky.telegrambot.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.telegrambot.exception.AnimalNotFindException;
import pro.sky.telegrambot.model.adoption.Adoption;
import pro.sky.telegrambot.model.report.DogReport;
import pro.sky.telegrambot.model.report.Report;
import pro.sky.telegrambot.model.ShelterId;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.repositories.AnimalRepository;
import pro.sky.telegrambot.repositories.DogReportRepository;
import pro.sky.telegrambot.repositories.UserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.logging.Logger;

@Service
    public class ReportService {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ReportService.class);
    private final String reportDir;
    private final AnimalRepository animalRepository;
    private final ShelterService shelterService;
    private final UserRepository userRepository;
    private final DogReportRepository dogReportRepository;

    public ReportService(AnimalRepository animalRepository, ShelterService shelterService,
                         UserRepository userRepository, DogReportRepository reportRepository,
                         @Value("${reportDir}")String reportDir) {
        this.animalRepository=animalRepository;
        this.shelterService=shelterService;
        this.userRepository=userRepository;
        this.dogReportRepository = reportRepository;
        this.reportDir=reportDir;
        }

        public void upload(Long animalId, MultipartFile file, String text, DogReport dogReport) throws IOException {
            var animal = animalRepository.findById(animalId) // загрузить фото и описaние животного
                    .orElseThrow(AnimalNotFindException::new);// если животное не удается
                                                                                      // найти, то выбрасывается исключение

            var dir = Path.of(reportDir);
            if (!dir.toFile().exists()) {
                Files.createDirectories(dir);
            }
            var path = saveFile(file, (Animal)animal);// создали папку с файлом

            Report report = (Report) dogReportRepository.findByAnimalId(animalId).orElse(new Report() {
                @Override
                public Adoption getAdoption() {
                    return null;
                }
            });
            report.setFilePath(path); //устанавливаем путь к файлу
            report.setData(file.getBytes()); //устанавливаем данные отчета с помощью setData. Данные берутся из переданного MultipartFile
            report.setFileSize(file.getSize());//устанавливаем размер файла с помощью setFileSize и медиа тип с помощью setMediaType
            report.setMediaType(file.getContentType());
            report.setAnimal((Animal) animal);
            dogReportRepository.save(dogReport);  // отчет сохраняется в репозиторий с помощью dogReportRepository.save()"((сохранение на диск(в базу данных)))"
            if (text != null) {
                report.setText(text);//сохраняем текст в отчет
            }
        }

        private String saveFile(MultipartFile file, Animal animal) throws RemoteException {
            var dotIndex = file.getOriginalFilename().lastIndexOf('.'); // индекс точки(.)
            var ext = file.getOriginalFilename().substring(dotIndex + 1);// получилми расширение файла(обрезали индекс строки до (.))
            var path = reportDir + "/" + animal.getId() + "_" + animal.getAnimalName()+"."+ext;// пишем путь к файлу
            try (var in = file.getInputStream();            //создаем поток, который читаем
                 var out = new FileOutputStream(path)) {    //создаем поток, который пишем
                in.transferTo(out);
            } catch (IOException e) {
                throw new RemoteException();                //что вложить(файл) в путь
            }
            return path;
        }

    public Report getReportById(ShelterId shelterId, Integer reportId) {
        return getReportById(shelterId,reportId);
    }

    public Collection<Report> getAllReport(ShelterId shelterId) {
        return getAllReport(shelterId);
    }

    public Collection<Report> getAllReportByDate(ShelterId shelterId, LocalDate date) {
        return getAllReportByDate(shelterId,date);
    }

    public void warningToUser(long id) {
    }
}
