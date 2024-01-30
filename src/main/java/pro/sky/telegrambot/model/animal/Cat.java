package pro.sky.telegrambot.model.animal;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cat")
public class Cat extends Animal {

    public Cat() {
    }

    public Cat(Animal animal) {
        setId(animal.getId());
        setAnimalName(animal.getAnimalName());
        setBreed(animal.getBreed());
        setGender(animal.getGender());
        setAge(animal.getAge());
        setPhoto(animal.getPhoto());
        setAdopted(animal.isAdopted());
    }
}
