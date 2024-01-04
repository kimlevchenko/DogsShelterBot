package pro.sky.telegrambot.exception;

public class InformationTypeByShelterNotFoundException extends RuntimeException {
    private final String informationType;

    public InformationTypeByShelterNotFoundException(String informationType) {
        this.informationType = informationType;
    }

    @Override
    public String getMessage() {
        return "Field by Shelter: " + informationType + " is not found!";
    }
}
