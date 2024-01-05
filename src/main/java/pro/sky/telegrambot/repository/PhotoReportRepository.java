package pro.sky.telegrambot.repository;

import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.PhotoReport;

public interface PhotoReportRepository extends JpaRepository<PhotoReport, Long> {
        Optional<PhotoReport> findByAnimalId(long id);// ищет по id животного
    }
