package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.report.DogReport;
import pro.sky.telegrambot.model.report.Report;

import java.util.Optional;

@Repository
public interface DogReportRepository extends JpaRepository<DogReport, Long> {
        Optional<Report> findByAnimalId(long id);// ищет по id животного
}