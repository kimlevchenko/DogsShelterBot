package pro.sky.telegrambot.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.swing.plaf.nimbus.State;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    private long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private ShelterId shelterId;
//    @ManyToOne  //по умолчанию (fetch = FetchType.EAGER)
//    private State state;
//    @ManyToOne
//    private State previousState;
    private LocalDateTime stateTime;

    public User(long id, String name, State state) {  //для создания нового
        this.id = id;
        this.name = name;
//        this.state = state;
    }

    public User() {  //для JPA репозитория
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
//    @JsonIgnore
//    public State getState() {
//        return state;
//    }
//
//    public void setState(State state) {
//        this.state = state;
//    }
//
//    @JsonIgnore
//    public State getPreviousState() {
//        return previousState;
//    }
//
//    public void setPreviousState(State previousState) {
//        this.previousState = previousState;
//    }

    @JsonIgnore
    public ShelterId getShelterId() {
        return shelterId;
    }

    public void setShelterId(ShelterId shelterId) {
        this.shelterId = shelterId;
    }

    public LocalDateTime getStateTime() {
        return stateTime;
    }

    public void setStateTime() {
        this.stateTime = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}