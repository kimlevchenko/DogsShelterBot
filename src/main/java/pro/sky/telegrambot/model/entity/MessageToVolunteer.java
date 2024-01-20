package pro.sky.telegrambot.model.entity;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "message_to_volunteer")
public class MessageToVolunteer {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) возьмем от message_id от бота
    private int id; //возьмем от message_id от бота

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime questionTime;

    private String question;

    private LocalDateTime answerTime;

    private String answer;

    public int getId() {
        return id;
    }

    public void setId(int id) {  // нужен для тестов
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(LocalDateTime questionTime) {
        this.questionTime = questionTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(LocalDateTime answerTime) {
        this.answerTime = answerTime;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageToVolunteer that = (MessageToVolunteer) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
