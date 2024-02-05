package pro.sky.telegrambot.service;

import pro.sky.telegrambot.exception.TelegramException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.telegrambot.exception.UserOrAnimalIsBusyException;
import pro.sky.telegrambot.configuration.TelegramBotSender;
import pro.sky.telegrambot.model.adoption.Adoption;
import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.animal.Cat;
import pro.sky.telegrambot.model.animal.Dog;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Service
public class AdoptionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdoptionService.class);
    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final CatRepository catRepository;
    private final DogAdoptionRepository dogAdoptionRepository;
    private final CatAdoptionRepository catAdoptionRepository;
    private final ShelterService shelterService;
    private final TelegramBotSender telegramBotSender;

    public AdoptionService(
            UserRepository userRepository,
            DogRepository dogRepository,
            CatRepository catRepository,
            DogAdoptionRepository dogAdoptionRepository,
            CatAdoptionRepository catAdoptionRepository,
            ShelterService shelterService,
            TelegramBotSender telegramBotSender) {
        this.userRepository = userRepository;
        this.dogRepository = dogRepository;
        this.catRepository = catRepository;
        this.dogAdoptionRepository = dogAdoptionRepository;
        this.catAdoptionRepository = catAdoptionRepository;
        this.shelterService = shelterService;
        this.telegramBotSender = telegramBotSender;
    }

    //из такого репозитория удается прочитать, возвращается предок
    //но в него ничего не удается сохранить уже при компиляции, при подстановке любого типа возникает
    //method save in interface org.springframework.data.repository.CrudRepository<T,ID> cannot be applied to given types;
    private JpaRepository<? extends Adoption, Integer> adoptionRepository(ShelterId shelterId) {
        return (shelterId == ShelterId.DOG) ? dogAdoptionRepository : catAdoptionRepository;
    }

    /**
     * Метод создает объест усыновления и сохраняет данные в таблицу Adoption
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param shelterId идентификатор приюта.
     * @param userId    индификатор пользователя.
     * @param animalId  индификатор питомца
     * @param trialDate дата окончания испытательного срока
     *                  дата самого усыновления берется с часов компьютера
     * @return Adoption созданный объект усыновления
     * //  * @throws ShelterNotFoundException если приют не найден.
     * @throws EntityNotFoundException     если не найден пользователь или питомец
     * @throws UserOrAnimalIsBusyException если пользователь или питомец уже имеют испытательный срок.
     */
    public Adoption createAdoption(ShelterId shelterId, long userId, int animalId, LocalDate trialDate) {
        shelterService.checkShelterIdGender(shelterId);

        //Проверяем, что заданный User есть
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User with id " + userId + " not found"));
        Adoption adoption; //переменная для возврата
        if (shelterId == ShelterId.DOG) {
            //Проверяем, что заданный Dog есть
            //Dog animal = dogRepository.getReferenceById(petId);  //так не идет обращение к  БД. Оно будет позже
            Dog animal = dogRepository.findById(animalId).orElseThrow(() ->
                    new EntityNotFoundException("Dog with id " + animalId + " not found"));
            //Проверяем, что у пользователя нет другого испытательного срока
            if (!dogAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    user, trialDate, LocalDate.now()).isEmpty()) {
                throw new UserOrAnimalIsBusyException();
            }
            //Проверяем, что у собаки нет другого испытательного срока
            if (!dogAdoptionRepository.findByAnimalAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    animal, trialDate, LocalDate.now()).isEmpty()) {
                throw new UserOrAnimalIsBusyException();
            }
            DogAdoption dogAdoption = new DogAdoption(user, animal, trialDate);
            adoption = dogAdoptionRepository.save(dogAdoption);
        } else {
            Cat animal = catRepository.findById(animalId).orElseThrow(() ->
                    new EntityNotFoundException("Cat with id " + animalId + " not found"));
            if (!catAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    user, trialDate, LocalDate.now()).isEmpty()) {
                throw new UserOrAnimalIsBusyException();
            }
            if (!catAdoptionRepository.findByAnimalAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    animal, trialDate, LocalDate.now()).isEmpty()) {
                throw new UserOrAnimalIsBusyException();
            }
            CatAdoption catAdoption = new CatAdoption(user, animal, trialDate);
            adoption = catAdoptionRepository.save(catAdoption);
        }
        try {
            telegramBotSender.sendMessageToUser(adoption.getUser(),
                    adoption.getUser().getName() + ", поздравляем с усыновлением нашего питомца! " +
                            "Вам назначен испытательный срок до " + trialDate.toString(), 0);
        } catch (TelegramApiException e) {
            LOGGER.error("Ошибка при поздравлении об усыновлении " + e.getMessage());
        }
        return adoption;
    }

    /**
     * Метод позволяет получить информацию по усыновлению животного по Id
     *
     * @param shelterId  идентификатор приюта.
     * @param adoptionId индификатор усыновления.
     * @return Adoption  искомый объект усыновления
     * //* @throws ShelterNotFoundException если приют не найден.
     * @throws EntityNotFoundException если не найден id усыновления
     */
    public Adoption getAdoption(ShelterId shelterId, int adoptionId) {
        shelterService.checkShelterIdGender(shelterId);
        return adoptionRepository(shelterId).findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Adoption with id " + adoptionId + " for shelter " + shelterId + " not found"));
        //System.out.println(adoption.getClass());
    }

    /**
     * Метод устанавливает испытательный срок для питомца и пользователя
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param shelterId  идентификатор приюта.
     * @param adoptionId индификатор усыновления.
     * @return {@link Adoption} измененный объект
     * //* @throws ShelterNotFoundException если приют не найден.
     * @throws EntityNotFoundException если не найден id усыновления
     * @throws TelegramException       если не найден не состоялось уведомление пользователя
     */
    public Adoption setTrialDate(ShelterId shelterId, Integer adoptionId, LocalDate trialDate) throws TelegramException {
        shelterService.checkShelterIdGender(shelterId);

        Adoption adoption = adoptionRepository(shelterId).findById(adoptionId).orElseThrow(() -> new EntityNotFoundException(
                "Adoption with id " + adoptionId + " for shelter " + shelterId + " not found"));
        long days = ChronoUnit.DAYS.between(adoption.getTrialDate(), trialDate);
        try {
            telegramBotSender.sendMessageToUser(adoption.getUser(), "ВНИМАНИЕ !!! " +
                    "Вам увеличен испытательный срок на " + days + " дней до " + trialDate.toString(), 0);
        } catch (TelegramApiException e) {
            LOGGER.error("Ошибка при попытке изменить испытательный срок " + e.getMessage());
            //TelegramException - это RunTimeException, в отличие от TelegramApiException
            throw new TelegramException(); //при ошибке срок не меняем и не сохраняем
        }
        adoption.setTrialDate(trialDate);
        if (shelterId == ShelterId.DOG) {
            return dogAdoptionRepository.save((DogAdoption) adoption);
        } else {
            return catAdoptionRepository.save((CatAdoption) adoption);
        }
    }

    /**
     * Метод удаляет усыновление по ID
     * Используется метод репозитория {@link JpaRepository#delete(Object)}
     *
     * @param shelterId  идентификатор приюта.
     * @param adoptionId индификатор усыновления.
     * @return Adoption  удаленный объект
     * // * @throws ShelterNotFoundException если приют не найден.
     * @throws EntityNotFoundException если не найден id усыновления
     */
    public Adoption deleteAdoption(ShelterId shelterId, int adoptionId) {
        shelterService.checkShelterIdGender(shelterId);
        Adoption adoption = getAdoption(shelterId, adoptionId);
        adoptionRepository(shelterId).deleteById(adoptionId);
        return adoption;
    }

    /**
     * Метод возвращает список всех усыновлений по индификатору приюта
     * Используется метод репозитория {@link JpaRepository#findById(Object)}
     *
     * @param shelterId идентификатор приюта.
     * @return Collection<Adoption>
     * // * @throws ShelterNotFoundException если приют не найден.
     */
    public Collection<Adoption> getAllAdoptions(ShelterId shelterId) {
        shelterService.checkShelterIdGender(shelterId);
        return List.copyOf(adoptionRepository(shelterId).findAll());
    }

    /**
     * Метод возвращает список всех активных на сегодня усыновлений по индификатору приюта
     *
     * @param shelterId идентификатор приюта.
     * @return Collection<Adoption>
     * // * @throws ShelterNotFoundException если приют не найден.
     */
    public Collection<Adoption> getAllActiveAdoptions(ShelterId shelterId) {
        shelterService.checkShelterIdGender(shelterId);
        if (shelterId == ShelterId.DOG) {
            return List.copyOf(dogAdoptionRepository
                    .findByDateLessThanEqualAndTrialDateGreaterThanEqual(LocalDate.now(), LocalDate.now()));
        } else {
            return List.copyOf(catAdoptionRepository
                    .findByDateLessThanEqualAndTrialDateGreaterThanEqual(LocalDate.now(), LocalDate.now()));
        }
    }

    /**
     * Метод возвращает активное усыновление пользователя на заданную дату
     * При создании усыновления и при записи испытательного срока API следит,
     * чтобы активное усыновление у всех пользователей и у любого питомца
     * на любую дату в пределах одного приюта было только одно
     * Используется дальше для поиска отчета по усыновлению
     * или для разрешения входа в состояние сдачи отчета
     * или для поздравления пользователя, у которого вчера истек испытательный срок
     *
     * @param user пользователь, у которого ищется активное усыновление
     * @param date дата, на которую найденное усыновление было активно
     * @return Adoption найденное активное усыновление. null, если такое не найдено
     */
    public Adoption getActiveAdoption(User user, LocalDate date) {
        List<? extends Adoption> adoptionList;
        if (user.getShelterId() == ShelterId.DOG) {
            adoptionList = dogAdoptionRepository
                    .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(user, date, date);
        } else {
            adoptionList = catAdoptionRepository
                    .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(user, date, date);
        }
        if (adoptionList.isEmpty()) {
            return null;
        } else {
            return adoptionList.get(0);
        }
    }
    }



