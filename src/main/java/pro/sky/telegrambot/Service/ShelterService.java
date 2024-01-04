package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.InformationTypeByShelterNotFoundException;
import pro.sky.telegrambot.exception.ShelterNotFoundException;
import pro.sky.telegrambot.model.Shelter;
import pro.sky.telegrambot.model.ShelterId;
import pro.sky.telegrambot.repository.ShelterRepository;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * В классе ShelterService содержится бизнес логика для работы с информацией о приютах.
 */
@Service
public class ShelterService {

    /**
     * Лист для получения всех приютов из БД.
     */
    private final List<Shelter> shelters;

    private final ShelterRepository shelterRepository;

    /**
     * Если в таблице найдутся лишние, несоответствующие перечислению ключи,
     * то spring не сможет создать этот бин. Будет ошибка
     * No enum constant pro.sky.telegrambot.entities.ShelterId.aaa
     * Если в таблице будут отсутствовать некоторые элементы перечисления
     * то список shelters будет короче, но это не страшно,
     * т.к. у пользователя и кнопок для выбора приюта будет меньше
     */
    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
        this.shelters = shelterRepository.findAll();
    }

    /**
     * Создает новый объект Shelter и сохраняет его в БД.
     *
     * @param shelter Объект Shelter для сохранения.
     * @return Возвращает созданный Shelter после сохранения.
     * @throws IllegalArgumentException Если аргумент shelter равен null.
     */
    public Shelter create(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    /**
     * Извлекает объект Shelter по указанному идентификатору.
     *
     * @param id идентификатор Shelter для извлечения.
     * @return возвращает объект Shelter.
     * @throws ShelterNotFoundException Если Shelter с указанным идентификатором не найдено.
     */
    public Shelter get(ShelterId id) {
        return shelterRepository.findById(id).orElseThrow(() -> new ShelterNotFoundException(id.toString()));
    }

    /**
     * Обновляет данные Shelter с указанным идентификатором в БД.
     *
     * @param id              идентификатор Shelter, который нужно обновить.
     * @param informationType тип информации о приюте.
     * @param newInformation  новая информация.
     * @return возвращает обновленный объект Shelter.
     * @throws ShelterNotFoundException если объект Shelter с указанным идентификатором не найден.
     */
    public Shelter update(ShelterId id, String informationType, String newInformation) throws IllegalAccessException {
        Shelter shelter = setInformation(id, informationType, newInformation);
        shelterRepository.save(shelter);
        return shelter;
    }

    /**
     * Удаляет объект Shelter с указанным идентификатором из БД.
     *
     * @param id идентификатор объекта Shelter для удаления.
     * @throws ShelterNotFoundException Если объект Shelter с указанным идентификатором не найден.
     */
    public void delete(ShelterId id) {
        Optional<Shelter> shelterOptional = shelterRepository.findById(id);
        if (shelterOptional.isPresent()) {
            shelterRepository.deleteById(id);
        } else {
            throw new ShelterNotFoundException(id.toString());
        }
    }

    /**
     * Извлекает список всех объектов Shelter из БД.
     *
     * @return возвращает список всех объектов Shelter.
     */
    public List<Shelter> findAll() {
        return List.copyOf(shelterRepository.findAll());
    }

    /**
     * Получает необходимое значение поля из объекта Shelter, находящегося в листе shelters.
     *
     * @param id              идентификатор объекта Shelter.
     * @param informationType тип информации о приюте..
     * @return возвращает нужную информацию о приюте.
     * @throws IllegalAccessException выбрасывается если базовое поле не доступно.
     */
    public String getInformation(ShelterId id, String informationType)
            throws IllegalAccessException {
        Shelter shelter = null;
        for (Shelter myShelter : shelters) {
            if (myShelter.getId().equals(id)) {
                shelter = myShelter;
            }
        }

        if (shelter == null) {
            throw new ShelterNotFoundException(id.toString());
        }
        Field field = null;
        Field[] fields = shelter.getClass().getDeclaredFields();
        for (Field myField : fields) {
            if (myField.getName().equals(informationType)) {
                field = myField;
            }
        }
        if (field == null) {
            throw new InformationTypeByShelterNotFoundException(informationType);
        }
        field.setAccessible(true);
        return (String) field.get(shelter);
    }

    /**
     * Изменяет значение типа информации (поля) о приюте. Изменения записываются в лист shelters.
     *
     * @param id              идентификатор объекта Shelter.
     * @param informationType тип информации о приюте..
     * @param newInformation  новая информация.
     * @return возвращает обновленный объект Shelter.
     * @throws IllegalAccessException выбрасывается если базовое поле не доступно.
     */
    public Shelter setInformation(ShelterId id, String informationType, String newInformation)
            throws IllegalAccessException {
        Shelter shelter = null;
        for (Shelter myShelter : shelters) {
            if (myShelter.getId().equals(id)) {
                shelter = myShelter;
            }
        }
        if (shelter == null) {
            throw new ShelterNotFoundException(id.toString());
        }
        Field[] fields = shelter.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(informationType)) {
                field.setAccessible(true);
                field.set(shelter, newInformation);
            }
        }
        return shelter;
    }


    /**
     * Метод предназначен для проверки API запросов
     * Для такого невероятного случая,
     * когда пришел запрос с валидным элементом перечисления ShelterId,
     * но в базе такого приюта нет
     * Проверка была актуальна, когда ShelterId был String
     *
     * @param shelterId идентификатор объекта Shelter.
     * @throws ShelterNotFoundException выбрасывается если базовое поле не найдено.
     */

    public void checkShelterId(ShelterId shelterId) {
        if (shelterId == null) {
            throw new ShelterNotFoundException("NULL");
        }
        if (!shelters.stream().map(Shelter::getId).toList().contains(shelterId)) {
            throw new ShelterNotFoundException(shelterId.toString());
        }
    }

    public String getNameById(ShelterId shelterId) {
        return shelters.stream().filter(shelter -> shelter.getId() == shelterId).toList().get(0).getName();
    }

}
