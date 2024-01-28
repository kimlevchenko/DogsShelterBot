package pro.sky.telegrambot.model.report;

import pro.sky.telegrambot.model.adoption.DogAdoption;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "dog_report")
public class DogReport extends Report {
    @ManyToOne
    private DogAdoption adoption;

    public DogReport(DogAdoption adoption, LocalDate now, byte[] data, String mediaType, Long fileSize, String text) {
        super(fileSize, mediaType, data, text);
        this.adoption = adoption;
    }

    public DogReport() {

    }

    @Override
    public Object getAdoption() {
        return adoption;
    }

    @Override
    public String toString() {
        return "DogReport{" +
                "dogAdoption=" + adoption +
                '}';
    }
}
