package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.photoReport.DogReport;
import pro.sky.telegrambot.model.photoReport.Report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DogReportRepository extends JpaRepository<DogReport, Long> {
        Optional<Report> findByAnimalId(long id);// ищет по id животного
}