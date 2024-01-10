package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.photoReport.DogReport;
import pro.sky.telegrambot.model.photoReport.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DogPhotoReportRepository extends JpaRepository<DogReport, Long> {
        Optional<Report> findByAnimalId(long id);// ищет по id животного

    //для волонтера
    List<DogReport> findByDate(LocalDate date);

    @Query(value = "SELECT * FROM dog_report where adoption_id = ?1 " +
            "and data is not null " +
            "and text is not null " +
            "order by date desc limit 1", nativeQuery = true)
    Report findLatestPhotoReport(Integer adoption_id);

}