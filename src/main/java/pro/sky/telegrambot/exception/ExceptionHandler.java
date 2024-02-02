package pro.sky.telegrambot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<?> handlerEntityNotFound(EntityNotFoundException e) {
        LOGGER.error("Entity by this Id not found. " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity by this Id not found. " + e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({TelegramException.class})
    public ResponseEntity<?> handlerTelegramError(TelegramException e) {
        LOGGER.error("TelegramError " + e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("TelegramError " + e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({UserOrAnimalIsBusyException.class})
    public ResponseEntity<?> UserOrPetIsBusyError(UserOrAnimalIsBusyException e) {
        LOGGER.error("User or pet already has a trial period. " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User or pet already has a trial period. " + e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({ShelterNotFoundException.class})
    public ResponseEntity<?> ShelterNotFoundError(ShelterNotFoundException e) {
        LOGGER.error("Shelter not found. " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Shelter not found. " + e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({MessageToVolunteerNotFoundException.class})
    public ResponseEntity<?> MessageToVolunteerNotFound(MessageToVolunteerNotFoundException e) {
        LOGGER.error("MessageToVolunteer not found. " + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MessageToVolunteer not found. " + e.getMessage());
    }
}
