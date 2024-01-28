package pro.sky.telegrambot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.telegrambot.model.adoption.CatAdoption;
import pro.sky.telegrambot.model.adoption.DogAdoption;
import pro.sky.telegrambot.model.animal.Cat;
import pro.sky.telegrambot.model.animal.Dog;
import pro.sky.telegrambot.model.entity.ShelterId;
import pro.sky.telegrambot.model.entity.User;
import pro.sky.telegrambot.model.report.CatReport;
import pro.sky.telegrambot.model.report.DogReport;
import pro.sky.telegrambot.repository.CatReportRepository;
import pro.sky.telegrambot.repository.DogReportRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    private DogReportRepository dogReportRepository;
    @Mock
    private CatReportRepository catReportRepository;
    @Mock
    private ShelterService shelterService;
    @InjectMocks
    private ReportService reportService;

    private DogAdoption adoption1;

    private CatAdoption adoption2;
    private User user1;

    private User user2;
    private Dog pet1;
    private Cat pet2;
    private LocalDate trialDate;
    private DogReport report1;
    private CatReport report2;
    private ShelterId shelterIdDog;
    private ShelterId shelterIdCat;


    @BeforeEach
    public void beforeEach() {
        shelterIdDog = ShelterId.DOG;
        shelterIdCat = ShelterId.CAT;
        user1 = new User();
        user1.setId(1L);
        user1.setName("Ivan");
        user1.setShelterId(shelterIdDog);
        user2 = new User();
        user2.setId(2L);
        user2.setName("Petr");
        user2.setShelterId(shelterIdCat);
        pet1 = new Dog();
        pet2 = new Cat();
        trialDate = LocalDate.now();
        adoption1 = new DogAdoption(user1, pet1, trialDate);
        adoption2 = new CatAdoption(user2, pet2, trialDate);
        report1 = new DogReport(adoption1, LocalDate.now(), null, "image/jpeg", 111L, null);
        report1.setId(1L);
        report2 = new CatReport(adoption2, LocalDate.now(), null, "image/jpeg", 111L, null);
        report2.setId(2L);
    }

    @Test
    public void saveDogReportTest() {
        byte[] photo = new byte[5];
        String text = "Пет здоров!";
        String mediaType = "image/jpeg";
        int mediaSize = 111;
        LocalDate date = LocalDate.now();
        //Пусть по заданному усыновлению и дате отчет уже есть
        List<DogReport> reportList = new ArrayList<>();
        reportList.add(report1);
        when(dogReportRepository.findByAdoptionAndDate(adoption1, date)).thenReturn(reportList);
        report1.setDate(date);
        report1.setText(text);
        when(dogReportRepository.save(report1)).thenReturn(report1);
        assertThat(reportService.saveReport(adoption1, date, photo, mediaType, (long) mediaSize, text)).isEqualTo(report1);
        verify(dogReportRepository, atLeast(1)).save(report1);
    }

    @Test
    public void saveDogReportWhenReportListIsEmptyTest() {
        LocalDate date = LocalDate.now();
        List<DogAdoption> adoptionList = new ArrayList<>();
        adoptionList.add(adoption1);

        List<DogReport> reportList = new ArrayList<>();
        when(dogReportRepository.findByAdoptionAndDate(adoption1, date)).thenReturn(reportList);
        DogReport newReport = new DogReport(adoption1, date, null, "image/jpeg", 111L, null);

        when(dogReportRepository.save(newReport)).thenReturn(newReport);
        assertThat(reportService.saveReport(adoption1, date, null, "image/jpeg", 111L, null)).isEqualTo(newReport);
        verify(dogReportRepository, atLeast(1)).save(newReport);
    }

    @Test
    public void saveCatReportTest() {
        byte[] photo = new byte[5];
        String text = "Пет здоров!";
        String mediaType = "image/jpeg";
        int mediaSize = 111;
        LocalDate date = LocalDate.now();
        List<CatAdoption> adoptionList = new ArrayList<>();
        adoptionList.add(adoption2);
        List<CatReport> reportList = new ArrayList<>();
        reportList.add(report2);
        when(catReportRepository.findByAdoptionAndDate(adoption2, date)).thenReturn(reportList);
        report2.setDate(date);
        report2.setText(text);
        when(catReportRepository.save(report2)).thenReturn(report2);
        assertThat(reportService.saveReport(adoption2, date, photo, mediaType, (long) mediaSize, text)).isEqualTo(report2);
        verify(catReportRepository, atLeast(1)).save(report2);
    }

    @Test
    public void saveCatReportWhenReportListIsEmptyTest() {
        LocalDate date = LocalDate.now();
        List<CatAdoption> adoptionList = new ArrayList<>();
        adoptionList.add(adoption2);
        List<CatReport> reportList = new ArrayList<>();
        when(catReportRepository.findByAdoptionAndDate(adoption2, date)).thenReturn(reportList);
        CatReport newReport = new CatReport(adoption2, date, null, "image/jpeg", 111L, null);
        when(catReportRepository.save(newReport)).thenReturn(newReport);

        assertThat(reportService.saveReport(adoption2, date, null, "image/jpeg", 111L, null)).isEqualTo(newReport);
        verify(catReportRepository, atLeast(1)).save(newReport);
    }

    @Test
    public void getReportByIdTest() {
        Mockito.doNothing().when(shelterService).checkShelterIdGender(shelterIdDog);
        when(dogReportRepository.findById(any())).thenReturn(Optional.of(report1));
        assertThat(reportService.getReportById(shelterIdDog, 1)).isEqualTo(report1);
        verify(dogReportRepository, atLeast(1)).findById(any());
    }

    @Test
    public void getReportByIdNegativeTest() {
        Mockito.doNothing().when(shelterService).checkShelterIdGender(shelterIdDog);
        when(dogReportRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> reportService.getReportById(shelterIdDog, 1));
        verify(dogReportRepository, atLeast(0)).findById(any());
    }

    @Test
    public void deleteReportByIdTest() {
        Mockito.doNothing().when(shelterService).checkShelterIdGender(shelterIdDog);
        when(dogReportRepository.findById(any())).thenReturn(Optional.of(report1));
        reportService.deleteReportById(shelterIdDog, 1);
        verify(dogReportRepository, atLeast(1)).deleteById(any());
    }

    @Test
    public void getAllDogReportsByDateTest() {
        LocalDate date = LocalDate.now();
        Mockito.doNothing().when(shelterService).checkShelterIdGender(shelterIdDog);
        List<DogReport> dogReportList = List.of(report1);
        when(dogReportRepository.findByDate(date)).thenReturn(dogReportList);
        assertThat(reportService.getAllReportsByDate(shelterIdDog, date))
                .isNotNull()
                .isNotEmpty();
        assertThat(reportService.getAllReportsByDate(shelterIdDog, date).size())
                .isEqualTo(dogReportList.size());
        assertThat(reportService.getAllReportsByDate(shelterIdDog, date))
                .containsExactlyInAnyOrder(report1);
    }

    @Test
    public void getAllCatReportsByDateTest() {
        LocalDate date = LocalDate.now();
        Mockito.doNothing().when(shelterService).checkShelterIdGender(shelterIdCat);
        List<CatReport> catReportList = List.of(report2);
        when(catReportRepository.findByDate(date)).thenReturn(catReportList);
        assertThat(reportService.getAllReportsByDate(shelterIdCat, date))
                .isNotNull()
                .isNotEmpty();
        assertThat(reportService.getAllReportsByDate(shelterIdCat, date).size())
                .isEqualTo(catReportList.size());
        assertThat(reportService.getAllReportsByDate(shelterIdCat, date))
                .containsExactlyInAnyOrder(report2);
    }

    @Test
    public void getAllReportsTest() {
        Mockito.doNothing().when(shelterService).checkShelterIdGender(shelterIdDog);
        List<DogReport> dogReportList = List.of(report1);
        when(dogReportRepository.findAll()).thenReturn(dogReportList);
        assertThat(reportService.getAllReports(shelterIdDog))
                .isNotNull()
                .isNotEmpty();
        assertThat(reportService.getAllReports(shelterIdDog).size())
                .isEqualTo(dogReportList.size());
        assertThat(reportService.getAllReports(shelterIdDog))
                .containsExactlyInAnyOrder(report1);
    }

}