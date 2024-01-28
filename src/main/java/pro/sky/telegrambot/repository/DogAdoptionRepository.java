package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.model.entity.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DogAdoptionRepository extends JpaRepository<DogAdoption, Integer> {
    //поиск усыновлений пользователя с пересекающимся испытательным сроком,
    List<DogAdoption> findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
            //trialDate_parameter >= date_base AND date_parameter <= trialDate_base
            User user, LocalDate trialDate, LocalDate Date);

    //поиск усыновлений животного с пересекающимся испытательным сроком,
    List<DogAdoption> findByAnimalAndDateLessThanEqualAndTrialDateGreaterThanEqual(
            //trialDate_parameter >= date_base AND date_parameter <= trialDate_base
            Animal animal, LocalDate trialDate, LocalDate Date);

    //поиск активных усыновлений, действующих на дату с проверкой обех границ
    //от даты усыновления до конца испытательного срока (использовал Павел)
    List<DogAdoption> findByDateLessThanEqualAndTrialDateGreaterThanEqual(
            //date_parameter >= date_base AND date_parameter <= trialDate_base
            LocalDate date1, LocalDate date2);

    //поиск активных усыновлений, действующих на дату с проверкой только верхней границы
    //(использовал Александр)
    List<DogAdoption> findByTrialDateGreaterThanEqual(LocalDate date);

    //Поиск усыновлений, в которых на дату-параметр заканчивается испытательный срок.
    //Для поздравлений
    List<DogAdoption> findByTrialDate(LocalDate date);
}