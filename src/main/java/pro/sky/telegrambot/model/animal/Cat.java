package pro.sky.telegrambot.model.animal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cat")
public class Cat extends Animal {

    @Id
    private int id;

    public Cat() {
    }

    public Cat(Animal animal) {
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
