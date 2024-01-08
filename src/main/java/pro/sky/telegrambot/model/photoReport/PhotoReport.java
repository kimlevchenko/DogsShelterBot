package pro.sky.telegrambot.model.photoReport;

import pro.sky.telegrambot.model.animal.Animal;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "photoReport")
    public abstract class PhotoReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath; // где лежит файл
    private Long fileSize; // размер файла
    private String mediaType; // тип картинки, который будем передавать
    private LocalDate date; // дата отчета

    @Lob
    private byte[] data; // картинка, байт(массив)
    @OneToOne
    @JoinColumn(name= "animal")
    private Animal animal;
    private String text; // текст отчета

    public PhotoReport(byte[] data, Animal animal, String text) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.date = date;
        this.data = data;
        this.animal = animal;
        this.text = text;
    }
    public PhotoReport() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
        PhotoReport that = (PhotoReport) o;
        return Objects.equals(id, that.id) && Objects.equals(filePath, that.filePath) && Objects.equals(fileSize, that.fileSize) && Objects.equals(mediaType, that.mediaType) && Objects.equals(date, that.date) && Arrays.equals(data, that.data) && Objects.equals(animal, that.animal) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, filePath, fileSize, mediaType, date, animal, text);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "PhotoReport{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", mediaType='" + mediaType + '\'' +
                ", date=" + date +
                ", data=" + Arrays.toString(data) +
                ", animal=" + animal +
                ", text='" + text + '\'' +
                '}';
    }
}