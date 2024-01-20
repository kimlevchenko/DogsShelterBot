package pro.sky.telegrambot.model.report;

import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.animal.Animal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "dog_report")
public class DogReport extends Report {
    @ManyToOne
    private DogAdoption adoption;

    @Override
    public DogAdoption getAdoption() {
        return adoption;
    }

    public DogReport(Long id, String filePath, Long fileSize, String mediaType,
                     LocalDate date, byte[] data, Animal animal, String text, DogAdoption adoption) {
        super(id, filePath, fileSize, mediaType, date, data, animal, text);
        this.adoption = adoption;
    }

    public DogReport(DogAdoption adoption) {
        this.adoption = adoption;
    }

    public DogReport() {
    }

    @Override
    public String toString() {
        return "DogReport{" +
                "id=" + getId() +
                ", adoption=" + adoption +
                ", date=" + getDate() +
                "}";
    }
}
