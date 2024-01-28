package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.report.DogReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DogReportRepository extends JpaRepository<DogReport, Integer> {
    //для волонтера
    List<DogReport> findByDate(LocalDate date);

    //для бота для оценки состояния сдачи отчета
    List<DogReport> findByAdoptionAndDate(DogAdoption dogAdoption, LocalDate date);

    List<DogReport> findByDateAndDataIsNotNullAndTextIsNotNull(LocalDate date);

    @Query(value = "SELECT * FROM dog_report where adoption_id = ?1 " +
            "and data is not null " +
            "and text is not null " +
            "order by date desc limit 1", nativeQuery = true)
    DogReport findLatestReport(Integer adoption_id);

}
