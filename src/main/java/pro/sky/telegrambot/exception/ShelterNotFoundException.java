package pro.sky.telegrambot.exception;

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

