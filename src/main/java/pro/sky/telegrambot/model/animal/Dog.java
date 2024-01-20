package pro.sky.telegrambot.model.animal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dog")
public class Dog extends Animal {
    @Id
    private int id;

    public Dog() {
    }

    public Dog(Animal animal) {
        setId(animal.getId());
        setAnimalName(animal.getAnimalName());
        setBreed(animal.getBreed());
        setAge(animal.getAge());
        setData(animal.getData());
        setAdopted(animal.isAdopted());

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
