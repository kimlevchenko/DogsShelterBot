package pro.sky.telegrambot.model.report;

import pro.sky.telegrambot.model.adoption.CatAdoption;

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
    public Object getAdoption() {
        return adoption;
    }

    public CatReport(CatAdoption adoption, LocalDate now, byte[] photo, String mediaType, Long mediaSize, String text) {
        super(mediaSize, mediaType, photo, text);
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

