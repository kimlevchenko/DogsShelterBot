package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.report.CatReport;

import java.time.LocalDate;
import java.util.List;

public interface CatReportRepository extends JpaRepository<CatReport, Integer> {
    List<CatReport> findByDate(LocalDate date);

    //для бота для оценки состояния сдачи отчета
    List<CatReport> findByAdoptionAndDate(CatAdoption catAdoption, LocalDate date);

    List<CatReport> findByDateAndDataIsNotNullAndTextIsNotNull(LocalDate date);

    @Query(value = "SELECT * FROM cat_report where adoption_id = ?1 " +
            "and data is not null " +
            "and text is not null " +
            "order by date desc limit 1", nativeQuery = true)
    CatReport findLatestReport(Integer adoption_id);

}