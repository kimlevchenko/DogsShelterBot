package pro.sky.telegrambot.model.adoption;

import com.pengrad.telegrambot.model.User;
import pro.sky.telegrambot.model.animal.Animal;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@MappedSuperclass
public abstract class Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //Усыновитель. не user_id, а целый User, чтобы возвратить в коллекции усыновлений описание усыновителя тоже
    @ManyToOne
    private User user;
    //Дата усыновления. За один день один пользователь может усыновить только одно животное.
    private LocalDate date;
    //Дата окончания испытательного срока. Назначается волонтером.
    //В интервале от date до trialDate бот ждет отчеты
    //При отборе животного волонтер устанавливает trialDate в 01.01.2001
    private LocalDate trialDate;

    public Adoption() {
    }

    public Adoption(User user, LocalDate date, LocalDate trialDate) {
        this.user = user;
        this.date = date;
        this.trialDate = trialDate;
    }

    public abstract Animal getAnimal();

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    } //для тестов

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getTrialDate() {
        return trialDate;
    }

    public void setTrialDate(LocalDate trialDate) {
        this.trialDate = trialDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adoption that = (Adoption) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + id +
                ", user=" + user +
                ", date=" + date +
                ", trialDate=" + trialDate +
                '}';
    }
}