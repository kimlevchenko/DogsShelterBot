package pro.sky.telegrambot.model.entity;

import java.io.Serializable;

public class StateButtonPK implements Serializable {
    private State state;
    private String caption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateButtonPK that)) return false;

        if (!state.equals(that.state)) return false;
        return caption.equals(that.caption);
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + caption.hashCode();
        return result;
    }
}
