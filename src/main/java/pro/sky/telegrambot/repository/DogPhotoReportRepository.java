package pro.sky.telegrambot.repository;

import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.photoReport.DogPhotoReport;
import pro.sky.telegrambot.model.photoReport.PhotoReport;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DogPhotoReportRepository extends JpaRepository<DogPhotoReport, Long> {
        Optional<PhotoReport> findByAnimalId(long id);// ищет по id животного

    //для волонтера
    List<DogPhotoReport> findByDate(LocalDate date);

    @Query(value = "SELECT * FROM dog_report where adoption_id = ?1 " +
            "and data is not null " +
            "and text is not null " +
            "order by date desc limit 1", nativeQuery = true)
    PhotoReport findLatestPhotoReport(Integer adoption_id);

}