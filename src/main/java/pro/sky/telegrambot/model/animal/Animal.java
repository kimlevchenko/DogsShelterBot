package pro.sky.telegrambot.model.animal;

import pro.sky.telegrambot.model.entity.Shelter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String breed;
    private String animalName;
    private int age;
    private String gender;
    @ManyToOne
    @JoinColumn(name = "shelter")
    private Shelter shelters;
    private byte[] photo; // фото для каждого живoтного
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

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
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
        return id == animal.id && age == animal.age && adopted == animal.adopted &&
                Objects.equals(breed, animal.breed) && Objects.equals(animalName, animal.animalName)
                && Objects.equals(gender, animal.gender) && Arrays.equals(photo, animal.photo);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, breed, animalName, age, gender, adopted);
        result = 31 * result + Arrays.hashCode(photo);
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
                ", photo=" + Arrays.toString(photo) +
                ", adopted=" + adopted +
                '}';
    }
}
