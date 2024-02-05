package pro.sky.telegrambot.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.telegrambot.configuration.TelegramBotSender;
import pro.sky.telegrambot.model.adoption.AdoptionDTO;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.animal.Dog;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.repository.*;
import pro.sky.telegrambot.service.AdoptionService;
import pro.sky.telegrambot.service.ShelterService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdoptionController.class)
public class AdoptionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DogRepository dogRepository;

    @MockBean
    private CatRepository catRepository;

    @MockBean
    private ShelterRepository shelterRepository;

    @MockBean
    private DogAdoptionRepository dogAdoptionRepository;

    @MockBean
    private CatAdoptionRepository catAdoptionRepository;

    @SpyBean
    private AdoptionService adoptionService;

    @SpyBean
    private ShelterService shelterService;

    @MockBean
    private TelegramBotSender telegramBotSender;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    private Dog animal;

    private DogAdoption adoption;

    private LocalDate trialDate;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(123L);
        animal = new Dog();
        animal.setId(1);
        trialDate = LocalDate.of(2023, 9, 25);
        adoption = new DogAdoption(user, animal, trialDate);
        adoption.setId(1);
    }

    @Test
    public void createAdoptionTest() throws Exception {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(dogRepository.findById(animal.getId())).thenReturn(Optional.of(animal));
        when(dogAdoptionRepository
                .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user, trialDate, LocalDate.now())).thenReturn(new ArrayList<>());
        when(dogAdoptionRepository
                .findByAnimalAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        animal, trialDate, LocalDate.now())).thenReturn(new ArrayList<>());
        //в save в качестве аргумента придет посторонний adoption, поэтому any()
        when(dogAdoptionRepository.save(any())).thenReturn(adoption);

        AdoptionDTO adoptionDTO = new AdoptionDTO();
        adoptionDTO.setUserId(123L);
        adoptionDTO.setAnimalId(1);
        adoptionDTO.setTrialDate(LocalDate.of(2023, 9, 23));

        mockMvc.perform(post("/adoption/DOG")
                        //вариант без DTO post("/adoption/DOG?user_id=123&pet_id=1&trial_date=25.09.2023")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionDTO))
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                    //проверяем, что к нам вернулся объект, которым мы замокали репозиторий
                    assertThat(dogAdoption).isEqualTo(adoption);
                    //на вход save попадет объект c id = 0, не равный нашему возвращаемому adoption
                    verify(dogAdoptionRepository, atLeast(1)).save(any());
                    //надо бы проверить, что аргументы save соответствуют http-запросу
                    //assertThat(dogAdoption.getTrialDate()).isEqualTo(adoption.getTrialDate());
                });
        //нужны еще отрицательные тесты
    }

    @Test
    public void getAdoptionTest() throws Exception {
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));
        Mockito.doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        mockMvc.perform(
                        get("/adoption/DOG/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                    assertThat(dogAdoption).isEqualTo(adoption);
                });
        verify(dogAdoptionRepository, atLeast(1)).findById(any());
    }

    @Test
    public void setTrialDateTest() throws Exception {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        when(dogAdoptionRepository.findById(adoption.getId())).thenReturn(Optional.of(adoption));
        when(dogAdoptionRepository.save(adoption)).thenReturn(adoption);

        mockMvc.perform(
                        put("/adoption/DOG/1?trial_date=01.01.2024")
                        //.contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                    //к нам должен вернуться наш же adoption, который мы поручили вернуть dogAdoptionRepository.findById и save
                    assertThat(dogAdoption).isEqualTo(adoption);
                    //но с измененной датой
                    assertThat(dogAdoption.getTrialDate()).isEqualTo(LocalDate.of(2024, 1, 1));

                });
        //кроме того проверим, что был вызов save
        verify(dogAdoptionRepository, new Times(1)).save(adoption);
    }

    @Test
    public void deleteAdoption() throws Exception {
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        mockMvc.perform(
                        delete("/adoption/DOG/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                });
        verify(dogAdoptionRepository, new Times(1)).deleteById(any());
        reset(dogAdoptionRepository);
    }

    @Test
    public void getAllAdoptionsTest() throws Exception {
        doNothing().when(shelterService).checkShelterIdGender(ShelterId.DOG);
        List<DogAdoption> adoptions = new ArrayList<>();
        adoptions.add(adoption);
        when(dogAdoptionRepository.findAll()).thenReturn(adoptions);
        mockMvc.perform(
                        get("/adoption/DOG/all")
                        //.contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    List<DogAdoption> dogAdoptionList = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<DogAdoption>>() {
                            }
                    );
                    assertThat(dogAdoptionList).isNotNull().isNotEmpty();
                    assertThat(dogAdoptionList.size()).isEqualTo(adoptions.size());
                });
    }

}