package pro.sky.telegrambot.model.animal;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dog")
public class Dog extends Animal {

    public Dog() {
    }

    public Dog(Animal animal) {
        setId(animal.getId());
        setAnimalName(animal.getAnimalName());
        setBreed(animal.getBreed());
        setGender(animal.getGender());
        setAge(animal.getAge());
        setPhoto(animal.getPhoto());
        setAdopted(animal.isAdopted());
    }
}
