package pro.sky.telegrambot.model.adoption;

import java.time.LocalDate;

public class AdoptionDTO {
    private int Id;
    private Long userId;
    private int animalId;
    private LocalDate trialDate;

    public int getId() {
        return Id;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAnimalId() {
        return animalId;
    }

    public LocalDate getTrialDate() {
        return trialDate;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public void setTrialDate(LocalDate trialDate) {
        this.trialDate = trialDate;
    }
}
