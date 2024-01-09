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
        setAnimalName(animal.getName());
        setBreed(animal.getBreed());
        setAge(animal.getAge());
    }
}