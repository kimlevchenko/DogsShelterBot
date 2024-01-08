package pro.sky.telegrambot.model.photoReport;

import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.animal.Animal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "dog_photoReport")
public class DogPhotoReport extends PhotoReport {
    @ManyToOne
    private DogAdoption adoption;
    @Override
    public CatAdoption getAdoption() {
        return adoption;
    }

    public DogPhotoReport(DogAdoption adoption, String filePath, Long fileSize, String mediaType,
                          LocalDate date, byte[] data, Animal animal, String text) {
        super(data, animal, text);
        this.adoption = adoption;
    }
    public DogPhotoReport() {
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
