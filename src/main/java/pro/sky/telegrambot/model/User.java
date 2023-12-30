package pro.sky.telegrambot.model;

import javax.persistence.*;
import javax.swing.plaf.nimbus.State;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    private long id; //пока не придумали авторизацию, в качестве id пользователя используем id чата
    //когда будет авторизация, тогда введем отдельно поле chatId
    private String name;
    @Enumerated(EnumType.STRING)
    private ShelterId shelterId;
    @ManyToOne  //по умолчанию (fetch = FetchType.EAGER)
    private State state;
    @ManyToOne
    private State previousState;
    private LocalDateTime stateTime;

    public User(long id, String name, State state) {  //для создания нового
        this.id = id;
        this.name = name;
        this.state = state;
    }

    public User() {  //для JPA репозитория
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShelterId getShelterId() {
        return shelterId;
    }

    public void setShelterId(ShelterId shelterId) {
        this.shelterId = shelterId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    public LocalDateTime getStateTime() {
        return stateTime;
    }

    public void setStateTime(LocalDateTime stateTime) {
        this.stateTime = stateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(shelterId, user.shelterId) && Objects.equals(state, user.state) && Objects.equals(previousState, user.previousState) && Objects.equals(stateTime, user.stateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, shelterId, state, previousState, stateTime);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}