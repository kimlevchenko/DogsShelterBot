package pro.sky.telegrambot.model.entity;

import javax.persistence.*;

@Entity
@IdClass(StateButtonPK.class)
public class StateButton {
    @Id
    @ManyToOne
    @JoinColumn(name = "state_id")  //можно и не писать
    private State state; //в работе не потребуется. Поле существует, только для hibernate
    @Id
    private String caption;
    @ManyToOne   //по умолчанию (fetch = FetchType.EAGER)
    @JoinColumn(name = "next_state_id") //можно и не писать
    private State nextState;
    @Column(name = "button_row") //имя колонки row запрещено в базе
    private byte row;
    @Column(name = "button_col")
    private byte col;
    @Enumerated(EnumType.STRING)
    private ShelterId shelterId;  //если пусто, то для любого приюта. если заполнено, то кнопка только для указанного

    public StateButton() {
    }

    //для тестов
    public StateButton(State state, String caption, State nextState, byte row, byte col, ShelterId shelterId) {
        this.state = state;
        this.caption = caption;
        this.nextState = nextState;
        this.row = row;
        this.col = col;
        this.shelterId = shelterId;
    }

    public State getState() {  //думаю, никогда не потребуется
        return state;
    }

    public String getCaption() {
        return caption;
    }

    public State getNextState() {
        return nextState;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ShelterId getShelterId() {
        return shelterId;
    }
}
