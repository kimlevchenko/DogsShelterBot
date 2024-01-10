package pro.sky.telegrambot.model.adoption;

import com.pengrad.telegrambot.model.User;
import pro.sky.telegrambot.model.adoption.Adoption;
import pro.sky.telegrambot.model.animal.Dog;

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

    @Override
    public Dog getAnimal() {
        return animal;
    }

    public DogAdoption(User user, Dog animal, LocalDate date, LocalDate trialDate) {
        super(user, date, trialDate);
        this.animal = animal;
    }

    public DogAdoption() {
    }

    @Override
    public String toString() {
        return "DogAdoption{" +
                "id=" + getId() +
                ", user=" + getUser() +
                ", pet=" + animal +
                ", date=" + getDate() +
                ", trialDate=" + getTrialDate() +
                "}";
    }
}
