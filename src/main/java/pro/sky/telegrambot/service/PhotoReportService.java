package pro.sky.telegrambot.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.sky.telegrambot.model.AnimalAdoption;
import pro.sky.telegrambot.model.PhotoReport;
import pro.sky.telegrambot.model.ShelterId;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.repository.AnimalRepository;
import pro.sky.telegrambot.repository.PhotoReportRepository;
import pro.sky.telegrambot.repository.UserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.logging.Logger;

@Service
    public class PhotoReportService {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PhotoReportService.class);
    private final String photoReportDir;
    private final AnimalRepository animalRepository;
    private final ShelterService shelterService;
    private final UserRepository userRepository;
    private final PhotoReportRepository photoReportRepository;

    public PhotoReportService(AnimalRepository animalRepository,ShelterService shelterService,
                              UserRepository userRepository, PhotoReportRepository photoReportRepository,
                             @Value("${photoReportDir}")String photoReportDir) {
        this.animalRepository=animalRepository;
        this.shelterService=shelterService;
        this.userRepository=userRepository;
        this.photoReportRepository = photoReportRepository;
        this.photoReportDir=photoReportDir;
        }

        public void upload(Long animalId, MultipartFile file, String text) throws IOException {
            var animal = animalRepository
                    .findById(animalId) // загрузить фото и описпние животного
                    .orElseThrow(); //.orElseThrow(AnimalNotFindException::new)!!!

            var dir = Path.of(photoReportDir);
            if (!dir.toFile().exists()) {
                Files.createDirectories(dir);
            }
            var path = saveFile(file, (Animal) animal);// создали папку с файлом

            PhotoReport photoReport = (PhotoReport) photoReportRepository.findByAnimalId(animalId).orElse(new PhotoReport() {
                @Override
                public AnimalAdoption getAdoption() {
                    return null;
                }
            });
            photoReport.setFilePath(path);
            photoReport.setData(file.getBytes());
            photoReport.setFileSize(file.getSize());
            photoReport.setMediaType(file.getContentType());
            photoReport.setAnimal((Animal) animal);
            photoReportRepository.save(photoReport);  // сохранение на диск(в базу данных)
            if (text != null) {
                photoReport.setText(text);//
            }
        }

        private String saveFile(MultipartFile file, Animal animal) throws RemoteException {
            var dotIndex = file.getOriginalFilename().lastIndexOf('.'); // индекс точки(.)
            var ext = file.getOriginalFilename().substring(dotIndex + 1);// получилми расширение файла(обрезали индекс строки до (.))
            var path = photoReportDir + "/" + animal.getId() + "_" + animal.getAnimalName()+"."+ext;// пишем путь к файлу
            try (var in = file.getInputStream();            //создаем поток, который читаем
                 var out = new FileOutputStream(path)) {    //создаем поток, который пишем
                in.transferTo(out);
            } catch (IOException e) {
                throw new RemoteException();                //что вложить(файл) в путь
            }
            return path;
        }

        public PhotoReport find(long animalId) {
            return (PhotoReport) photoReportRepository.findByAnimalId(animalId).orElse(null); //вывести отчет по животному, если такой есть
        }

    public PhotoReport getPhotoReportById(ShelterId shelterId, Integer photoReportId) {
        return getPhotoReportById(shelterId,photoReportId);
    }

    public Collection<PhotoReport> getAllPhotoReport(ShelterId shelterId) {
        return getAllPhotoReport(shelterId);
    }

    public Collection<PhotoReport> getAllPhotoReportByDate(ShelterId shelterId, LocalDate date) {
        return getAllPhotoReportByDate(shelterId,date);
    }

    public void warningToUser(long id) {
    }
}
