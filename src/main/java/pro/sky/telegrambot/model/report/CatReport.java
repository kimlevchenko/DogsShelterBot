package pro.sky.telegrambot.model.report;

import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.animal.Animal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "cat_photoReport")
public class CatReport extends Report {
    @ManyToOne
    private CatAdoption adoption;
    @Override
    public CatAdoption getAdoption() {
        return adoption;
    }

    public CatReport(Long id, String filePath, Long fileSize, String mediaType,
                     LocalDate date, byte[] data, Animal animal, String text,
                     CatAdoption adoption) {
        super(id, filePath, fileSize, mediaType, date, data, animal, text);
        this.adoption = adoption;
    }

    public CatReport(CatAdoption adoption) {
        this.adoption = adoption;
    }

    public CatReport() {
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
