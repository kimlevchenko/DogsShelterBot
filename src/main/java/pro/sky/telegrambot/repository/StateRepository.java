package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.state.NamedState;
import pro.sky.telegrambot.model.state.State;

public interface StateRepository extends JpaRepository<State, String> {
    //мы используем StateRepository
    //для получения состояния неправильного выбора из кнопок
    //для получения начального состояния для нового пользователя
    //для получения начального состояния после выбора приюта
    State findByNamedState(NamedState namedState);
}
