package pro.sky.telegrambot.model;


import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
    @Table(name = "animal_photoReport")
    public class AnimalPhotoReport extends PhotoReport {
        @ManyToOne
        private AnimalAdoption adoption;
        @Override
        public AnimalAdoption getAdoption() {
            return adoption;
        }

    public AnimalPhotoReport() {
        }
}
