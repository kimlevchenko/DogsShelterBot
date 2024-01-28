package pro.sky.telegrambot.model.state;

/**
 * Именованные состояния.
 * Извлекаются из таблицы состояний из отдельной колонки named_state.
 * Используется, чтобы методы могли проверить,
 * не надо ли что-то сделать специальное при переходе в это состояние или в нем самом.
 * Все состояния текстового ввода обязательно имеют заполненное named_state,
 * т.к. требуют специального анализа ответа
 */
public enum NamedState {
    // Нужны для
    BAD_CHOICE,
    INITIAL_STATE,
    AFTER_SHELTER_CHOICE_STATE,
    REPORT,
    ANIMAL_BY_NUMBER,
    MESSAGE_TO_VOLUNTEER,
    FEEDBACK_REQUEST,
    ANIMAL_LIST
}
