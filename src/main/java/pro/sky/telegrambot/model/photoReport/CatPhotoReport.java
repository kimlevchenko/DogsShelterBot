package pro.sky.telegrambot.model.photoReport;

import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.animal.Animal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "cat_photoReport")
public class CatPhotoReport extends PhotoReport {
    @ManyToOne
    private CatAdoption adoption;
    @Override
    public CatAdoption getAdoption() {
        return adoption;
    }

    public CatPhotoReport(CatAdoption adoption, String filePath, Long fileSize, String mediaType,
                          LocalDate date, byte[] data, Animal animal, String text) {
            super(data, animal, text);
            this.adoption = adoption;
    }
    public CatPhotoReport() {
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
