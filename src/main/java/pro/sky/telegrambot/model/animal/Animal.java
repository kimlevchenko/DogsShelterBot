package pro.sky.telegrambot.model.animal;

import javax.persistence.*;
import java.util.Objects;
@Entity
@Table(name = "animal")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String breed;
    private String animalName;
    private int age;
    private String gender;


    public Animal(long id, String breed, String animalName, int age, String gender) {
        this.id = id;
        this.breed = breed;
        this.animalName = animalName;
        this.age = age;
        this.gender = gender;

    }

    public Animal() {
    }

    public long getId() {
        return id;
    }

    public String getBreed() {
        return breed;
    }

    public String getName() {
        return animalName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return id == animal.id && age == animal.age && Objects.equals(breed, animal.breed) && Objects.equals(animalName, animal.animalName) && Objects.equals(gender, animal.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, breed, animalName, age, gender);
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", breed='" + breed + '\'' +
                ", name='" + animalName + '\'' +
                ", age=" + age +
                ", gender='" + gender +
                '}';
    }
}
