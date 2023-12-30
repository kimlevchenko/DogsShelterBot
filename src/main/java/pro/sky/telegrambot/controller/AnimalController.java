package pro.sky.telegrambot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.telegrambot.model.animal.Animal;
import pro.sky.telegrambot.service.AnimalService;

import java.util.Collection;

@RestController
@RequestMapping("/animal")
public class AnimalController {
    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
            this.animalService = animalService;
    }
    @GetMapping("{id}")
    public Animal getInfo(@PathVariable long id) {
        return animalService.findAnimal(id); //вывели
    }

    @PostMapping
    public Animal createAnimal(@RequestBody Animal animal) {
        return animalService.addAnimal(animal); //создали
    }
    @PutMapping
    public Animal editAnimal(@RequestBody Animal animal) {
        return animalService.editAnimal(animal);//заменили
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteStudent(@PathVariable Long id) {
        animalService.deletedAnimal(id);
        return ResponseEntity.ok().build();//удалили
    }
    @GetMapping("/byAge")
    public Collection<Animal> byAge(@RequestParam int age) {
        return animalService.filterByAge(age);
    }
    @GetMapping("/byAll")
    public Collection<String> byAll (){
        return animalService.filterAllAnimal();
    }
}
