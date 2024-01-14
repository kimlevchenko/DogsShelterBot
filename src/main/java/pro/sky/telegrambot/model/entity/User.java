package pro.sky.telegrambot.model.entity;

import javax.persistence.*;
import javax.swing.plaf.nimbus.State;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    private long id;
    private String name;
    //    @Enumerated(EnumType.STRING)
    private LocalDateTime stateTime;

    public User(long id, String name, State state) {
        this.id = id;
        this.name = name;
    }

    public User() {
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
        return id == user.id && Objects.equals(name, user.name) && Objects.equals(stateTime, user.stateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, stateTime);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}