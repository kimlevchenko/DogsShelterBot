package pro.sky.telegrambot.model.animal;

import pro.sky.telegrambot.model.Shelter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;
@Entity
@Table(name = "animal")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String breed;
    private String animalName;
    private int age;
    private String gender;
    @ManyToOne
    @JoinColumn(name = "shelter")  // для создания новой колонки
    private Shelter shelters;
    @Column(columnDefinition = "shelter")
    private byte[] data; // фото для каждого живoтного
    private boolean adopted; // статус животного

    public Animal() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Shelter getShelters() {
        return shelters;
    }

    public void setShelters(Shelter shelters) {
        this.shelters = shelters;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isAdopted() {
        return adopted;
    }

    public void setAdopted(boolean adopted) {
        this.adopted = adopted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id == animal.id && age == animal.age && adopted == animal.adopted && Objects.equals(breed, animal.breed) && Objects.equals(animalName, animal.animalName) && Objects.equals(gender, animal.gender) && Objects.equals(shelters, animal.shelters) && Arrays.equals(data, animal.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, breed, animalName, age, gender, shelters, adopted);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", breed='" + breed + '\'' +
                ", animalName='" + animalName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", shelters=" + shelters +
                ", data=" + Arrays.toString(data) +
                ", adopted=" + adopted +
                '}';
    }
}
