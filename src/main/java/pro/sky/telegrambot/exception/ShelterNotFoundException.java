package pro.sky.telegrambot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ShelterNotFoundException extends RuntimeException {
    private final String id;

    public ShelterNotFoundException(String id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Shelter with id: " + id + " is not found!";
    }
}

