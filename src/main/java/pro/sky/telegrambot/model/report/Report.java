package pro.sky.telegrambot.model.report;

import pro.sky.telegrambot.model.animal.Animal;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "report")
public abstract class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath; // где лежит файл
    private Long mediaSize; // размер файла
    private String mediaType; // тип картинки, который будем передавать
    private LocalDate date; // дата отчета

    @Lob
    private byte[] photo; // картинка, байт(массив)
    @OneToOne
    @JoinColumn(name = "animal")
    private Animal animal;
    private String text; // текст отчета
    private boolean photoPresented;
    private boolean textPresented;

    public Report(Long id, String filePath,
                  byte[] photo, String text) {
        this.id = id;
        this.filePath = filePath;
        this.mediaSize = mediaSize;
        this.mediaType = mediaType;
        this.date = LocalDate.now();
        this.photo = photo;
        this.animal = animal;
        this.text = text;
    }

    public Report() {

    }

    public Long getId() {
        return id;
    }

    public abstract Object getAdoption();

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(Long mediaSize) {
        this.mediaSize = mediaSize;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Objects.equals(filePath, report.filePath) && Objects.equals(mediaSize, report.mediaSize) && Objects.equals(mediaType, report.mediaType) && Objects.equals(date, report.date) && Arrays.equals(photo, report.photo) && Objects.equals(animal, report.animal) && Objects.equals(text, report.text);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filePath, mediaSize, mediaType, date, animal, text);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", mediaSize=" + mediaSize +
                ", mediaType='" + mediaType + '\'' +
                ", date=" + date +
                ", photo=" + Arrays.toString(photo) +
                ", animal=" + animal +
                ", text='" + text + '\'' +
                '}';
    }

    public boolean getPhotoPresented() {
        return photoPresented;
    }

    public void setPhotoPresented(boolean photoPresented) {
        this.photoPresented = photoPresented;
    }

    public boolean getTextPresented() {
        return textPresented;
    }

    public void setTextPresented(boolean textPresented) {
        this.textPresented = textPresented;
    }
}
