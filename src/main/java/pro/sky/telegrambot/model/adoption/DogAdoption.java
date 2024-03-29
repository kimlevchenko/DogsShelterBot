package pro.sky.telegrambot.model.adoption;

import pro.sky.telegrambot.model.animal.Cat;
import pro.sky.telegrambot.model.animal.Dog;
import pro.sky.telegrambot.model.entity.User;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "dog_adoption")
public class DogAdoption extends Adoption {
    //Питомец. не animal_id, а целый Animal, чтобы возвратить в коллекции усыновлений и описание питомца тоже
    @ManyToOne
    private Dog animal;

    public DogAdoption() {

    }

    @Override
    public Dog getAnimal() {
        return animal;
    }

    public DogAdoption(User user, Dog animal, LocalDate trialDate) {
        super(user, trialDate);
        this.animal = animal;
    }

    @Override
    public String toString() {
        return "DogAdoption{" +
                "id=" + getId() +
                ", user=" + getUser() +
                ", animal=" + animal +
                ", date=" + getDate() +
                ", trialDate=" + getTrialDate() +
                "}";
    }
}

