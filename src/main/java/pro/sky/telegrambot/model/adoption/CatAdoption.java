package pro.sky.telegrambot.model.adoption;

import com.pengrad.telegrambot.model.User;
import pro.sky.telegrambot.model.adoption.Adoption;
import pro.sky.telegrambot.model.animal.Cat;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "cat_adoption")
public class CatAdoption extends Adoption {
    //Питомец. не animal_id, а целый Animal, чтобы возвратить в коллекции усыновлений и описание питомца тоже
    @ManyToOne
    private Cat animal;
    @Override
    public Cat getAnimal() {
        return animal;
    }
    public CatAdoption(User user, Cat animal, LocalDate date, LocalDate trialDate) {
        super(user, date, trialDate);
        this.animal = animal;
    }
    public CatAdoption() {
    }
    @Override
    public String toString() {
        return "CatAdoption{" +
                "id=" + getId() +
                ", user=" + getUser() +
                ", pet=" + animal +
                ", date=" + getDate() +
                ", trialDate=" + getTrialDate() +
                "}";
    }
}